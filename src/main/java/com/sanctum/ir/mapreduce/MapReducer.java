/*
 * Copyright (C) 2018 Matt
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.sanctum.ir.mapreduce;

import com.sanctum.ir.Configuration;
import com.sanctum.ir.ThreadedDataLoader;
import com.sanctum.ir.Tweet;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Naive threaded MapReduce implementation
 *
 * @author Matt
 */
public class MapReducer {

    public static BlockingQueue<Mapper> mappers;
    private final ArrayList<Reducer> reducers;
    public static BlockingQueue<Mapper> mapperQueue;
    private final int numMappers;
    private final int reducersPerMapper;
    private final int numWriters;

    /**
     * Constructor
     * @param numMappers
     * @param reducersPerMapper
     * @param numWriters
     */
    public MapReducer(int numMappers, int reducersPerMapper, int numWriters) {
        MapReducer.mappers = new LinkedBlockingQueue();
        this.reducers = new ArrayList();
        MapReducer.mapperQueue = new ArrayBlockingQueue(10000);
        this.numMappers = numMappers;
        this.reducersPerMapper = reducersPerMapper;
        this.numWriters = numWriters;
    }

    /**
     * Perform MapReduce indexing on the data
     *
     * @param loader
     */
    public void mapreduce(ThreadedDataLoader loader) {
        ArrayList<Tweet[]> data = loader.getLoadedData();
        createMappers(data);
        System.out.print("Started mapping and reducing...");
        
        while (!mappers.isEmpty() || !mapperQueue.isEmpty()) {
            // if all mappers done and 1 mapper in queue
            if (mappers.isEmpty() && mapperQueue.size() == 1) {
                ArrayList<HashMap> mappings = new ArrayList();
                mappings.add(mapperQueue.poll().getPairs());
                Reducer r = new Reducer(mappings, Configuration.INDEX_SAVE_DIRECTORY);
                reducers.add(r);
                r.start();
            }

            // if 2 mappers in queue, create reducer
            if (mapperQueue.size() >= 2) {
                ArrayList<HashMap> mappings = new ArrayList();
                mappings.add(mapperQueue.poll().getPairs());
                mappings.add(mapperQueue.poll().getPairs());
                Reducer r = new Reducer(mappings, Configuration.INDEX_SAVE_DIRECTORY);
                reducers.add(r);
                r.start();
            }
        }
        System.out.println("done.");
    }

    /**
     * Creates the Mappers for the parsed data.
     *
     * @param data
     */
    private void createMappers(ArrayList<Tweet[]> data) {
        for (int i = 0; i < data.size(); i++) {
            Mapper m = new Mapper(data.get(i));
            mappers.add(m);
            m.start();
        }
    }

    /**
     * Merges all reducer results and writes them to an index.
     *
     * @throws IOException
     */
    public void merge() throws IOException {
        System.out.print("Merging results...");
        ArrayList<HashMap> mappings = new ArrayList();
        HashMap<String, String> finalMap = new HashMap();

        // wait for all reducers to finish
        while (!doneReducing()) {
            // wait
        }

        // get all mappings
        for (Reducer r : this.reducers) {
            mappings.add(r.getReducedPairs());
        }

        // update mappings
        for (HashMap m : mappings) {
            for (Object k : m.keySet()) {
                String key = (String) k;
                
                if (finalMap.containsKey(key)) {
                    String hereVal = finalMap.get(key);
                    String togo = (String) m.get(k);
                    
                    if(hereVal.substring(0, hereVal.indexOf("(")).equalsIgnoreCase(togo.substring(0, togo.indexOf("(")))) {
                        String finalVal = hereVal.substring(0, hereVal.indexOf(")")) + ", " + togo.substring(togo.indexOf("(") + 1, togo.indexOf(")") + 1);
                        finalMap.put(key, finalVal);
                    } else {
                        finalMap.put(key, finalMap.get(key) + "; " + m.get(k).toString());
                    }
                } else {
                    finalMap.put(key, m.get(k).toString());
                }
            }
        }
        
        for(Object k : finalMap.keySet()) {
            String key = (String) k;
            setInverseDocumentFrequencies(finalMap, key);
        }
        
        System.out.println("done.");
        System.out.print("Writing index...");
        writeIndex(finalMap);
        System.out.println("done.");
        System.out.println("Indexing complete.");
    }

    /**
     * Checks if all reducers have finished processing.
     *
     * @return boolean
     */
    private boolean doneReducing() {
        boolean done = true;

        for (Reducer r : this.reducers) {
            done &= r.done;
        }
        return done;
    }

    /**
     * Writes the final word index.
     *
     * @param finalMap
     * @throws IOException
     */
    private void writeIndex(HashMap<String, String> finalMap) throws IOException {
        File indexFolder = new File(Configuration.INDEX_SAVE_DIRECTORY);
        indexFolder.mkdir();
        PrintWriter writer;

        for (String key : finalMap.keySet()) {
            String f = key.toLowerCase().charAt(0) + "/";
            File letterIndex = new File(Configuration.INDEX_SAVE_DIRECTORY + f);

            if (!letterIndex.exists()) {
                letterIndex.mkdir();
            }

            try {
                key = key.length() > 30 ? key.substring(0, 30) : key;
                writer = new PrintWriter(new BufferedWriter(new FileWriter(new File(Configuration.INDEX_SAVE_DIRECTORY + f + key.toLowerCase() + ".index"))));
                writer.println(finalMap.get(key));
                writer.flush();
            } catch (IOException e) {}

        }
    }
    
    /**
     * Updates the term frequencies for each document.
     * @param finalMap
     * @param key 
     */
    private void setInverseDocumentFrequencies(HashMap<String, String> finalMap, String key) {
        String[] docs = finalMap.get(key).split("; ");
        String idfDocs = "";
        
        for(String doc : docs) {
            double tf = doc.substring(doc.indexOf("(") + 1, doc.indexOf(")")).split(", ").length;
            double idf = Math.log(((double)ThreadedDataLoader.COLLECTION_SIZE) / tf);
            double tf_idf = tf * idf;
            idfDocs += doc + "[" + Math.round(tf_idf * 100.0)/100.0 + "]; ";
        }
        
        finalMap.put(key, idfDocs);
    }

}
