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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Matt
 */
public class TweetLoaderThread extends Thread {
    
    private final PartialTweetLoader loader;
    public boolean done;
    
    /**
     * Constructor
     * @param fileName
     * @param id
     * @param numTweets
     */
    public TweetLoaderThread(String fileName, int id, int numTweets) {
        this.loader = new PartialTweetLoader(fileName, id, numTweets);
        this.done = false;
    }
    
    @Override
    public void run() {
        try {
            this.loader.readTweets();
            this.done = true;
        } catch (IOException ex) {
            Logger.getLogger(TweetLoaderThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Returns the TweetLoader of this thread.
     * @return PartialTweetLoader
     */
    public PartialTweetLoader getLoader() {
        return this.loader;
    }
    
}
