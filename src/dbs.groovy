import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import groovy.sql.Sql



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

def qLogger= new QueryLogger(conn,logger)

qLogger.setQuery("select datname,numbackends,xact_commit,xact_rollback,blks_read,blks_hit from pg_stat_database;")
qLogger.setKeys(["host","datname"]) 
hardInterval=60
softInterval=20
wait=0
count=0
qLogger.update() 
while(count<5){
	qLogger.update() 
	if(wait >= hardInterval ) {
		wait=0;
		qLogger.write() 
		count++
	}else{
		qLogger.writeDeltas() 
	}
	sleep(softInterval*1000) 
	wait+=softInterval 

}

System.exit(0) 


