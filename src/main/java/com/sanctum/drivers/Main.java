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
        long startTime = System.currentTimeMillis();
        ThreadedDataLoader loader = new ThreadedDataLoader(10);
        boolean config = Configuration.loadConfiguration(null);
        MapReducer reducer = new MapReducer(2, 100);
        if (config) {
            loader.loadData();
            reducer.mapreduce(loader);
            reducer.merge();
            System.out.println(" (" + (System.currentTimeMillis() - startTime) / 1000.0 + " sec total)");
        } else {
            System.out.println("Failed to load config.");
        }
    }

}
