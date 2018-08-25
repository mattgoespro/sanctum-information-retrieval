/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sanctum.ir;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class to store the data file paths.
 *
 * @author Matt
 */
public class DataPathStore {

    private final HashMap<Integer, String> filePathStore;
    private final HashMap<String, Integer> inverseStore;
    private int filePathID;

    /**
     * Constructor
     */
    public DataPathStore() {
        this.inverseStore = new HashMap();
        this.filePathStore = new HashMap();
        this.filePathID = 0;
    }

    /**
     * Inserts a path into the store.
     *
     * @param key
     */
    public void put(String key) {
        synchronized (ThreadedDataLoader.class) {
            this.filePathStore.put(filePathID, key);
            this.inverseStore.put(key, filePathID);
            ++this.filePathID;
        }
    }
    
    /**
     * Returns the path for a given Integer key.
     * @param key
     * @return String
     */
    public String get(Integer key) {
        return this.filePathStore.get(key);
    }
    
    /**
     * Returns an Integer key for a specific path value.
     * @param value
     * @return Integer
     */
    public Integer getKey(String value) {
        return this.inverseStore.get(value);
    }

    /**
     * Writes the file path store to a file.
     */
    public void write() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(new File("data_path_store.data")))) {
            for (String path : inverseStore.keySet()) {
                // write Integer-String key
                writer.println(inverseStore.get(path) + " " + path);
                writer.flush();
            }
            writer.close();

        } catch (IOException ex) {
            Logger.getLogger(ThreadedDataLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Loads the store containing the file path values.
     *
     * @throws java.io.IOException
     */
    public void load() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("data_path_store.data"));
        String line = reader.readLine();

        while (line != null) {
            int id = Integer.parseInt(line.substring(0, line.indexOf(" ")));
            String path = line.substring(line.indexOf(" ") + 1);
            this.filePathStore.put(id, path);
            this.inverseStore.put(path, id);
            line = reader.readLine();
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
