package its.datastructure;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.expr.Name;

//import com.github.javaparser.ast.expr.Name;

public class FileClassinfo {
	//private String type = "";
	private Map<JavaClass, HashSet<JavaClass>> interfaces = new HashMap<JavaClass, HashSet<JavaClass>>();
	private Map<File, JavaClass> superclasses = new HashMap<File, JavaClass>();
	private Map<JavaClass, HashSet<JavaClass>> interfaces_include_extends = new HashMap<JavaClass, HashSet<JavaClass>>();
	private HashSet<JavaClass> calledjavaclass = new HashSet<JavaClass>();
	private File filename = new File("");
	private JavaClass rootjavaclass = new JavaClass("","");

	private String path = "";
	private String packagename = "";
	
	private int hashcode = 0;
	//class declared in this file
	private HashSet<JavaClass> classinfile = new HashSet<JavaClass>();
	//all the classes declared in this file, includes the classes it extends from parent
	private HashSet<JavaClass>classinfile_include_extends = new HashSet<JavaClass>();
	// private Map<JavaClass, HashSet<JavaClass>>parents = new HashMap<JavaClass,HashSet<JavaClass>>();

	// separate util and abstract file from direct depend file
	private Boolean isutil = false;
	private Boolean isabstract = false;
	private Boolean isrealtest = false;
	private int testcaseNumber=0;
	private int beforeClassNumber=0;
	private int beforeNumber=0;
	private int afterClassNumber=0;
	private int afterNumber=0;
	
	private int istestNumber_extend=0;
	private int beforeClassNumber_extend=0;
	private int beforeNumber_extend=0;
	private int afterClassNumber_extend=0;
	private int afterNumber_extend=0;
	

	// this file has pattern like: import package.class.*
	private Boolean containimportall = false;
	private List<Name> importallname = new ArrayList<Name>();

	private HashSet<String> dependenciesName = new HashSet<String>();
	// lines of code
	private int loc = 0;
	//the direct extend class 
	private HashSet<JavaClass> extendsclasses = new HashSet<JavaClass>();
	//private HashSet<File> extendfiles = new HashSet<File>();
	//all the extended class (maybe multi-layer: A extends B, B extends C)
	private HashSet<JavaClass> alldirectIndirectextendsclasses = new HashSet<JavaClass>();
	private HashSet<File> allextendfiles = new HashSet<File>();
	
	private HashSet<JavaClass> mockclass = new HashSet<JavaClass>();

	// Map<JavaClass, HashSet<JavaClass>>parents
	public FileClassinfo(Map<JavaClass, HashSet<JavaClass>> interfaces, Map<File, JavaClass> superclasses,
			File filename, String path, String packagename, HashSet<JavaClass> classinfile,
			HashSet<String> dependenciesName, int loc, boolean containimportall, List<Name> importallname,
			HashSet<JavaClass> extendsclasses, HashSet<JavaClass> alldirectIndirectextendsclasses,
			HashSet<JavaClass> mockclass,
			int istestNumber,
			int istestNumber_extend,
			int beforeNumber, int afterNumber, int beforeClassNumber, int afterClassNumber,
			int beforeNumber_extend, int afterNumber_extend, int beforeClassNumber_extend, int afterClassNumber_extend,
			HashSet<File>allextendfiles,HashSet<JavaClass>classinfile_include_extends,
			Map<JavaClass, HashSet<JavaClass>> interfaces_include_extends,HashSet<JavaClass> calledjavaclass,JavaClass rootjavaclass) { //HashSet<File>extendfiles,,String type
		this.testcaseNumber = istestNumber;
		this.beforeNumber = beforeNumber;
		this.afterNumber = afterNumber;
		this.beforeClassNumber = beforeClassNumber;
		this.afterClassNumber = afterClassNumber;
		this.packagename = packagename;
		this.interfaces = interfaces;
		this.superclasses = superclasses;
		this.filename = filename;
		this.path = path;
		this.classinfile = classinfile;
		this.dependenciesName = dependenciesName;
		// this.parents = parents;
		this.loc = loc;
		this.containimportall = containimportall;
		this.importallname = importallname;
		this.extendsclasses = extendsclasses;
		this.alldirectIndirectextendsclasses = alldirectIndirectextendsclasses;
		
		//this.extendfiles = extendfiles;
		this.allextendfiles = allextendfiles;
		this.classinfile_include_extends = classinfile_include_extends;
		this.interfaces_include_extends = interfaces_include_extends;
		
		this.mockclass = mockclass;
		
		this.istestNumber_extend = istestNumber_extend;
		this.beforeClassNumber_extend = beforeClassNumber_extend;
		this.afterClassNumber_extend = afterClassNumber_extend;
		this.beforeNumber_extend =beforeNumber_extend;
		this.afterNumber_extend = afterNumber_extend;
		
		this.calledjavaclass = calledjavaclass;
		this.rootjavaclass = rootjavaclass;
		
	}
	
	public JavaClass getrootjavaclass() {
		return this.rootjavaclass;
	}
	public void setrootjavaclass(JavaClass rootjavaclass) {
		this.rootjavaclass = rootjavaclass;
	}

	public HashSet<JavaClass> getcalledjavaclass(){
		return this.calledjavaclass;
	}
	
	public void setcalledjavaclass(HashSet<JavaClass> calledjavaclass) {
		this.calledjavaclass = calledjavaclass;
	}
	
	public HashSet<JavaClass> getmockclass() {
		return this.mockclass;
	}

	public void setmockclass(HashSet<JavaClass> mockclass) {
		this.mockclass = mockclass;
	}

	public HashSet<JavaClass> getAllDirectandIndirectExtendsclasses() {
		return this.alldirectIndirectextendsclasses;
	}

	public void setAllDirectandIndirectExtendsclasses(HashSet<JavaClass> alldirectIndirectextendsclasses) {
		this.alldirectIndirectextendsclasses = alldirectIndirectextendsclasses;
	}

	public HashSet<JavaClass> getExtendsclasses() {
		return this.extendsclasses;
	}

	public void setExtendsclasses(HashSet<JavaClass> extendsclasses) {
		this.extendsclasses = extendsclasses;
	}
	
	public HashSet<JavaClass> getclassDeclaredInFile_includeExtends() {
		return this.classinfile_include_extends;
	}
	
	public void setclassDeclaredInFile_includeExtends(HashSet<JavaClass> classinfile_include_extends) {
		this.classinfile_include_extends = classinfile_include_extends;
	}
	
	public Map<JavaClass,HashSet<JavaClass>> getInterfaces_includes_extends(){
		return this.interfaces_include_extends;
	}
	
	public void setInterfaces_includes_extends(Map<JavaClass,HashSet<JavaClass>> interfaces_include_extends ){
		this.interfaces_include_extends = interfaces_include_extends;
	}
	

	public HashSet<File> getAllextendsfiles() {
		return this.allextendfiles;
	}

	public void setAllextendsfile(HashSet<File> allextendsfiles) {
		this.allextendfiles = allextendsfiles;
	}
	
	
	
	public List<Name> getimportallname() {
		return this.importallname;
	}

	public void setimportallname(List<Name> importallname) {
		this.importallname = importallname;
	}

	public void setcontainimportall(Boolean containimportall) {
		this.containimportall = containimportall;
	}

	public Boolean getcontainimportall() {
		return this.containimportall;
	}

	public void setloc(int loc) {
		this.loc = loc;
	}

	public int getloc() {
		return this.loc;
	}
	
	public void setistestNumber(int istestNumber) {
		this.testcaseNumber = istestNumber;
	}

	public int getRealtestNumber() {
		return this.testcaseNumber;
	}
	public void setbeforeNumber(int beforeNumber) {
		this.beforeNumber = beforeNumber;
	}

	public int getbeforeNumber() {
		return this.beforeNumber;
	}
	public void setbeforeClassNumber(int beforeClassNumber) {
		this.beforeClassNumber = beforeClassNumber;
	}

	public int getbeforeClassNumber() {
		return this.beforeClassNumber;
	}
	public void setafterClassNumber(int afterClassNumber) {
		this.afterClassNumber = afterClassNumber;
	}

	public int getafterClassNumber() {
		return this.afterClassNumber;
	}
	public void setafterNumber(int afterNumber) {
		this.afterNumber = afterNumber;
	}

	public int getafterNumber() {
		return this.afterNumber;
	}
	
	/////////
	public void setistestNumber_extend(int istestNumber_extend) {
		this.istestNumber_extend = istestNumber_extend;
	}

	public int getRealtestNumber_extend() {
		return this.istestNumber_extend;
	}
	public void setbeforeNumber_extend(int beforeNumber_extend) {
		this.beforeNumber_extend = beforeNumber_extend;
	}
	public int getbeforeNumber_extend() {
		return this.beforeNumber_extend;
	}
	public void setbeforeClassNumber_extend(int beforeClassNumber_extend) {
		this.beforeClassNumber_extend = beforeClassNumber_extend;
	}

	public int getbeforeClassNumber_extend() {
		return this.beforeClassNumber_extend;
	}
	public void setafterClassNumber_extend(int afterClassNumber_extend) {
		this.afterClassNumber_extend = afterClassNumber_extend;
	}

	public int getafterClassNumber_extend() {
		return this.afterClassNumber_extend;
	}
	public void setafterNumber_extend(int afterNumber_extend) {
		this.afterNumber_extend = afterNumber_extend;
	}

	public int getafterNumber_extend() {
		return this.afterNumber_extend;
	}
	
	/////////
	
	public void setDependenciesName(HashSet<String> dependenciesName) {
		this.dependenciesName = dependenciesName;

	}

	public HashSet<String> getDependenciesName() {
		return this.dependenciesName;
	}

	public void setClassDeclaredInFile(HashSet<JavaClass> classinfile) {
		this.classinfile = classinfile;
	}

	public HashSet<JavaClass> getClassDeclaredInFile() {
		return this.classinfile;
	}

	public void setInterfaceMapForUpdate(Map<JavaClass, HashSet<JavaClass>> interfaces) {
		this.interfaces = interfaces;
	}

	public void setSuperClassMapForUpdate(Map<File, JavaClass> superclasses) {
		this.superclasses = superclasses;
	}

	public void setFilename(File filename) {
		this.filename = filename;
	}
	public File getfilename() {
		return this.filename;
	}
	public void setPath(String path) {
		this.path = path;
	}

	public void setPackage(String packagename) {
		this.packagename = packagename;
	}

	public String getPackage() {
		return this.packagename;
	}

	public String getPath() {
		return this.path;
	}

	public Map<JavaClass, HashSet<JavaClass>> getInterfaces() {
		return this.interfaces;
	}
	

	public Map<File, JavaClass> getSuperClasses() {
		return this.superclasses;
	}

	

	public int hashCode() {
		return this.setHashcode(this.interfaces.hashCode() + this.filename.hashCode() + this.packagename.hashCode());
	}

	public boolean equals(Object other) {
		if (other instanceof FileClassinfo == false) {
			return false;
		}

		FileClassinfo otherFileClass = (FileClassinfo) other;
		if (this.getPackage().equals(otherFileClass.getPackage())
				&& this.getfilename().equals(otherFileClass.getfilename())) {
			return true;
		}
		return false;
	}

	public Boolean getistest() {
		return this.isrealtest;
	}

	public void setistest(Boolean istest) {
		this.isrealtest = istest;
	}

	public Boolean getisutil() {
		return this.isutil;
	}

	public void setisutil(Boolean isutil) {
		this.isutil = isutil;
	}

	public Boolean getisabstract() {
		return this.isabstract;
	}

	public void setisabstract(Boolean isabstract) {
		this.isabstract = isabstract;
	}

	public int getHashcode() {
		return hashcode;
	}

	public int setHashcode(int hashcode) {
		this.hashcode = hashcode;
		return hashcode;
	}


}
