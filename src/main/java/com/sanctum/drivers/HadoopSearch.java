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

import com.sanctum.ir.DataPathStore;
import com.sanctum.ir.SearchIndex;
import java.io.IOException;
import java.net.URI;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class HadoopSearch {
    
    public static DataPathStore pathStore = new DataPathStore();
    
    /**
     * Command-line search for a sequence of terms.
     * @param args 
     * @throws java.io.IOException 
     */
    public static void main(String[] args) throws IOException {
        Configuration conf = new Configuration();
        conf.addResource(new Path("file:///etc/hadoop/conf/core-site.xml"));
        conf.addResource(new Path("file:///etc/hadoop/conf/hdfs-site.xml"));
        FileSystem fs = FileSystem.get(URI.create(conf.get("fs.defaultFS")), conf);
        pathStore.load(fs);
        System.out.println(SearchIndex.search(fs, args));
    }
}
