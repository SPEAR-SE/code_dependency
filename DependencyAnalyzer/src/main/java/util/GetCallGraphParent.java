package util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import its.datastructure.JavaClass;

public class GetCallGraphParent {
	public Map<JavaClass,HashSet<JavaClass>> getcallgraphparent(Map<JavaClass,HashSet<JavaClass>>interfaces) {
		Map<JavaClass,HashSet<JavaClass>>parents = new HashMap<JavaClass,HashSet<JavaClass>>();
		 
		Set<JavaClass> keys = interfaces.keySet();
		if(keys.size() > 0) {
			for(JavaClass key: keys) {			 
				HashSet<JavaClass> values = interfaces.get(key);
		 
				for(JavaClass interfacevalue: values) {					 
					 Boolean haskey = false;
					 Set<JavaClass> kys = parents.keySet();
					 if(kys.size()>0) {
						for(JavaClass ky: kys) {				 
							 if(ky.equals(interfacevalue)) {
								parents.get(interfacevalue).add(key);				
								haskey = true;						
								break;
						     } 						
						}
					 }
					 if(haskey == false) {
						 HashSet<JavaClass> tmphash = new HashSet<JavaClass>();	
						 tmphash.add(key);
						 parents.put(interfacevalue,tmphash);
					 }	
			
			   }				
			}
		}

			
	return parents;	
	}

}
