
import java.text.SimpleDateFormat

class ExecutionContext  { 
	def map=null
        def ExecutionContext(args,defaults=null){
                def map=[:]
		if (defaults != null) { map = defaults }
                for(def arg:args){
                        def index=arg.indexOf("=")
                        def k=arg.substring(0,index)
                        def v=arg.substring(index+1,arg.size())
                        map[k]=v
                }
                this.map=map
        }

}


class Logger{
	private static verbose=false;
	private static Logger instance = null;
	private static pout;
	private static perr;
	private Logger(){}

	def setVerboseTrue(){
		verbose=true;
	}
	
	def getInstance(){
		if(instance==null){
			instance = new Logger();
		}
		return instance;
	}
	def setOutStream(def out){
		pout=out		
	}
	def setErrStream(def err){
		perr=err
	} 
	def log( line ){
		def dt = new Date()
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss zzz"); 
		//def timestamp = String.format('%1$te-%1$tb-%1$ty %tT',dt) 
		def timestamp=dateFormatter.format(dt)
		pout <<  "" + timestamp + "\t" + line  + "\n" 
	}

	def logkv( map ) {
		def line=""
		if (map.keySet().size() < 1) { return } 
		for(def k: map.keySet()){
			def v = map[k] 
			line=line +  ', "' + k + '": "' + v  +'"'
		}
		log(line) 
	}
	def error( line ,exception=null) { 
		def dt = new Date()
		def timestamp = String.format('%1$te-%1$tb-%1$ty %tT',dt) 
		perr <<  "" + timestamp + "\t" + line  + "\n" 
	}

	def exception(line,exception=null){
		this.error("Exception: ["+line+"]") 

		if(exception!=null){
			exception.printStackTrace(perr) 
		}
	}
	def verbose(line){
		if(verbose){
			error(line);
		}
	}
}
