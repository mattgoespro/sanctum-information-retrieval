package com.sanctum.drivers;

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
import com.sanctum.ir.DataPathStore;
import java.io.IOException;
import java.net.URI;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

public class HadoopDocSplit {

    public static class DocSplitMapper extends Mapper<Object, Text, Text, Text> {

        private final Text tweet = new Text();

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
            tweet.set(value.toString());
            context.write(tweet, new Text());
        }
    }

    public static class DocSplitReducer extends Reducer<Text, Text, Text, Text> {

        private MultipleOutputs mos;
        private final Text result = new Text();

        @Override
        public void setup(Context context) {
            mos = new MultipleOutputs(context);
        }

        @Override
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            result.set(key.toString());
            pathStore.put("tweet_" + key.toString().hashCode());
            mos.write(result, new Text(), "tweet_" + key.toString().hashCode());
        }
    }
    
    public static DataPathStore pathStore = new DataPathStore();
    
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        conf.addResource(new Path("file:///etc/hadoop/conf/core-site.xml"));
        conf.addResource(new Path("file:///etc/hadoop/conf/hdfs-site.xml"));
        FileSystem fs = FileSystem.get(URI.create(conf.get("fs.defaultFS")), conf);
        Job job = Job.getInstance(conf, "doc split");
        job.setJarByClass(HadoopDocSplit.class);
        job.setMapperClass(DocSplitMapper.class);
        job.setCombinerClass(DocSplitReducer.class);
        job.setReducerClass(DocSplitReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job, new Path("/sanctum/data"));
        FileOutputFormat.setOutputPath(job, new Path("/sanctum/tweet_documents"));
        
        if(job.waitForCompletion(true)) {
            pathStore.write(fs);
        }
    }
}
