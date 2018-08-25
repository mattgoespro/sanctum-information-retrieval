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
import java.util.ArrayList;

/**
 * Multithreaded DataLoader class
 *
 * @author Matt
 */
public class ThreadedDataLoader extends DataLoader {
    
    public static ArrayList<Tweet[]> data = new ArrayList();
    public static int COLLECTION_SIZE = 0;
    
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

        ArrayList<String> srcFilePaths = new ArrayList();
        getSourceFiles(dataFiles, srcFilePaths);

        if (srcFilePaths.isEmpty()) {
            System.out.println("Error: File paths could not be found.");
            return;
        }

        // create folder for tweet documents to be written to
        File f = new File("tweet_documents/");
        f.mkdir();
        
        // start threads
        for (String path : srcFilePaths) {

            int tweetsPerThread = (int) Math.ceil((double) 60000 / (double) this.threadsPerFile);

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

        }
        
        while (!allDone()) {
            // do nothing
        }
        
        System.out.print("Writing data paths...");
        File dataPaths = new File("data_path_store.data");
        
        if (!dataPaths.exists()) {
            System.out.println("done.");
            pathStore.write();
        } else {
            System.out.println("using existing data paths file.");
        }
        
        ThreadedDataLoader.COLLECTION_SIZE = pathStore.getSize();
        
        System.out.println("Loading successful (" + (System.currentTimeMillis() - startTime) / 1000.0 + " sec)");
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
    
}
