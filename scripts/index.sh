#!/bin/bash

hdfs dfs -rm -R sanctum/index
hadoop jar sanctum_ir-master/target/SANCTUM-1.0-SNAPSHOT.jar com.sanctum.drivers.HadoopIndex
