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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Loads the IR configuration.
 * @author Matt
 */
public class Configuration {
    
    private static final String KEY_DATA_DIRECTORY = "Twitter data directory", 
            KEY_INDEXING_INCLUDE_HASHTAGS = "Include hashtags", 
            KEY_INDEXING_INCLUDE_MENTIONS = "Include mentions",
            KEY_INDEXING_INCLUDE_LINKS = "Include links", 
            KEY_INDEX_SAVE_DIRECTORY = "Inverted file save directory", 
            KEY_FILESYSTEM_ROOT = "Filesystem root";
    public static String DATA_DIRECTORY, 
            INDEXING_INCLUDE_HASHTAGS, 
            INDEXING_INCLUDE_MENTIONS,
            INDEXING_INCLUDE_LINKS, 
            INDEX_SAVE_DIRECTORY,
            FILESYSTEM_ROOT;
    
    /**
     * Loads the configuration. Returns true if successful.
     * @param fileName
     * @return boolean
     */
    public static boolean loadConfiguration(String fileName) {
        File conf = new File(fileName);
        
        if(conf.exists()) {
            try {
                Scanner scFile = new Scanner(conf);
                String line;
                
                while(scFile.hasNext()) {
                    line = scFile.nextLine();
                    if(line.startsWith("#") || line.startsWith(" ") || line.equals("") || line.startsWith("\n")) continue;
                    
                    String value = line.replaceAll("\\s+", "");
                    value = value.substring(value.indexOf(":") + 1);
                    
                    if(line.startsWith(Configuration.KEY_DATA_DIRECTORY)) Configuration.DATA_DIRECTORY = value;
                    else if(line.startsWith(Configuration.KEY_INDEXING_INCLUDE_HASHTAGS)) Configuration.INDEXING_INCLUDE_HASHTAGS = value;
                    else if(line.startsWith(Configuration.KEY_INDEXING_INCLUDE_MENTIONS)) Configuration.INDEXING_INCLUDE_MENTIONS = value;
                    else if(line.startsWith(Configuration.KEY_INDEXING_INCLUDE_LINKS)) Configuration.INDEXING_INCLUDE_LINKS = value;
                    else if(line.startsWith(Configuration.KEY_INDEX_SAVE_DIRECTORY)) Configuration.INDEX_SAVE_DIRECTORY = value;
                    else if(line.startsWith(Configuration.KEY_FILESYSTEM_ROOT)) Configuration.FILESYSTEM_ROOT = value;
                    else return false;
                }
                
                return true;
            } catch (FileNotFoundException ex) {
                return false;
            }
        }
        
        return false;
    }
}
