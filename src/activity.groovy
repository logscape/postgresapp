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

defaultUser=properties.map["defaultUser"]		// defaultUser
defaultPassword=properties.map["defaultPassword"]	// defaultPassword 
hostnames=properties.map["hostnames"].split(",")   // user:password@hostname 
def resultSet=[:]

for(def hostname:hostnames){
	def url = "jdbc:postgresql://"+hostname+"/postgres?user="+defaultUser+"&password="+defaultPassword+"";
	def sql = Sql.newInstance(url) 
	def serverInfo=new DBServerInfo(defaultUser,defaultPassword,hostname,sql)
	resultSet[hostname]=["connectionDetails":serverInfo] 
}


query="select datname,pid,usename,application_name,client_addr,client_hostname,backend_start,waiting,state from pg_stat_activity;"
for(def hostname:resultSet.keySet()){
	def sql=resultSet[hostname]["connectionDetails"].sql 
	sql.eachRow(query) {
		def row=" host:" + hostname + ", " +it 
		logger.log(row.replace("[","").replace("]",""))
		
	}
}

System.exit(0) 


