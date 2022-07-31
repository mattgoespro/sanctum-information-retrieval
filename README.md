# SANCTUM-IR: Big Data Indexing and Information Retrieval

## Background and Description

SANCTUM-IR is a module application that was built during my Honours year, alongside three other students who were allocated other parts of the project.

The aim of the project was to develop software to uncover patterns in large feeds of twitter data and display then in a user-friendly way.

The main requirement was to be able to do the data processing in a clustered environment, since the disk size of our sample data was in the order of terrabytes. We therefore decided to use AWS EC2 clusters and their Hadoop infrastructure to test on.

SANCTUM is compromised of three different modules, each of which can run independently of each other:

1. IR (Information Retrieval) Module
2. ARM (Association Rule Mining) Module
3. Pattern Visualization Web App

Results are produced using a pipelined data-processing system:

1. The IR module pre-processes and indexes the large amount of Twitter feed data for faster information retrieval/searches.
2. The AR module processes the indexed data using Association Rule Mining algorithms to produce a record of association rules and patterns. Learn more about it [here](https://www.upgrad.com/blog/association-rule-mining-an-overview-and-its-applications/).
3. The Web App uses these records to display the patterns in a user-friendly way.

## Running SANCTUM-IR

There are 2 ways to run this module:

- Using native Java (does not require Hadoop or the HDFS)
- Using the Hadoop Job Scheduler and HDFS (requires an HDFS installation)

SANCTUM can be configured by modifying the values in _config.cfg_.

Note: When attempting to re-index data, delete the _data_path_store.data_ file to update the documents to search.

## Indexing

Hadoop:

- Execute hadoop-index.sh (Linux) or hadoop-index.bat (Windows)
  SANCTUM:
  - Execute sanctum-index.sh (Linux) or sanctum-index.bat (Windows)
  - Syntax: **java -cp <jar path> com.sanctum.drivers.Main <num threads per file> <num mappers per reducer> <num writers>**

To change the MapReduce parameters, edit the appropriate file above in a text editor.

## Searching:

Search HDFS: **hadoop jar <jar path> com.sanctum.drivers.Search true <top k> [term 1] [term 2] [term 3] ...**

Search local filesystem: **java -cp <jar path> com.sanctum.drivers.Search false <top k> [term 1] [term 2] [term 3] ...**

- When searching for hashtags or mentions, replace the '#' and '@' symbols with 'hashtag*' and 'mention*' respectively. - Eg. #trump -> hashtag_trump, @justin -> 'mention_justin' -->
