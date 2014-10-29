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


/*defaultUser=properties.map["defaultUser"]		// defaultUser
defaultPassword=properties.map["defaultPassword"]	// defaultPassword 
hostnames=properties.map["hostnames"].split(",")   // user:password@hostname 
def resultSet=[:]

for(def hostname:hostnames){
	def url = "jdbc:postgresql://"+hostname+"/postgres?user="+defaultUser+"&password="+defaultPassword+"";
	def sql = Sql.newInstance(url) 
	def serverInfo=new DBServerInfo(defaultUser,defaultPassword,hostname,sql)
	resultSet[hostname]=["connectionDetails":serverInfo] 
}


query="select * from   pg_statio_all_tables ;" 
for(def hostname:resultSet.keySet()){
	def sql=resultSet[hostname]["connectionDetails"].sql 
	sql.eachRow(query) {
		def row=" host:" + hostname + ", " +it 
		logger.log(row.replace("[","").replace("]",""))
		
	}
}

query="SELECT   sum(heap_blks_read) as heap_read,  sum(heap_blks_hit)  as heap_hit,  sum(heap_blks_hit) / (sum(heap_blks_hit) + sum(heap_blks_read)) as ratio FROM   pg_statio_user_tables;"
for(def hostname:resultSet.keySet()){
	def sql=resultSet[hostname]["connectionDetails"].sql 
	sql.eachRow(query) {
		def row=" host:" + hostname + ", " +it 
		logger.log(row.replace("[","").replace("]",""))
		
	}
}


System.exit(0) 
*/


def conn = new ConnectionDetails();


conn.user=properties.map["defaultUser"]		// defaultUser
conn.password=properties.map["defaultPassword"]	// defaultPassword 
conn.hostnames=properties.map["hostnames"].split(",")   // user:password@hostname 


def loggers=[  new QueryLogger(conn,logger),  new QueryLogger(conn,logger)  ] 
// ,  new QueryLogger(conn,logger)) ]
// Get statio tables 
loggers[0].setQuery("select * from   pg_statio_all_tables")
loggers[0].setKeys(["host","relid","schemaname","relname"]) 

// Calculate cache 
loggers[1].setQuery("SELECT   sum(heap_blks_read) as heap_read,  sum(heap_blks_hit)  as heap_hit,  sum(heap_blks_hit) / (sum(heap_blks_hit) + sum(heap_blks_read)) as ratio FROM   pg_statio_user_tables;") 
loggers[1].setKeys(["host"]) 

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

System.exit(0) 

