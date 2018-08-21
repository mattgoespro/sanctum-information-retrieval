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
package com.sanctum.ir;

import com.sanctum.ir.mapreduce.MapReducer;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Class used to write the Indices
 * @author Matt
 */
public class IndexWriter extends Thread {
    
    private final ArrayList<String> keys;
    
    /**
     * Constructor 
     * @param keys
     */
    public IndexWriter(ArrayList<String> keys) {
        this.keys = new ArrayList(keys.size());
        
        for (String k : keys) {
            this.keys.add(k);
        }
    }
    
    @Override
    public void run() {
        FileWriter writer;
        System.out.println("Starting writer...");
        for (String key : keys) {
            String f = key.toLowerCase().charAt(0) + "/";
            File letterIndex = new File(Configuration.INDEX_SAVE_DIRECTORY + f);

            if (!letterIndex.exists()) {
                letterIndex.mkdir();
            }

            try {
                String keyDir = key.length() > 30 ? key.substring(0, 30) : key;
                writer = new FileWriter(new File(Configuration.INDEX_SAVE_DIRECTORY + f + keyDir.toLowerCase() + ".index"));
                writer.write(MapReducer.finalMap.get(key));
                writer.flush();
                writer.close();
            } catch (IOException e) {}
        }
        System.out.println("Writer complete.");
    }
}
