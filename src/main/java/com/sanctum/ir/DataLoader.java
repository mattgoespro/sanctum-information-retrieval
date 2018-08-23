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
 * Class for loading all Twitter data from the HDFS
 *
 * @author Matt
 */
public class DataLoader {

    protected ArrayList<TweetLoader> loaders;
    public static HashMap<Integer, String> filePathStore = new HashMap();
    public static HashMap<String, Integer> inverseStore = new HashMap();
    public static int filePathID = 0;

    /**
     * Constructor
     */
    public DataLoader() {
        this.loaders = new ArrayList();
        
        File pathStore = new File("data_path_store.data");
        
        if(pathStore.exists()) {
            loadFilePathStore();
        }
    }

    /**
     * Loads the tweet data from the local filesystem.
     */
    public void loadData() {
        System.out.println("Loading data from " + Configuration.DATA_DIRECTORY);
        long startTime = System.currentTimeMillis();
        File dataFiles = new File(Configuration.DATA_DIRECTORY);

        if (!dataFiles.exists()) {
            System.out.println("Error: Unable to find directory " + Configuration.DATA_DIRECTORY + ".");
            return;
        }

        ArrayList<String> filePaths = new ArrayList();
        getSourceFiles(dataFiles, filePaths);

        if (filePaths.isEmpty()) {
            System.out.println("Error: File paths could not be found.");
            return;
        }

        System.out.print("Writing data paths...");
        File dataPaths = new File("data_path_store.data");

        if (!dataPaths.exists()) {
            System.out.println("done. Using existing data paths.");
            writeFilePathStore(filePaths);
        }
        
        System.out.println("done");

        for (String filePath : filePaths) {
            TweetLoader l = new TweetLoader(filePath);
            try {
                l.readTweets();
                loaders.add(l);
            } catch (IOException ex) {
                Logger.getLogger(DataLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        System.out.println("Loading successful (" + (System.currentTimeMillis() - startTime) / 1000.0 + " sec)");

    }

    /**
     * Finds all Twitter file paths within a directory.
     *
     * @param root
     * @param paths
     */
    protected void getSourceFiles(File root, ArrayList<String> paths) {
        if (root.isDirectory()) {
            for (File child : root.listFiles()) {
                getSourceFiles(child, paths);
            }
        } else {
            if(!root.getName().equalsIgnoreCase("index.html"))
                paths.add(root.getAbsolutePath());
        }
    }

    /**
     * Writes the file path store to a file.
     *
     * @param filePaths
     */
    protected void writeFilePathStore(ArrayList<String> filePaths) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(new File("data_path_store.data")))) {
            for (String path : filePaths) {
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
     */
    private void loadFilePathStore() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("data_path_store.data"));
            String line = reader.readLine();

            while (line != null) {
                int id = Integer.parseInt(line.substring(0, line.indexOf(" ")));
                String path = line.substring(line.indexOf(" ") + 1);
                ThreadedDataLoader.filePathStore.put(id, path);
                ThreadedDataLoader.inverseStore.put(path, id);
                line = reader.readLine();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ThreadedDataLoader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ThreadedDataLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
