package util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;

import configuration.Constant;
import configuration.SetEnv;
import its.datastructure.FileClassinfo;
import its.datastructure.JavaClass;
import visitor.ClassDeclarationVisitor;
import visitor.ClassInterfacesCollector;
import visitor.enumVisitor;
 

public class getFileInfoInSystem {
	private Map<JavaClass, HashSet<JavaClass>> interfaces = new HashMap<JavaClass, HashSet<JavaClass>>();
	private Map<JavaClass, HashSet<JavaClass>> classparents = new HashMap<JavaClass, HashSet<JavaClass>>();
	
	public getFileInfoInSystem(Map<JavaClass, HashSet<JavaClass>> interfaces,Map<JavaClass, HashSet<JavaClass>> classparents) {
		this.interfaces = interfaces;
		this.classparents = classparents;
	}
	
	public Map<JavaClass, HashSet<JavaClass>> getclassinterfaceMap() {
		return this.interfaces;
	}
	public void setclassinterfaceMap(Map<JavaClass, HashSet<JavaClass>> interfaces) {
		this.interfaces = interfaces;
	}
	public Map<JavaClass, HashSet<JavaClass>> getclassparentsMap(){
		return this.classparents;
	}
	public void setclassparentsMap(Map<JavaClass, HashSet<JavaClass>> classparents) {
		this.classparents = classparents;
	}
	
	/*
	 * return all interfaces in project//Map<JavaClass, HashSet<JavaClass>>
	 */
	public List<FileClassinfo>  getFileInfoList(String projectdirpath, String jardir) throws Exception {			
		SetEnv envset = new SetEnv();	
		List<File> projectfiles = envset.getJavaFiles(projectdirpath);		
		//System.out.println(projectfiles.size());

		//Map<File,FileClassinfo> systemindex = new HashMap<File,FileClassinfo>();
		
		List<FileClassinfo> allfileinfo = new ArrayList<FileClassinfo>();
		
		//HashSet<JavaClass> testFiles = new HashSet<JavaClass>();
		
		for(File projectfile : projectfiles) {
			System.out.println("parsing " + projectfile);
			TypeSolver typesolver = envset.setTypesolver(projectdirpath, jardir, 2);
			envset.setStaticParserConfiguration(typesolver);			
			
			FileClassinfo fileClassinfo = getonefileinfo(projectfile,projectfiles,typesolver);	
			
			//systemindex.put(projectfile, fileClassinfo);
			
			if(fileClassinfo !=null) {
				allfileinfo.add(fileClassinfo);
			}	
		}			
		
		PoslishFileClassinfo gia = new PoslishFileClassinfo();
	 	gia.polishFileClassInfo_allimport(allfileinfo);	
	 	gia.polistTest(allfileinfo, projectfiles);
	 	gia.givepackagename(allfileinfo,projectfiles);
	 	gia.polishFileClassInfo_extends(allfileinfo);
	 	
	 	
	 	//System.out.println("fileparse: "+ allfileinfo.size());
		
	 	return allfileinfo;							
	} 
	
	
	/*
	 *  return JavaClass and set of JavaClasses called or declared in it 
	 */		
	public  ClassInterfacesCollector getCallGraphInterface(CompilationUnit cu,TypeSolver typesolver) {
		// all interfaces
		ClassInterfacesCollector cifc = new ClassInterfacesCollector(typesolver);
		Map<JavaClass, HashSet<JavaClass>> interfaces = new HashMap<JavaClass,  HashSet<JavaClass>>();
		cifc.setinterfaces(interfaces);
		HashSet<JavaClass> mockclass = new HashSet<JavaClass>();
		cifc.setmockclass(mockclass);
		
		VoidVisitor<ClassInterfacesCollector> visitclass =  new ClassInterfacesCollector(typesolver);			
		visitclass.visit(cu, cifc); 
		
		return cifc;
	}
	
	
	
	public Boolean isTest(CompilationUnit cu) {
		if(cu.toString().contains("@Test")) {
			return true;
		}
		else {
			return false;
		}		
	}
	
	public Boolean isUtil(File filename) {
		String fn = filename.getName().toLowerCase();	
		if(fn.contains("util")) {				
				return true;		
		}
		return false;		
	}
	
	/*
	 * @Deprecated
	 
	public Boolean isAbstract(File filename) {
		String fn = filename.getName().toLowerCase();
		if(fn.contains("abstract")) {				
				return true;
			}		
		return false;		
	}
	*/
	
	public boolean isAbstract(CompilationUnit cu) {
		if(cu.findFirst(ClassOrInterfaceDeclaration.class).isPresent()) {
			ClassOrInterfaceDeclaration firstclass = cu.findFirst(ClassOrInterfaceDeclaration.class).get();
			if(!firstclass.getModifiers().isEmpty()) {
				for(Modifier m: firstclass.getModifiers()) {					 
					if(m.getKeyword().toString().toLowerCase().equals("abstract")) {						
						return true;
					}
				}
			}
		}
		return false; 
	}
	
	public FileClassinfo getonefileinfo(File projectfile,List<File> projectfiles,TypeSolver typesolver) {
		GetCU getcu = new GetCU();
		CompilationUnit cu = getcu.getCu(projectfile);
		
		FileClassinfo fileClassinfo= new FileClassinfo(new HashMap<JavaClass, HashSet<JavaClass>>(), new HashMap<File, JavaClass>(),
				new File(""),"","",new HashSet<JavaClass>(),new HashSet<String>(),0,false,new ArrayList<Name>(),new HashSet<JavaClass>(),
				new HashSet<JavaClass>(),new HashSet<JavaClass>(),0,0,0,0,0,0,0,0,0,0,new HashSet<File>(),
				new HashSet<JavaClass>(),new HashMap<JavaClass, HashSet<JavaClass>>(),new HashSet<JavaClass>(),new JavaClass("",""));
		fileClassinfo.setFilename(projectfile);
		fileClassinfo.setPath(projectfile.getPath());
		
		if(cu != null) {
		/*
		 * find rootjavaclass
		 */
		String packagename = "";
		Optional<PackageDeclaration> rootpackage = cu.getPackageDeclaration();
		if(rootpackage.isPresent()) {
			packagename = rootpackage.get().getNameAsString();
		}
		String classname = "";
		if(cu.findFirst(ClassOrInterfaceDeclaration.class).isPresent()) {
			classname = cu.findFirst(ClassOrInterfaceDeclaration.class).get().getNameAsString();
		}
		else if(cu.findFirst(EnumDeclaration.class).isPresent()) {
			classname = cu.findFirst(EnumDeclaration.class).get().getNameAsString();
		}
		JavaClass rootjavaclass = new JavaClass(packagename,classname);
		
		/*
		 *  get all the dependency(import) file name
		 */
		ClassInterfacesCollector cic = new ClassInterfacesCollector(typesolver);
		HashSet<String> dependentfilename = new HashSet <String>();
		List<Name> dependname = new ArrayList<Name>();
		if(cic.getDependencyname(cu).size() > 0) {
			dependname = cic.getDependencyname(cu);
			for (Name nm: cic.getDependencyname(cu)) {
				String filename1 = nm.asString().substring(nm.toString().lastIndexOf(".")+1);
				dependentfilename.add(filename1);
				
		 }
	   }
			
		List<Name> importallname = new ArrayList<Name>();
		Boolean containimportall = false;
		if( getimportall(cu).size()>0) {
			importallname =  getimportall(cu);
			containimportall = true;
		}
		/* get all the interface(called classes) of this projectfile
		 * by javaparser
		 */
		ClassInterfacesCollector classinterfacecollector = getCallGraphInterface(cu,typesolver);
		//Map<JavaClass, HashSet<JavaClass>> callinterface = classinterfacecollector.getinterfaces();			
		//HashSet<ClassOrInterfaceType> mockclass = getCallGraphInterface(cu).getmockclass();
		GetAdditionalclass gac = new GetAdditionalclass();
		classinterfacecollector = gac.getotherjavaclass(cu, dependname,classinterfacecollector,projectfiles);
		Map<JavaClass, HashSet<JavaClass>> interfacee = classinterfacecollector.getinterfaces();
		
		HashSet<JavaClass> calledjavaclass = new HashSet<JavaClass>();
		calledjavaclass = interfacee.get(rootjavaclass);
		
		HashSet<JavaClass> mockclass = classinterfacecollector.getmockclass();
		/*
		 * find all declared classes in this projectfile
		 */
		//HashMap<File,HashSet<JavaClass>>declaredclassinfile = new HashMap<File,HashSet<JavaClass>>();
		HashSet<JavaClass> jcs = new HashSet();			
		ClassDeclarationVisitor cdv = new ClassDeclarationVisitor();
		cdv.visit(cu, jcs);
		
		/*HashSet<JavaClass> enumjcs = new HashSet<JavaClass>();
		VoidVisitor<HashSet<JavaClass>>  ev = new enumVisitor();
		ev.visit(cu, enumjcs);
		
		jcs.addAll(enumjcs);*/
				
		HashMap<File,JavaClass> superclasses = new HashMap<File, JavaClass>();
		superclasses.put(projectfile, rootjavaclass);
			
		// generate lines of code
		CountLine cl = new CountLine();
		int linesofcode = 0;
		linesofcode = cl.countLineinCU(cu);
		
		//get extends
		GetClassExtends gce = new GetClassExtends();
		HashSet<JavaClass> extendsjavaclasses = gce.getextends(cu);
		
		JunitCount junitcount = new JunitCount();
		int istestNumber = 0;
		istestNumber = junitcount.countistestNumber(cu);
		int beforeClassNumber = 0;
		beforeClassNumber = junitcount.countbeforeClassNumber(cu);
		int beforeNumber = junitcount.countbeforeNumber(cu);
		int afterClassNumber = junitcount.countafterClassNumber(cu);
		int afterNumber = junitcount.countafterNumber(cu);
		
		
		
		
		fileClassinfo.setInterfaceMapForUpdate(interfacee);	
		Map<JavaClass, HashSet<JavaClass>> interfacee_withextend = new HashMap<JavaClass, HashSet<JavaClass>>(interfacee);
		fileClassinfo.setInterfaces_includes_extends(interfacee_withextend);	
		fileClassinfo.setcalledjavaclass(calledjavaclass);
		//fileClassinfo.setfileparents(parent);
		fileClassinfo.setSuperClassMapForUpdate(superclasses);
		fileClassinfo.setPackage(packagename);
		fileClassinfo.setClassDeclaredInFile(jcs);
		
		HashSet<JavaClass> jcs_withextend = new HashSet<JavaClass> (jcs);
		fileClassinfo.setclassDeclaredInFile_includeExtends(jcs_withextend);
		
		fileClassinfo.setisabstract(isAbstract(cu));		 
		
		fileClassinfo.setistest(isTest(cu));
		fileClassinfo.setisutil(isUtil(projectfile));
		
		fileClassinfo.setDependenciesName(dependentfilename);
		fileClassinfo.setloc(linesofcode);
		
		fileClassinfo.setcontainimportall(containimportall);
		fileClassinfo.setimportallname(importallname);
		
		fileClassinfo.setExtendsclasses(extendsjavaclasses);
		fileClassinfo.setmockclass(mockclass);
		
		fileClassinfo.setistestNumber(istestNumber);
		fileClassinfo.setbeforeClassNumber(beforeClassNumber);
		fileClassinfo.setbeforeNumber(beforeNumber);
		fileClassinfo.setafterClassNumber(afterClassNumber);
		fileClassinfo.setafterNumber(afterNumber);
		
		fileClassinfo.setrootjavaclass(rootjavaclass);
			 
		
	}
		return fileClassinfo; 
	}
	
	
	public List<Name> getimportall(CompilationUnit cu) {
		List<String> whitelists = Arrays.asList("org.junit","java.util");
		 List<ImportDeclaration> packages = cu.findAll(ImportDeclaration.class);
		 List<Name> importallnames = new ArrayList<Name>();
		 if(packages.size()> 0 && packages.toString().contains(".*")) {
			 for (ImportDeclaration packagee: packages) {	
				 if(packagee.toString().contains(".*")) {
					 Boolean haswhitelist = false;
					 for(String whitelist: whitelists) {
						 if(packagee.toString().contains(whitelist)) {
							 haswhitelist = true;
							 break;							
						 }						 
					 }
					 if(haswhitelist == false) {
						 importallnames.add(packagee.getName());
					 }				 
				 }
				 	
			 }	
		 }		  		 
		 return importallnames;
	}
	
	

}

