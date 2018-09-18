============================================================================
SANCTUM Information Retrieval Module for Indexing and Searching Twitter Data
============================================================================

There are 2 ways to run this module:
    - Using native Java (does not require Hadoop or the HDFS)
	- Using the Hadoop Job Scheduler and HDFS (requires that the HDFS has been installed)

========================================
			  Configuration
========================================

Check the file named config.cfg.

========================================
Setup (if setup script does not work)
========================================

SANCTUM: None

Hadoop:
	- Create a new folder in the root directory of the HDFS called 'sanctum'
	- Create a new folder inside /sanctum called 'data'
	- Put your data files to index into the /sanctum/data directory.

=======================================
				  Run
=======================================

Note: When attempting to reindex data,
delete the 'data_path_store.data' file
to update the documents to search.

Indexing:
	- Hadoop: 
		- Execute hadoop-index.sh (Linux) or hadoop-index.bat (Windows)
	- SANCTUM: 
		- Execute sanctum-index.sh (Linux) or sanctum-index.bat (Windows)
		- To change the MapReduce parameters, edit the appropriate file
		above in a text editor.
			- Syntax: java -cp <jar path> com.sanctum.drivers.Main <num threads per file> <num mappers per reducer> <num writers>

Searching:
	- Search HDFS: hadoop jar <jar path> com.sanctum.drivers.Search true <top k> [term 1] [term 2] [term 3] ...  
	- Search local filesystem: java -cp <jar path> com.sanctum.drivers.Search false <top k> [term 1] [term 2] [term 3] ...  
	- When searching for hashtags or mentions, replace the '#' and '@' symbols with 'hashtag_' and 'mention_' respectively.
		- Eg. #trump -> hashtag_trump, @justin -> 'mention_justin'