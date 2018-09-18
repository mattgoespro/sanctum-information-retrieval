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
import java.util.ArrayList;

/**
 * Class used to write the Indices
 * @author Matt
 */
public class IndexWriter extends Thread {
    
    private final ArrayList<String> keys;
    public volatile boolean done = false;
    
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
        
        for (String key : keys) {
            String f = key.toLowerCase().charAt(0) + "/";
            File letterIndex = new File("index/" + f);
            
            if(key.startsWith("hashtag_")) letterIndex = new File("index/hashtags/");
            else if(key.startsWith("mention_")) letterIndex = new File("index/mentions/");
            
            if (!letterIndex.exists()) {
                letterIndex.mkdir();
            }

            try {
                String keyDir = key.length() > 30 ? key.substring(0, 30) : key;
                String path = "index/" + keyDir.charAt(0) + "/";
                
                if(key.startsWith("hashtag_")) {
                    path = "index/hashtags/";
                } else if(key.startsWith("mention_")) {
                    path = "index/mentions/";
                }
                
                writer = new FileWriter(new File(path, keyDir + ".index"));
                
                for (String i : MapReducer.finalMap.get(key)) {
                    writer.write(i + "\n");
                }
                
                writer.close();
            } catch (IOException e) {}
        }
        
        done = true;
    }
}
