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

import com.sanctum.ir.Configuration;
import com.sanctum.ir.DataLoader;
import com.sanctum.ir.ThreadedDataLoader;
import com.sanctum.ir.Tweet;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Naive threaded MapReduce implementation
 * @author Matt
 */
public class MapReducer {
    
    private ArrayList<Mapper> mappers;
    
    /**
     * Constructor
     */
    public MapReducer() {
        this.mappers = new ArrayList();
    }
    
    public void mapreduce(ThreadedDataLoader loader) {
        ArrayList<Tweet[]> data = loader.getLoadedData();
        
        for (int i = 0; i < data.size(); i++) {
            Mapper m = new Mapper(data.get(i));
            mappers.add(m);
            m.start();
        }
        
        while(!doneMapping()) {
            // wait   
        }
        
        ArrayList<HashMap> mappings = new ArrayList();
        for (Mapper m : mappers) {
            mappings.add(m.getPairs());
        }
        
        Reducer r = new Reducer(mappings, Configuration.get(Configuration.INDEX_SAVE_DIRECTORY));
        r.start();
    }
    
    private boolean doneMapping() {
        boolean done = true;
        
        for (Mapper m : this.mappers) {
            done &= m.done;
        }
        
        return done;
    }
}
