PostgresApp-1.1 ![](http://logscape.com/images/track.png?version=github)
===========


The PostgresApps monitors tables, indexes and resource utilisation

## Prequisites

The following commands should be available from the command-line. They are available in most default Linux/Postgres installations. 

	sar needs to be installed.
	iostat is required for disk performance metrics 

## Downloads 

 * [PostgresApp-1.1.zip](https://github.com/logscape/postgresapp/raw/master/dist/PostgresApp-1.1.zip)
 * [Example Properties File ](https://github.com/logscape/postgresapp/raw/master/dist/PostgresApp-1.1-override.properties)


## Overview

The Home Workspace gives you an overview of your databases and a view of  your postgres logs. The App is split into four categories

	* Tables - displays the scan for the tables. Your query profiles. 
	* Cache  - displays the buffer hit and miss counts for the tables and indexes. 
	* Toast -  displays information about the toast tables and their buffer hit performance. 
	* Processes  - displays the kernel cache, cpu and memory utilization of postgres processes 

![](docs/images/pg_home.png) 


## Tables 

![](docs/images/pg_tables.png) 

## Cache 

![](docs/images/pg_cache.png) 
