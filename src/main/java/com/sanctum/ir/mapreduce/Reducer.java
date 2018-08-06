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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

enum IndexType {
    SINGLE_FILE, DISTRIBUTED
}

/**
 * Class for reducing a list of keys and values to a single value
 *
 * @author Matt
 */
public class Reducer extends Thread {

    private final ArrayList<HashMap> mappings;
    private final HashMap<String, String> reducedPairs;
    private final String outFile;
    private final IndexType type;

    /**
     * Constructor
     *
     * @param mappings
     * @param type
     * @param outFile
     */
    public Reducer(ArrayList<HashMap> mappings, IndexType type, String outFile) {
        this.mappings = mappings;
        this.reducedPairs = new HashMap();
        this.outFile = outFile;
        this.type = type;
    }

    @Override
    public void run() {
        for (HashMap m : mappings) {
            for (Object k : m.keySet()) {
                String key = (String) k;

                if (reducedPairs.containsKey(key)) {
                    reducedPairs.put(key, reducedPairs.get(key) + "; " + m.get(k).toString());
                } else {
                    reducedPairs.put(key, m.get(k).toString());
                }
            }
        }

        switch (type) {
            case SINGLE_FILE: {
                try {
                    writeSingleIndex();
                } catch (IOException ex) {
                    Logger.getLogger(Reducer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            case DISTRIBUTED: {
                try {
                    writeDistributedIndex();
                } catch (IOException ex) {
                    Logger.getLogger(Reducer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * Writes the indices to a file.
     *
     * @throws IOException
     */
    private void writeSingleIndex() throws IOException {
        PrintWriter writer = new PrintWriter(new FileWriter(new File(this.outFile)));

        synchronized (this) {
            for (Object key : this.reducedPairs.keySet()) {
                writer.println(key.toString() + " " + reducedPairs.get(key));
                writer.flush();
            }
        }
    }

    private synchronized void writeDistributedIndex() throws IOException {
        File indexFolder = new File("index/");
        indexFolder.mkdir();
        SortedSet<String> keys = new TreeSet<>(reducedPairs.keySet());
        String prevKey = "";
        PrintWriter writer;

        for (String key : keys) {
            String fileIndex = key.toLowerCase().charAt(0) + "";
            File indexFile = new File("index/index_" + fileIndex + ".txt");

            writer = new PrintWriter(new BufferedWriter(new FileWriter(indexFile, true)));

            writer.println(key + " " + reducedPairs.get(key));
            writer.flush();
            prevKey = fileIndex;
        }
    }

}
