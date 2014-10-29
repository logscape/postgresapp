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

if ( properties.map.containsKey("_filter")== false ) {
	logger.error(" Requires a comma separated app filter  " ) 
}


def appNameFilters=properties.map["_filter"].split(",") 





def shellCommandIterator(cmdString){
	def cmd=[ "bash" , "-c", cmdString.stripMargin()  ]
	return cmd.execute().text.split("\n")
}

/*
Get resources 
ps -wweo uname,pid,psr,pcpu,cputime,pmem,rsz,vsz,tty,s,etime,comm
*/

/*
logscape 29816  11  0.0 00:00:00  0.0  1244  18168 pts/7    R       00:00	X
root     29981   4  0.0 00:00:00  0.0  3604  73444 ?        S    17:11:38	bash
root     30140  11  0.0 00:12:57  0.0     0      0 ?        S 18-18:23:00	perl 
logscape 30191  12  0.0 00:00:00  0.0  1556  73444 ?        S    17:11:38

*/
def processHashMap=[:]
shellCommandIterator("ps -wweo uname,pid,psr,pcpu,cputime,pmem,rsz,vsz,tty,s,etime,comm").each{ line -> 
	toks=line.split(/\s+/)
	pid=toks[1] 
	comm=toks[-1]
	if (appNameFilters.contains(comm)) { 
		processHashMap[pid] = ["_":"_", "appname":appname , "metric":"proc"] 	
		processHashMap[pid]["pid"]=toks[1]
		processHashMap[pid]["pcpu"]=toks[3]
		processHashMap[pid]["pmem"]=toks[5]
		processHashMap[pid]["rss"]=toks[6]
		processHashMap[pid]["comm"]=toks[-1]
	}
}


processHashMap.keySet().each{ k -> 
	logger.logkv(processHashMap[k]) 
/*	line=", pid:"+k
        line=line+ " "+ processHashMap[k]
        line=line.replace("cID",", CID")
        line=line.replace("[","")
        line=line.replace("]","")

        line= line.replaceAll(/\, (\w+)(\:)/){ all,key,delim ->
                ", "  + key + "="
        }

        logger.log(line)*/

}


