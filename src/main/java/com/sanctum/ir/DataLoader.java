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
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for loading all Twitter data from the HDFS
 *
 * @author Matt
 */
public class DataLoader {

    protected ArrayList<TweetLoader> loaders;
    public static DataPathStore pathStore;

    /**
     * Constructor
     */
    public DataLoader() {
        this.loaders = new ArrayList();
        DataLoader.pathStore = new DataPathStore();
        
        try {
            pathStore.load(null);
        } catch (IOException ex) {
            System.out.println("Unable to load file path store.");
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
            pathStore.write(null);
            System.out.println("done.");
        }else {
            System.out.println("using existing data paths.");
        }

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
}
