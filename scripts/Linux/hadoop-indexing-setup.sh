#!bin/bash
hdfs dfs -mkdir sanctum
hdfs dfs -mkdir sanctum/data
hdfs dfs -put indexing_token_blacklist.cfg sanctum
