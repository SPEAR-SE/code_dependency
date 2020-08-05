package testsmell;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.resolution.types.ResolvedReferenceType;

import util.ClassInterfacesCollector;
import util.JavaClass;
import testsmell.helper.FileMapping;
import util.Constant;
import util.GetCU;
import util.Output;

public class TS_InheritForPolymorphism {
	private FileMapping filemapping = new FileMapping();
	
	public void getInheritForPolymorphism(List<File>files,List<File>testfiles) {
		Output output = new Output();
		String constantpath  = "/Users/zipeng/Projects/10project/";
		
		HashMap<File, HashSet<File>> javaclass_children_file = filemapping.getDirectExtension(testfiles);
		for(File file: javaclass_children_file.keySet()) {
			//step 1: found files that have more than one inhertiance.
			 
//			if(file.getName().contains("BaseProviderMetadataTest")) {
//				System.out.println(file.getName());
//				System.out.println(javaclass_children_file.get(file));
//			}
			if(javaclass_children_file.get(file).size() > 1) {
				//step2: compare the children
				HashSet<File> children = javaclass_children_file.get(file);
				
				Object[] childrenArray = new File[children.size()] ;
				childrenArray  =   children.toArray();
				
				outloop:
				for(int i = 0; i < childrenArray.length - 1 ; i++) {
					for(int j = i+1; j< childrenArray.length; j++) {
						if(similarJavaFile((File)childrenArray[i],(File)childrenArray[j],files)) {							
							System.out.println(file + " inheritedby " + javaclass_children_file.get(file));
							
							String[]data = {"TESTSMELL3","InheritForPolymophism", 
									file.toString().replace(constantpath, ""),
									"Inherited_by_"+javaclass_children_file.get(file).size()+"_classes",
									javaclass_children_file.get(file).toString()
							};
							output.writerCSV(data, Constant.testsmell);
							break outloop;
						}
						
					}					 
				}
						 
			}
		}
		
	}
	
	/*
	 * check whether fileA and fileB are similar to each other:
	 * 1) Case1: only method parameters varies in class construct  
	 */
	public boolean similarJavaFile(File fileA, File fileB,List<File>files) {
		boolean similar = false;
		GetCU getcu = new GetCU();
		CompilationUnit cuA = getcu.getCu(fileA);
		CompilationUnit cuB = getcu.getCu(fileB);
		
		ClassOrInterfaceDeclaration cA = cuA.findFirst(ClassOrInterfaceDeclaration.class).get();//.findAll(Statement.class).size();
		ClassOrInterfaceDeclaration cB = cuB.findFirst(ClassOrInterfaceDeclaration.class).get();//.findAll(Statement.class).size();
		
		
		//case1: only constructor difference
		if(cA.getMembers().size() == 1 && cB.getMembers().size() == 1) {			
			if(cuA.findAll(ConstructorDeclaration.class).size() == 1 && cuB.findAll(ConstructorDeclaration.class).size() == 1) {
				ConstructorDeclaration classA = cuA.findFirst(ConstructorDeclaration.class).get();
				ConstructorDeclaration classB = cuB.findFirst(ConstructorDeclaration.class).get();	
				if(similarConstructor(classA,classB,files) == true) {
					similar = true;
				}
			}
		}					
		return similar;
	}
	
	/*
	 *  check whether two constructors are similar to each other  
	 */	
	public boolean similarConstructor(ConstructorDeclaration classA, ConstructorDeclaration classB,List<File>files) {
		boolean similar = false;
		 
		//the two constructor both only call super(arg1, arg2);
		if(classA.removeComment().findAll(Statement.class).size() == classB.removeComment().findAll(Statement.class).size()) {
			//if only call super(arg1,arg2) in constructor
			if(classA.findFirst(ExplicitConstructorInvocationStmt.class).isPresent() &&	classB.findFirst(ExplicitConstructorInvocationStmt.class).isPresent()) {
				ExplicitConstructorInvocationStmt ecA = classA.findFirst(ExplicitConstructorInvocationStmt.class).get();
				ExplicitConstructorInvocationStmt ecB = classB.findFirst(ExplicitConstructorInvocationStmt.class).get();
				if(similarConstructorCall(ecA,ecB,files) == true) {			 
						similar = true;
				}				
			}
			
			//TODO: add other Cases(e.g: variable different)
		}
 			
		return similar;
	}
	
 
	
	public boolean similarConstructorCall(ExplicitConstructorInvocationStmt excA, ExplicitConstructorInvocationStmt excB,List<File>files) {
		boolean similar = true;
		
		NodeList<Expression> excAargs = excA.getArguments();
		NodeList<Expression> excBargs = excB.getArguments();
		if(excAargs.size()==0 && excBargs.size()==0) {
			return similar;
		}
		else {
			if(excAargs.size() != excBargs.size()) {
				similar = false;
			}
			else {
				for(int i = 0; i < excAargs.size(); i++) {
					Expression expA = excAargs.get(i);
					Expression expB = excBargs.get(i);					
					if(expA.isObjectCreationExpr() && expB.isObjectCreationExpr()) {
						if(similarObjectCreationExpr(expA.asObjectCreationExpr(),expB.asObjectCreationExpr(), files) == false) {
							similar = false;
							break;
						}						 
					}					 
				}
			}			 
		}
		
		return similar;
	}
	 
	public boolean similarObjectCreationExpr(ObjectCreationExpr expA, ObjectCreationExpr expB,List<File>files) {
		boolean similar = false;
		JavaClass javaclassA,javaclassB;
		 if(expA.findFirst(ClassOrInterfaceType.class).isPresent() && 
				 expB.findFirst(ClassOrInterfaceType.class).isPresent()) {
			 ClassOrInterfaceType ciA = expA.findFirst(ClassOrInterfaceType.class).get();
			 javaclassA = getJavaClass_from_ClassOrInterfaceType(ciA);
			 ClassOrInterfaceType ciB = expB.findFirst(ClassOrInterfaceType.class).get();
			 javaclassB = getJavaClass_from_ClassOrInterfaceType(ciB);
		 }else {
			 return false;
		 }
		  
		 if(inheritFromSameParent(javaclassA,javaclassB,files)) {
			 similar = true;
		 }
		return similar;
	}
	
	
	//transfer ClassOrInterfaceType to Javaclass - simple version
	public JavaClass getJavaClass_from_ClassOrInterfaceType(ClassOrInterfaceType classtype) {		
		ClassInterfacesCollector cicollector = new ClassInterfacesCollector();
		String dependentpackage = cicollector.getCurrentpackagename(classtype.findCompilationUnit().get());		
		String field = "";
		String classname = "";
		
		//get all the dependency for the file
		List<Name> dependencynames = cicollector.getDependencyname(classtype.findCompilationUnit().get());		 
		List<String> dependentyfilename = new ArrayList <String>();  
		if(dependencynames.size() > 0) {
			for (Name nm: dependencynames) {				
				String filename = nm.asString().substring(nm.toString().lastIndexOf(".")+1);
				dependentyfilename.add(filename);			
			}			
		}	
		
		if(classtype.toString().contains(".")) {
			field = classtype.toString().substring(0, classtype.toString().indexOf("."));
			classname = classtype.toString().substring(classtype.toString().lastIndexOf(".")+1);
		}
		else {
			field = classtype.toString();
			classname = classtype.getNameAsString();
		}
		try {
			ResolvedReferenceType rrt = classtype.resolve();
			String truepackagename = rrt.getTypeDeclaration().getPackageName();
			String childclassname = rrt.getTypeDeclaration().getClassName();
			if(childclassname.contains(".")) {
				while(childclassname.contains(".")){
					String parentclassname = childclassname.substring(0, childclassname.lastIndexOf("."));
					dependentpackage = truepackagename + parentclassname;
					childclassname = parentclassname;
				}			
			}else {
				dependentpackage = truepackagename;
			}		
		}catch(Exception e){
			if(dependentyfilename.contains(classtype.toString())) {
				for(Name dn: dependencynames) {
					if(dn.getIdentifier().equals(classtype.getNameAsString())) {
						dependentpackage = dn.asString().substring(0, dn.asString().lastIndexOf("."));
						break;
					}								
				}
			}else if(dependentyfilename.contains(field) ) {
				for(Name dn: dependencynames) {
					if(dn.getIdentifier().equals(field)) {																	
						dependentpackage = dn.asString().substring(0, dn.asString().lastIndexOf("."));
						dependentpackage = dependentpackage + "." + field;
						break;
					}								
				  }		
				}
			}
		
		JavaClass javaclass = new JavaClass(dependentpackage,classtype.getName().toString());
		return javaclass;
	}
	
	//check whether two javaclass extends the same parent.
	public boolean inheritFromSameParent(JavaClass jcA, JavaClass jcB,List<File>files) {
		boolean sameparent = false;
		File fileA=null,fileB = null;
		HashMap<JavaClass,File> class_file = filemapping.getJavaClassFileMapping(files);
		if(class_file.keySet().contains(jcA)) {
			fileA = class_file.get(jcA);
		}
		if(class_file.keySet().contains(jcB)) {
			fileB = class_file.get(jcB);
		}
		
		if(fileA != null && fileB != null) {
			HashMap<File, HashSet<JavaClass>> file_javaclasses = filemapping.getFileExtendMapping(files);
			HashSet<JavaClass> parentA = file_javaclasses.get(fileA);
			HashSet<JavaClass> parentB = file_javaclasses.get(fileB);			
			if(parentA.equals(parentB)) {
				sameparent = true;
			}
		}				
		return sameparent;
	}

}
