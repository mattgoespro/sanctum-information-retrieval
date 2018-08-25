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
package com.sanctum.ir.mapreduce;

import com.sanctum.ir.ThreadedDataLoader;
import com.sanctum.ir.Tweet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class for mapping keys to values
 *
 * @author Matt
 */
public class Mapper extends Thread {

    public volatile boolean done = false;
    private final Tweet[] tweets;
    private final HashMap<String, ArrayList<Integer>> pairs;

    /**
     * Constructor
     *
     * @param tweets
     */
    public Mapper(Tweet[] tweets) {
        this.tweets = tweets;
        this.pairs = new HashMap();
    }

    @Override
    public void run() {
        for (Tweet t : this.tweets) {
            if (t == null) {
                continue;
            }

            ArrayList<String> words = t.getWords();

            if (words == null) {
                continue;
            }

            for (String k : words) {
                String key = (String) k;
                key = key.toLowerCase();
                
                if (pairs.containsKey(key)) {
                    pairs.get(key).add(ThreadedDataLoader.pathStore.getKey(t.getContainingFileName()));
                } else {
                    ArrayList<Integer> values = new ArrayList();
                    values.add(ThreadedDataLoader.pathStore.getKey(t.getContainingFileName()));
                    pairs.put(key, values);
                }
            }
        }

        MapReducer.mapperQueue.add(this);
        MapReducer.mappers.remove(this);
    }

    /**
     * Returns the word and containing file pair keys.
     *
     * @return HashMap
     */
    public HashMap getPairs() {
        return this.pairs;
    }

}
