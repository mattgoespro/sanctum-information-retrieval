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
import java.util.LinkedList;
import java.util.Queue;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Naive threaded MapReduce implementation
 * @author Matt
 */
public class MapReducer {
    
    private final ArrayList<Mapper> mappers;
    private final ArrayList<Reducer> reducers;
    private final Queue<Mapper> mapperQueue;
    
    /**
     * Constructor
     */
    public MapReducer() {
        this.mappers = new ArrayList();
        this.reducers = new ArrayList();
        this.mapperQueue = new LinkedList();
    }
    
    /**
     * Perform MapReduce indexing on the data
     * @param loader 
     */
    public void mapreduce(ThreadedDataLoader loader) {
        ArrayList<Tweet[]> data = loader.getLoadedData();
        createMappers(data);
        
        while(!mappers.isEmpty() || !mapperQueue.isEmpty()) {
            // if all mappers done and 1 mapper in queue
            if(mappers.isEmpty() && mapperQueue.size() == 1) {
                ArrayList<HashMap> mappings = new ArrayList();
                mappings.add(mapperQueue.poll().getPairs());
                Reducer r = new Reducer(mappings, Configuration.INDEX_SAVE_DIRECTORY);
                reducers.add(r);
                r.start();
            }
            
            // if 2 mappers in queue, create reducer
            if(mapperQueue.size() >= 2) {
                ArrayList<HashMap> mappings = new ArrayList();
                mappings.add(mapperQueue.poll().getPairs());
                mappings.add(mapperQueue.poll().getPairs());
                Reducer r = new Reducer(mappings, Configuration.INDEX_SAVE_DIRECTORY);
                reducers.add(r);
                r.start();
            }
            
            checkMappers();
        }
    }
    
    /**
     * Creates the Mappers for the parsed data.
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
     * Checks if there is a mapper ready to be reduced and adds it to the finished-mapper queue.
     */
    private void checkMappers() {
        for (int i = 0; i < mappers.size(); i++) {
            if(mappers.get(i).done) {
                this.mapperQueue.add(mappers.remove(i));
                break;
            }
        }
    }
    
    /**
     * Merges all reducer results and writes them to an index.
     * @throws IOException 
     */
    public void merge() throws IOException {
        ArrayList<HashMap> mappings = new ArrayList();
        HashMap<String, String> finalMap = new HashMap();
        
        // wait for all reducers to finish
        while(!doneReducing()) {
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
                    finalMap.put(key, finalMap.get(key) + "; " + m.get(k).toString());
                } else {
                    finalMap.put(key, m.get(k).toString());
                }
            }
        }
        
        writeIndex(finalMap);
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
     * Writes the final word index.
     * @param finalMap
     * @throws IOException 
     */
    private void writeIndex(HashMap<String, String> finalMap) throws IOException {
        File indexFolder = new File(Configuration.INDEX_SAVE_DIRECTORY);
        indexFolder.mkdir();
        SortedSet<String> keys = new TreeSet<>(finalMap.keySet());
        PrintWriter writer;

        for (String key : keys) {
            String f = key.toLowerCase().charAt(0) + "/";
            File letterIndex = new File(Configuration.INDEX_SAVE_DIRECTORY + f);
            
            if(!letterIndex.exists()) letterIndex.mkdir();
            
            // file names can't contain colons
            key = key.replaceAll(":", "");
            writer = new PrintWriter(new BufferedWriter(new FileWriter(new File(Configuration.INDEX_SAVE_DIRECTORY + f + key.toLowerCase() + ".txt"))));

            writer.println(finalMap.get(key));
            writer.flush();
        }
    }
    
}
