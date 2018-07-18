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
 * Multithreaded DataLoader class
 *
 * @author Matt
 */
public class ThreadedDataLoader extends DataLoader {

    private int threadsPerFile;
    private ArrayList<TweetLoaderThread> threads;

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
        System.out.println("Loading data from " + Configuration.get(Configuration.DATA_DIRECTORY));
        long startTime = System.currentTimeMillis();
        File dataFiles = new File(Configuration.get(Configuration.DATA_DIRECTORY));

        if (!dataFiles.exists()) {
            System.out.println("Error: Unable to find directory " + Configuration.get(Configuration.DATA_DIRECTORY) + ".");
            return;
        }

        ArrayList<String> filePaths = new ArrayList();
        getFiles(dataFiles, filePaths, new TweetFileFilter());

        if (filePaths.isEmpty()) {
            System.out.println("Error: File paths could not be found.");
            return;
        }

        // start threads
        for (String path : filePaths) {
            try {
                int numLines = (int) Math.ceil(TweetLoader.fileSize(path));
                for (int i = 0; i < this.threadsPerFile; i++) {
                    TweetLoaderThread t = new TweetLoaderThread(path, i, (int)Math.ceil(numLines / this.threadsPerFile));
                    this.threads.add(t);
                    t.start();
                }
            } catch (IOException ex) {
                Logger.getLogger(ThreadedDataLoader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        while(!allDone()) {
            // do nothing
        }
        
        System.out.println("Loading successful (" + (System.currentTimeMillis() - startTime) / 1000.0 + " sec)");
    }
    
    /**
     * Checks if all threads have completed their tasks.
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
