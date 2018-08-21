#!/bin/bash

hdfs dfs -rm -R /sanctum/output/searchresults
cd sanctum_ir-master
mvn package
cd ..
hadoop jar sanctum_ir-master/target/SANCTUM-1.0-SNAPSHOT.jar HadoopSearch heart
hdfs dfs -cat /sanctum/output/searchresults/*
