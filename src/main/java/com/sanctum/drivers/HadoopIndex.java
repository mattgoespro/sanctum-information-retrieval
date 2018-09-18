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

import com.sanctum.ir.TagFilter;
import java.io.IOException;
import java.net.URI;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class HadoopIndex {

    public static class TokenizerMapper extends Mapper<Object, Text, Text, Text> {

        /**
         *
         * @param key
         * @param value
         * @param context
         * @throws IOException
         * @throws InterruptedException
         */
        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String[] raw = value.toString().split(" ");
            String dir = "sanctum/tweet_documents/tweet_" + value.toString().hashCode();
            writeDocument(context, value);
            writeIndices(context, raw, dir);
        }
        
        /**
         * Writes a single document containing a single tweet.
         * @param context
         * @param value
         * @throws IOException 
         */
        private void writeDocument(Context context, Text value) throws IOException {
            FileSystem fs = FileSystem.get(URI.create(context.getConfiguration().get("fs.defaultFS")), context.getConfiguration());
            try (FSDataOutputStream tweetDoc = fs.create(new Path("sanctum/tweet_documents/tweet_" + value.toString().hashCode()))) {
                tweetDoc.writeBytes(value.toString());
            }
        }
        
        /**
         * Writes the indices to the context given the raw data from the file.
         * @param context
         * @param raw
         * @param dir
         * @throws IOException
         * @throws InterruptedException 
         */
        private void writeIndices(Context context, String[] raw, String dir) throws IOException, InterruptedException {
            for (String w : raw) {
                if (!w.startsWith("#")) {
                    w = w.replaceAll("\t", " ");
                    w = w.replaceAll("\\p{Punct}", " ");
                    String[] process = w.split(" ");

                    for (String s : process) {
                        if (!s.equals("") && !HadoopIndex.filter.blacklists(s)) {
                            context.write(new Text(s), new Text(dir));
                        }
                    }
                } else {
                    w = "#" + w.replaceAll("\\p{Punct}", "");
                    if (!w.equals("") && !HadoopIndex.filter.blacklists(w)) {
                        context.write(new Text(w), new Text(dir));
                    }
                }
            }
        }
    }

    public static class PathReducer extends Reducer<Text, Text, Text, Text> {

        @Override
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            if (key.toString().equalsIgnoreCase("part")) {
                return;
            }

            String keyDir = key.toString().length() > 30 ? key.toString().substring(0, 30) : key.toString();
            FileSystem fs = FileSystem.get(URI.create(context.getConfiguration().get("fs.defaultFS")), context.getConfiguration());
            String path = "sanctum/index/" + keyDir.charAt(0) + "/" + keyDir + ".index";
            
            if (keyDir.startsWith("#")) {
                keyDir = "hashtag_" + keyDir.substring(1);
                path = "sanctum/index/hashtags/" + keyDir + ".index";
            } else if(keyDir.startsWith("@")) {
                keyDir = "mention_" + keyDir.substring(1);
                path = "sanctum/index/mentions/" + keyDir + ".index";
            }
            
            try (FSDataOutputStream w = fs.create(new Path(path))) {
                for (Text val : values) {
                    w.writeBytes(val.toString() + "\n");
                }
            }
        }
    }

    public static TagFilter filter = new TagFilter();

    public static void main(String[] args) throws Exception {
        long startTime = System.currentTimeMillis();
        Configuration conf = new Configuration();
        conf.addResource(new Path("file:///etc/hadoop/conf/core-site.xml"));
        conf.addResource(new Path("file:///etc/hadoop/conf/hdfs-site.xml"));
        FileSystem fs = FileSystem.get(URI.create(conf.get("fs.defaultFS")), conf);

        boolean irConfig = com.sanctum.ir.Configuration.loadConfiguration();

        if (irConfig) {
            filter.loadBlacklist(null);
            Job job = Job.getInstance(conf, "word paths");
            job.setJarByClass(HadoopIndex.class);
            job.setMapperClass(TokenizerMapper.class);
            job.setCombinerClass(PathReducer.class);
            job.setReducerClass(PathReducer.class);
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(Text.class);
            FileInputFormat.addInputPath(job, new Path("sanctum/data"));
            FileOutputFormat.setOutputPath(job, new Path("sanctum/index"));
            fs.close();
            job.waitForCompletion(true);
            System.out.println("Job complete (" + (System.currentTimeMillis() - startTime) / 1000.0 + " sec)");
        } else {
            System.out.println("Unable to load config file. Either there is a syntax error in"
                    + " the config or 'config.cfg' could not be found. Make sure it is in the same"
                    + "directory as the jar.");
        }
    }
}
