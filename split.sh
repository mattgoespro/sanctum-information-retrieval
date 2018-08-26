#!/bin/bash
hdfs dfs -rm -R /sanctum/tweet_documents
hadoop jar sanctum_ir-master/target/SANCTUM-1.0-SNAPSHOT.jar HadoopDocSplit
