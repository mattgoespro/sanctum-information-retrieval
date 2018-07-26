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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for reducing a list of keys and values to a single value
 * @author Matt
 */
public class Reducer extends Thread {
    
    private ArrayList<HashMap> mappings;
    private HashMap<String, String> reducedPairs;
    private String outFile;
    
    /**
     * Constructor
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
                
                if(reducedPairs.containsKey(key)) {
                    reducedPairs.put(key, reducedPairs.get(key) + "; " + m.get(k).toString());
                } else {
                    reducedPairs.put(key, m.get(k).toString());
                }
            }
        }
        
        try {
            writeIndex();
        } catch (IOException ex) {
            Logger.getLogger(Reducer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void writeIndex() throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter(new File(this.outFile)));
        
        for (Object key : this.reducedPairs.keySet()) {
            writer.println(key.toString() + " " + reducedPairs.get(key));
            writer.flush();
        }
    }
}
