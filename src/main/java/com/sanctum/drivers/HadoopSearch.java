package com.sanctum.drivers;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
