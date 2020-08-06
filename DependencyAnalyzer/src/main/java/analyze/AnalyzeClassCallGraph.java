package analyze;

import java.util.HashSet;
import java.util.Map;
import java.util.Stack;

import its.datastructure.JavaClass;

public class AnalyzeClassCallGraph {
	/*
	 * get all classes from call graph (remove the hierarchy)
	 *  
	 *  a=[b,c]; b=[d,e];c=[];d=[];e=[];
	    a = [b,c,d,e];
	 *
	 */
	    public  HashSet<JavaClass> iteratorClassHashMap(JavaClass rootjavaclass, Map<JavaClass, HashSet<JavaClass>>classinterfaces) {  	
	    	HashSet<JavaClass> newclasses = new HashSet<JavaClass>();
	    	newclasses.add(rootjavaclass);
	    	
			Stack<JavaClass> tmp = new Stack<JavaClass>();
			if(rootjavaclass != null) {
	    		tmp.push(rootjavaclass);
	    	}
			
			JavaClass popvalue;
			while(!tmp.isEmpty()) {
				popvalue = tmp.pop();
				newclasses.add(popvalue);
				if(classinterfaces.get(popvalue) != null) { 
					for(JavaClass test: classinterfaces.get(popvalue)) {
						if(!newclasses.contains(test)) {
							tmp.push(test);
						}
					}
			    } 
			}
					
			return newclasses;		
		}

}
