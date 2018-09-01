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
import com.sanctum.ir.search.SearchIndex;
import com.sanctum.ir.TagFilter;
import com.sanctum.ir.Tweet;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class Search {

    public static DataPathStore pathStore = new DataPathStore();

    /**
     * Command-line search for a sequence of terms.
     *
     * @param args
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        if (args.length == 0 || args.length == 1 || args.length == 2) {
            System.out.println("Usage: hadoop jar <jar path> <classpath> <search HDFS?> <top k> [term 1] [term 2] [term 3 ] ...");
            return;
        }

        Configuration conf = new Configuration();
        boolean cfg = com.sanctum.ir.Configuration.loadConfiguration(null);

        if (cfg) {
            FileSystem fs = null;
            try {
                if (Boolean.parseBoolean(args[0])) {
                    conf.addResource(new Path(URI.create(com.sanctum.ir.Configuration.HADOOP_CONFIG_DIRECTORY + "core-site.xml")));
                    conf.addResource(new Path(URI.create(com.sanctum.ir.Configuration.HADOOP_CONFIG_DIRECTORY + "hdfs-site.xml")));
                    fs = FileSystem.get(URI.create(conf.get("fs.defaultFS")), conf);
                }
            } catch (Exception e) {
                System.out.println("Usage: hadoop jar <jar path> <classpath> <search HDFS?> <top k> [term 1] [term 2] [term 3 ] ...");
                return;
            }

            int k = 0;

            try {
                k = Integer.parseInt(args[1]);
            } catch (Exception e) {
                System.out.println("Usage: hadoop jar <jar path> <classpath> <search HDFS?> <top k> [term 1] [term 2] [term 3 ] ...");
                return;
            }

            long startTime = System.currentTimeMillis();
            pathStore.load(fs);
            TagFilter f = new TagFilter();
            f.loadBlacklist(fs);

            for (int i = 0; i < args.length; i++) {
                if (f.blacklists(args[i])) {
                    args[i] = null;
                }
            }
            
            String[] arguments = new String[args.length - 2];
            
            for (int i = 2; i < args.length; i++) {
                arguments[i-2] = args[i];
            }
            
            Collection<String> search = SearchIndex.search(fs, arguments, k);

            if (search != null) {
                writeSearchResults(fs, search);
            } else {
                System.out.println("No results found.");
            }
            System.out.println("Search complete (" + (System.currentTimeMillis() - startTime) / 100.0 + " sec)");
        } else {
            System.out.println("Unable to load config.");
        }
    }

    /**
     * Writes the results of a search to a file.
     *
     * @param fs
     * @param results
     * @throws IOException
     */
    private static void writeSearchResults(FileSystem fs, Collection<String> results) throws IOException {
        if (fs != null) {
            try (FSDataOutputStream writer = fs.create(new Path("sanctum/search_results"))) {
                for (String result : results) {
                    Tweet t = new Tweet("", result);
                    t.filter();
                    writer.writeBytes(t.toString() + "\n");
                }
            }
        } else {
            File f = new File("search_results");
            f.mkdir();
            
            try (FileWriter writer = new FileWriter("search_results/search")) {
                for (String result : results) {
                    Tweet t = new Tweet("", result);
                    t.filter();
                    writer.write(t.toString() + "\n");
                }
            }
        }
    }
}
