import java.text.SimpleDateFormat 

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
appProperties=new ExecutionContext( arguments.findAll { it[0] == '_' } ) 
def appname=properties.map["bundleId"].split("-")[0].toLowerCase()







def shellCommandIterator(cmdString){
	def cmd=[ "bash" , "-c", cmdString.stripMargin()  ]
	return cmd.execute().text.split("\n")
}

/*
Get resources 
ps -wweo uname,pid,psr,pcpu,cputime,pmem,rsz,vsz,tty,s,etime,comm
*/

/*
             total       used       free     shared    buffers     cached
Mem:       8070268    4656424    3413844          0     320908    1022748
-/+ buffers/cache:    3312768    4757500
Swap:      3980284     303888    3676396

*/
def metrics=["_":"_" , "appname":appname, "metric":"os" ]
shellCommandIterator("free").each { line -> 
	if(line.contains("Mem")){
		def tokens=line.split("Mem:")[1].split(/\s+/)[1..-1] 
		metrics["total"]=tokens[0]
		metrics["used"]=tokens[1]
		metrics["usedpct"]= ( tokens[1].toFloat() / tokens[0].toFloat() ) * 100.0 
		metrics["free"]=tokens[2]
		metrics["shared"]=tokens[3]
		metrics["buffers"]=tokens[4]
		metrics["cached"]=tokens[5]
	}
}

//13:05:12 up 2 days,  2:42, 11 users,  load average: 0.36, 0.39, 0.43
shellCommandIterator("uptime").each { line -> 
	if(line.contains("average:")){
		def tokens=line.split("average:")[1].split(',')
		metrics["1m"]=tokens[0]
		metrics["5m"]=tokens[1]
		metrics["15m"]=tokens[2]
	}
}


logger.logkv(metrics) 



