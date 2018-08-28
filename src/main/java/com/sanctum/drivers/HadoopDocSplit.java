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
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
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
            context.write(new Text("data_paths_store"), new Text(value.toString().hashCode() + ""));
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
            if (key.toString().equalsIgnoreCase("data_paths_store")) {
                for (Text val : values) {
                    mos.write(new Text(val.toString()), new Text("sanctum/index/tweet_" + val.toString() + "-m-00000"), "data_paths_store");
                }
            } else {
                result.set(key.toString());
                mos.write(result, new Text(), "tweet_" + key.toString().hashCode());
            }
        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            mos.close();
        }

    }

    public static DataPathStore pathStore = new DataPathStore();

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "doc split");
        job.setJarByClass(HadoopDocSplit.class);
        job.setMapperClass(DocSplitMapper.class);
        job.setCombinerClass(DocSplitReducer.class);
        job.setReducerClass(DocSplitReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job, new Path("sanctum/data"));
        FileOutputFormat.setOutputPath(job, new Path("sanctum/tweet_documents"));
        job.waitForCompletion(true);
    }
}
