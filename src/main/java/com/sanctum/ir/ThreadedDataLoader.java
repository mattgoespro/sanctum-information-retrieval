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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Multithreaded DataLoader class
 *
 * @author Matt
 */
public class ThreadedDataLoader extends DataLoader {

    private final int threadsPerFile;
    private final ArrayList<TweetLoaderThread> threads;

    /**
     * Constructor
     *
     * @param threadsPerFile
     */
    public ThreadedDataLoader(int threadsPerFile) {
        super();
        this.threads = new ArrayList();
        this.threadsPerFile = threadsPerFile;
        
        File dataPaths = new File("data_path_store.data");
        
        if(dataPaths.exists()) {
            loadFilePathStore();
        }
    }

    @Override
    public void loadData() {
        System.out.println("Loading data from " + Configuration.DATA_DIRECTORY);
        long startTime = System.currentTimeMillis();
        File dataFiles = new File(Configuration.DATA_DIRECTORY);

        if (!dataFiles.exists()) {
            System.out.println("Error: Unable to find directory " + Configuration.DATA_DIRECTORY + ".");
            return;
        }

        ArrayList<String> filePaths = new ArrayList();
        getFiles(dataFiles, filePaths, new TweetFileFilter());

        if (filePaths.isEmpty()) {
            System.out.println("Error: File paths could not be found.");
            return;
        }

        System.out.println("Writing data paths...");
        File dataPaths = new File("data_path_store.data");

        if (!dataPaths.exists()) {
            try {
                // start threads
                try (PrintWriter writer = new PrintWriter(new FileWriter(new File("data_path_store.data")))) {
                    for (String path : filePaths) {
                        // write Integer-String key
                        writer.println(inverseStore.get(path) + " " + path);
                        writer.flush();
                        
                        try {
                            int numLines = (int) Math.ceil(TweetLoader.fileSize(path));
                            
                            // skip empty files
                            if (numLines == 0) {
                                continue;
                            }
                            
                            int tweetsPerThread = (int) Math.ceil((double) numLines / (double) this.threadsPerFile);
                            
                            // ensure there are always more tweets than threads
                            if (tweetsPerThread >= 1) {
                                for (int i = 0; i < this.threadsPerFile; i++) {
                                    TweetLoaderThread t = new TweetLoaderThread(path, i, tweetsPerThread);
                                    this.threads.add(t);
                                    t.start();
                                }
                            } else {
                                System.out.println("Error: You can't have that many threads.");
                                return;
                            }
                            
                        } catch (IOException ex) {
                            Logger.getLogger(ThreadedDataLoader.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    writer.close();
                }
                
                while (!allDone()) {
                    // do nothing
                }

                System.out.println("Loading successful (" + (System.currentTimeMillis() - startTime) / 1000.0 + " sec)");
            } catch (IOException ex) {
                Logger.getLogger(ThreadedDataLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
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

    /**
     * Checks if all threads have completed their tasks.
     *
     * @return boolean
     */
    private boolean allDone() {
        boolean done = true;
        for (TweetLoaderThread t : this.threads) {
            done &= t.done;
        }

        return done;
    }

    /**
     * Returns all loaded Tweets.
     *
     * @return
     */
    public ArrayList<Tweet[]> getLoadedData() {
        ArrayList<Tweet[]> data = new ArrayList();

        for (TweetLoaderThread thread : this.threads) {
            data.add(thread.getLoader().getTweets());
        }

        return data;
    }

    /**
     * Test method. Writes all filtered Tweets to a file.
     *
     * @param fileName
     * @throws IOException
     */
    public void writeTweets(String fileName) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(new File(fileName)))) {
            for (Tweet[] tweets : getLoadedData()) {
                for (int i = 0; i < tweets.length; i++) {
                    if (tweets[i] != null) {
                        writer.println(tweets[i].toString());
                        writer.flush();
                    }
                }
            }
            writer.close();
        }
    }
}
