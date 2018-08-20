#!/bin/bash

hdfs dfs -rm -R /sanctum/output
hadoop jar sanctum_ir-master/target/SANCTUM-1.0-SNAPSHOT.jar HadoopMain
