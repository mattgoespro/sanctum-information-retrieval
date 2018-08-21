#!/bin/bash

hdfs dfs -mkdir /sanctum
hdfs dfs -mkdir /sanctum/data
hdfs dfs -mkdir /sanctum/output
hdfs dfs -mkdir /sanctum/output/searchresults
hdfs dfs -put sanctum_ir-master/twitter_sample/twitter/20091027/* /sanctum/data
hdfs dfs -put sanctum_ir-master/target/config.cfg /sanctum
hdfs dfs -put sanctum_ir-master/indexing_token_blacklist.cfg /sanctum
