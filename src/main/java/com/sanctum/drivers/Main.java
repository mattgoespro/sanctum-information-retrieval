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
package com.sanctum.drivers;

import com.sanctum.ir.Configuration;
import com.sanctum.ir.ThreadedDataLoader;
import com.sanctum.ir.mapreduce.MapReducer;
import java.io.IOException;

/**
 *
 * @author Matt
 */
public class Main {
    
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        if (args.length == 0 || args.length == 1 || args.length == 2) {
            System.out.println("Usage: java -cp <jar path> <classpath> <num threads per file> <num mappers per reducer> <num writers>");
            return;
        }
        
        int numThreadsPerFile, mappersPerReducer, numWriters;
        
        try {
            numThreadsPerFile = Integer.parseInt(args[0]);
            mappersPerReducer = Integer.parseInt(args[1]);
            numWriters = Integer.parseInt(args[2]);
        } catch(Exception e) {
            System.out.println("Usage: java -cp <jar path> <classpath> <num threads per file> <num mappers per reducer> <num writers>");
            return;
        }
        
        long startTime = System.currentTimeMillis();
        ThreadedDataLoader loader = new ThreadedDataLoader(numThreadsPerFile);
        boolean config = Configuration.loadConfiguration();
        MapReducer reducer = new MapReducer(mappersPerReducer, numWriters);
        
        if (config) {
            loader.loadData();
            reducer.mapreduce(loader);
            reducer.merge();
            System.out.println(" (" + (System.currentTimeMillis() - startTime) / 1000.0 + " sec total)");
        } else {
            System.out.println("Unable to load config file. Either there is a syntax error in "
                    + "the config file or 'config.cfg' could not be found. Make sure it is in the same"
                    + "directory as the jar.");
        }
    }

}
