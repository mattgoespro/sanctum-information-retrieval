/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sanctum.ir;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Base64;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

/**
 * Class to store the data file paths.
 *
 * @author Matt
 */
public class DataPathStore {
    
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
        this.filePathStore.put(this.filePathStore.size() + "", key);
        this.inverseStore.put(key, this.filePathStore.size() + "");
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
        if (fs == null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(new File("data_path_store.data")))) {
                for (String path : inverseStore.keySet()) {
                    // write Integer-String key
                    writer.println(inverseStore.get(path) + " " + path);
                    writer.flush();
                }
            } catch (IOException ex) {
                Logger.getLogger(ThreadedDataLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                FSDataOutputStream writer = fs.create(new Path("/sanctum/data_path_store.data"));
                System.out.println(inverseStore.size() + ": " + inverseStore.keySet());
                for (String path : inverseStore.keySet()) {
                    System.out.println("Writing " + path);
                    // write Integer-String key
                    writer.writeBytes(inverseStore.get(path) + " " + path + "\n");
                }
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(DataPathStore.class.getName()).log(Level.SEVERE, null, ex);
            }
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
            FSDataInputStream store = fs.open(new Path("/sanctum/data_path_store.data"));
            LineIterator lineIterator = IOUtils.lineIterator(store, "UTF-8");
            String line;

            while (lineIterator.hasNext()) {
                line = lineIterator.nextLine();
                String id = line.substring(0, line.indexOf(" "));
                String path = line.substring(line.indexOf(" ") + 1);
                this.filePathStore.put(id, path);
                this.inverseStore.put(path, id);
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
