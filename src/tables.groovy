import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import groovy.sql.Sql;



pout = pout == null ? System.out : pout
perr = perr == null ? System.err : perr

def stdOut = pout
def stdErr = perr
Logger logger = new Logger()
logger.setOutStream(pout)
logger.setErrStream(perr)


def propertyMissing(String name) {}

 
def arguments  =  args  as List 
def properties= null;


if (arguments!=null) {
	properties=new ExecutionContext(arguments) 
}else{
	properties=new ExecutionContext(arguments,_bindings) 
} 


def conn = new ConnectionDetails();


conn.user=properties.map["defaultUser"]		// defaultUser
conn.password=properties.map["defaultPassword"]	// defaultPassword 
conn.hostnames=properties.map["hostnames"].split(",")   // user:password@hostname 


def loggers=[  new QueryLogger(conn,logger),  new QueryLogger(conn,logger)  ] 
// ,  new QueryLogger(conn,logger)) ]
// Get statio tables 
loggers[0].setQuery("select pg_database.datname, pg_database_size(pg_database.datname) AS size FROM pg_database")
loggers[0].setKeys(["host","datname"]) 

// Calculate cache 
loggers[1].setQuery("select * from pg_stat_all_tables") 
loggers[1].setKeys(["host","relid","relname"]) 

hardInterval=60
softInterval=20
wait=0
count=0
while(count<5){
	for(def qLogger: loggers){
		qLogger.update() 
		if(wait >= hardInterval ) {
			wait=0;
			qLogger.write() 
			count++
		}else{
			qLogger.writeDeltas() 
		}
	}
	sleep(softInterval*1000) 
	wait+=softInterval 

}


