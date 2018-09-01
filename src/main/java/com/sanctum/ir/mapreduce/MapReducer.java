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

import com.sanctum.ir.ThreadedDataLoader;
import com.sanctum.ir.Tweet;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
    private final ArrayList<IndexWriter> writers;
    public static BlockingQueue<Mapper> mapperQueue;
    public static HashMap<String, ArrayList<String>> finalMap = new HashMap();
    private final int mappersPerReducer;
    private final int numWriters;

    /**
     * Constructor
     *
     * @param mappersPerReducer
     * @param numWriters
     */
    public MapReducer(int mappersPerReducer, int numWriters) {
        MapReducer.mappers = new LinkedBlockingQueue();
        this.reducers = new ArrayList();
        this.writers = new ArrayList();
        MapReducer.mapperQueue = new ArrayBlockingQueue(100000000);
        this.mappersPerReducer = mappersPerReducer;
        this.numWriters = numWriters;
    }

    /**
     * Perform MapReduce indexing on the data
     *
     * @param loader
     */
    public void mapreduce(ThreadedDataLoader loader) {
        createMappers(ThreadedDataLoader.data);
        System.out.print("Started mapping and reducing...");
        long startTime = System.currentTimeMillis();

        while (!mappers.isEmpty() || !mapperQueue.isEmpty()) {
            if (mappers.isEmpty() && mapperQueue.size() < mappersPerReducer) {
                ArrayList<HashMap> mappings = new ArrayList();

                while (!mapperQueue.isEmpty()) {
                    mappings.add(mapperQueue.poll().getPairs());
                }

                Reducer r = new Reducer(mappings);
                reducers.add(r);
                r.start();
            }

            if (mapperQueue.size() >= mappersPerReducer) {
                ArrayList<HashMap> mappings = new ArrayList();

                for (int i = 0; i < mappersPerReducer; i++) {
                    mappings.add(mapperQueue.poll().getPairs());
                }

                Reducer r = new Reducer(mappings);
                reducers.add(r);
                r.start();
            }
        }
        System.out.println("done (" + (System.currentTimeMillis() - startTime) / 1000.0 + " sec)");
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
        long startTime = System.currentTimeMillis();
        ArrayList<HashMap> mappings = new ArrayList();

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
                key = key.toLowerCase();
                
                if (finalMap.containsKey(key)) {
                    finalMap.get(key).addAll((ArrayList<String>) m.get(k));
                } else {
                    finalMap.put(key, (ArrayList<String>) m.get(k));
                }
            }
        }

        System.out.println("done (" + (System.currentTimeMillis() - startTime) / 1000.0 + " sec)");
        System.out.print("Writing index...");
        startTime = System.currentTimeMillis();
        writeIndex();
        
        while(!doneWriting()) {
            // wait
        }
        
        System.out.println("done (" + (System.currentTimeMillis() - startTime) / 1000.0 + " sec)");
        System.out.print("Indexing complete");
    }

    /**
     * Checks if all reducers have finished processing.
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
     * Checks if all writers have finished writing.
     * @return boolean
     */
    private boolean doneWriting() {
        boolean done = true;
        
        for (IndexWriter r : this.writers) {
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
    private void writeIndex() throws IOException {
        File indexFolder = new File("index/");
        indexFolder.mkdir();

        int cCopy = finalMap.size() / numWriters, c = 0;
        ArrayList<String> keys = new ArrayList();
        
        for (String k : finalMap.keySet()) {
            if(c == cCopy) {
                c = 0;
                IndexWriter writer = new IndexWriter(keys);
                writer.start();
                writers.add(writer);
                keys = new ArrayList();
            }
            keys.add(k);
            c++;
        }
    }
}
