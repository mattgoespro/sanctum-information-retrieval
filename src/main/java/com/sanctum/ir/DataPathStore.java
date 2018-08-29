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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;

/**
 * Class to store the data file paths.
 *
 * @author Matt
 */
public class DataPathStore implements Serializable {
    
    public static long serialVersionUID = 4;
    
    public final HashMap<String, String> filePathStore;
    private final HashMap<String, String> inverseStore;

    /**
     * Constructor
     */
    public DataPathStore() {
        this.inverseStore = new HashMap();
        this.filePathStore = new HashMap();
    }

    /**
     * Inserts a path into the store.
     *
     * @param key
     */
    public void put(String key) {
        this.filePathStore.put(key.hashCode() + "", key);
        this.inverseStore.put(key, key.hashCode() + "");
    }

    /**
     * Returns the path for a given Integer key.
     *
     * @param key
     * @return String
     */
    public String get(String key) {
        return this.filePathStore.get(key);
    }

    /**
     * Returns an Integer key for a specific path value.
     *
     * @param value
     * @return Integer
     */
    public String getKey(String value) {
        return this.inverseStore.get(value);
    }

    /**
     * Writes the file path store to a file.
     *
     * @param fs
     */
    public void write(FileSystem fs) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(new File("data_path_store.data")))) {
            for (String path : inverseStore.keySet()) {
                // write Integer-String key
                writer.println(inverseStore.get(path) + " " + path);
                writer.flush();
            }
        } catch (IOException ex) {
            Logger.getLogger(ThreadedDataLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Loads the store containing the file path values.
     *
     * @param fs
     * @throws java.io.IOException
     */
    public void load(FileSystem fs) throws IOException {
        if (fs == null) {
            BufferedReader reader = new BufferedReader(new FileReader("data_path_store.data"));
            String line = reader.readLine();

            while (line != null) {
                String id = line.substring(0, line.indexOf(" "));
                String path = line.substring(line.indexOf(" ") + 1);
                this.filePathStore.put(id, path);
                this.inverseStore.put(path, id);
                line = reader.readLine();
            }
        } else {
            RemoteIterator<LocatedFileStatus> files = fs.listFiles(new Path("sanctum/tweet_documents"), true);
            
            while (files.hasNext()) {
                LocatedFileStatus lfs = files.next();
                
                if (lfs.getPath().getName().contains("data_paths_store")) {
                    FSDataInputStream store = fs.open(lfs.getPath());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(store));
                    String line = reader.readLine();

                    while (line != null) {
                        String[] idPath = line.split("\t");
                        String id = idPath[0];
                        String path = idPath[1];
                        this.filePathStore.put(id, path);
                        this.inverseStore.put(path, id);
                        line = reader.readLine();
                    }
                    
                    reader.close();
                }
            }
        }
    }

    /**
     * Returns the number of entries in the store.
     *
     * @return Integer
     */
    public int getSize() {
        return this.filePathStore.size();
    }
}