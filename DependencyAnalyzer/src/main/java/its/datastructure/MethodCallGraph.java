package its.datastructure;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


public class MethodCallGraph  {
	private Map<Method, HashSet<Method>> methodinterfaces = new HashMap<Method, HashSet<Method>>();
	private Map<JavaClass, HashSet<Method>> methodinclass = new HashMap<JavaClass, HashSet<Method>>();
	private File filename;
	private Map<JavaClass,HashSet<Method>>methoddeclaredinfile = new HashMap<JavaClass, HashSet<Method>>();
	
	public MethodCallGraph(File filename,Map<Method, HashSet<Method>> methodinterfaces,Map<JavaClass, HashSet<Method>> methodinclass) {
		this.filename = filename;
		this.methodinterfaces = methodinterfaces;
		this.methodinclass = methodinclass;
	}
	
	public void setInterfaceMapForUpdate(Map<Method, HashSet<Method>> methodinterfaces) {
		this.methodinterfaces = methodinterfaces;
	}

	public void setSuperClassMapForUpdate(Map<JavaClass, HashSet<Method>> methodsuperclass) {
		this.methodinclass = methodsuperclass;
	}
	
	public Map<Method, HashSet<Method>> getInterfaces() {
		return this.methodinterfaces;
	}
	
	public Map<JavaClass, HashSet<Method>> getSuperClasses(){
		return this.methodinclass;
	}
	
	public File getfile() {
		return this.filename;
	}
	
	public void setfile(File filename) {
		this.filename = filename;
	}
	
	public void setMethodDeclaredinClass(Map<JavaClass, HashSet<Method>> methoddeclaredinfile) {
		this.methoddeclaredinfile = methoddeclaredinfile;
	}
	
	public Map<JavaClass, HashSet<Method>>  getMethodDeclaredinClass() {
		return this.methoddeclaredinfile;
	}
	
}
