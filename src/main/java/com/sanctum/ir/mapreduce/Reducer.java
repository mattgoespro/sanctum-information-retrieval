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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class for reducing a list of keys and values to a single value
 *
 * @author Matt
 */
public class Reducer extends Thread {

    private final ArrayList<HashMap> mappings;
    private final HashMap<String, String> reducedPairs;
    private final String outFile;
    public volatile boolean done = false;

    /**
     * Constructor
     *
     * @param mappings
     * @param outFile
     */
    public Reducer(ArrayList<HashMap> mappings, String outFile) {
        this.mappings = mappings;
        this.reducedPairs = new HashMap();
        this.outFile = outFile;
    }

    @Override
    public void run() {
        for (HashMap m : mappings) {
            for (Object k : m.keySet()) {
                String key = (String) k;
                
                if (reducedPairs.containsKey(key.toLowerCase())) {
                    reducedPairs.put(key, reducedPairs.get(key) + "; " + m.get(k).toString());
                } else {
                    reducedPairs.put(key, m.get(k).toString());
                }
            }
        }
        
        done = true;
    }
    
    /**
     * Returns the result of the reduction.
     * @return HashMap
     */
    public HashMap<String, String> getReducedPairs() {
        return this.reducedPairs;
    }
}
