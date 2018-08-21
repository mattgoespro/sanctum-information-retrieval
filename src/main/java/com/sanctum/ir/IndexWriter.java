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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

/**
 *
 * @author Matt
 */
public class IndexWriter extends Thread {
    
    private HashMap<String, String> finalMap;
    
    /**
     * Constructor
     * @param tempMap 
     */
    public IndexWriter(HashMap<String, String> tempMap) {
        this.finalMap = new HashMap();
        
        for (String key : tempMap.keySet()) {
            finalMap.put(key, tempMap.get(key));
        }
    }
    
    @Override
    public void run() {
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
}
