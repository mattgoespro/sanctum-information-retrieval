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
import java.io.IOException;
import java.net.URI;
import java.util.Scanner;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

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
     * @param hdfsRoot
     * @return boolean
     * @throws java.io.IOException
     */
    public static boolean loadConfiguration(String hdfsRoot) throws IOException {
        if(!hdfsRoot.startsWith("hdfs://")) {
            try {
                Scanner scFile = new Scanner(new File("config.cfg"));
                String line;
                
                while(scFile.hasNext()) {
                    line = scFile.nextLine();
                    if(line.startsWith("#") || line.startsWith(" ") || line.equals("") || line.startsWith("\n")) continue;
                    
                    String value = line.replaceAll("\\s+", "");
                    value = value.substring(value.indexOf(":") + 1);
                    
                    if(line.startsWith(com.sanctum.ir.Configuration.KEY_DATA_DIRECTORY)) com.sanctum.ir.Configuration.DATA_DIRECTORY = value;
                    else if(line.startsWith(com.sanctum.ir.Configuration.KEY_INDEXING_INCLUDE_HASHTAGS)) com.sanctum.ir.Configuration.INDEXING_INCLUDE_HASHTAGS = value;
                    else if(line.startsWith(com.sanctum.ir.Configuration.KEY_INDEXING_INCLUDE_MENTIONS)) com.sanctum.ir.Configuration.INDEXING_INCLUDE_MENTIONS = value;
                    else if(line.startsWith(com.sanctum.ir.Configuration.KEY_INDEXING_INCLUDE_LINKS)) com.sanctum.ir.Configuration.INDEXING_INCLUDE_LINKS = value;
                    else if(line.startsWith(com.sanctum.ir.Configuration.KEY_INDEX_SAVE_DIRECTORY)) com.sanctum.ir.Configuration.INDEX_SAVE_DIRECTORY = value;
                    else if(line.startsWith(com.sanctum.ir.Configuration.KEY_FILESYSTEM_ROOT)) com.sanctum.ir.Configuration.FILESYSTEM_ROOT = value;
                    else return false;
                }
                
                return true;
            } catch (FileNotFoundException ex) {
                return false;
            }
        } else {
            String uri = hdfsRoot + "/sanctum/config.cfg";
            FileSystem sys = FileSystem.get(URI.create(uri), new org.apache.hadoop.conf.Configuration());
            FSDataInputStream fs = sys.open(new Path(uri));
            LineIterator lineIterator = IOUtils.lineIterator(fs, "UTF-8");
            String line;
            while(lineIterator.hasNext()) {
                line = lineIterator.nextLine();

                if(line.startsWith("#") || line.startsWith(" ") || line.equals("") || line.startsWith("\n")) continue;
                    
                    String value = line.replaceAll("\\s+", "");
                    value = value.substring(value.indexOf(":") + 1);
                    
                    if(line.startsWith(com.sanctum.ir.Configuration.KEY_DATA_DIRECTORY)) com.sanctum.ir.Configuration.DATA_DIRECTORY = value;
                    else if(line.startsWith(com.sanctum.ir.Configuration.KEY_INDEXING_INCLUDE_HASHTAGS)) com.sanctum.ir.Configuration.INDEXING_INCLUDE_HASHTAGS = value;
                    else if(line.startsWith(com.sanctum.ir.Configuration.KEY_INDEXING_INCLUDE_MENTIONS)) com.sanctum.ir.Configuration.INDEXING_INCLUDE_MENTIONS = value;
                    else if(line.startsWith(com.sanctum.ir.Configuration.KEY_INDEXING_INCLUDE_LINKS)) com.sanctum.ir.Configuration.INDEXING_INCLUDE_LINKS = value;
                    else if(line.startsWith(com.sanctum.ir.Configuration.KEY_INDEX_SAVE_DIRECTORY)) com.sanctum.ir.Configuration.INDEX_SAVE_DIRECTORY = value;
                    else if(line.startsWith(com.sanctum.ir.Configuration.KEY_FILESYSTEM_ROOT)) com.sanctum.ir.Configuration.FILESYSTEM_ROOT = value;
                    else return false;
            }
            return true;
        }
    }
}
