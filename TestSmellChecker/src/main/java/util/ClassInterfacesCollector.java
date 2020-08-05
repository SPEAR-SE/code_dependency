package util;


 
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;

import util.JavaClass;
import visitor.ClassDeclarationVisitor;
import visitor.ClasstypeVisitor;

public class ClassInterfacesCollector extends VoidVisitorAdapter<ClassInterfacesCollector>{
	private Map<JavaClass, HashSet<JavaClass>> interfaces;
	private HashSet<JavaClass> mockclass;
	public Map<JavaClass, HashSet<JavaClass>> getinterfaces() {
		return this.interfaces;
	}
	public void setinterfaces(Map<JavaClass, HashSet<JavaClass>> interfaces) {
		this.interfaces = interfaces;
	}	
	public HashSet<JavaClass> getmockclass() {
		return this.mockclass;
	}
	public void setmockclass(HashSet<JavaClass> mockclass) {
		this.mockclass = mockclass;
	}
	
	/**
	 * override visit EnumDeclaration
	 */
	public void visit(EnumDeclaration enumdecla,ClassInterfacesCollector interfacesandmock) {
		String packagename = getCurrentpackagename(enumdecla.findCompilationUnit().get());
		String classname = enumdecla.getName().toString();
		Map<JavaClass, HashSet<JavaClass>> interfaces = interfacesandmock.getinterfaces();
	 
		JavaClass parentenum = new JavaClass(packagename,classname);	
		
		HashSet<ClassOrInterfaceType> enumcitchildren = new HashSet<ClassOrInterfaceType>();
		HashSet<ClassOrInterfaceType> enummockchildren = new HashSet<ClassOrInterfaceType>();
		List<HashSet<ClassOrInterfaceType>> enumallchildren = new ArrayList<HashSet<ClassOrInterfaceType>>();
		enumallchildren.add(enumcitchildren);
		enumallchildren.add(enummockchildren);
		VoidVisitor<List<HashSet<ClassOrInterfaceType>>>  visitclasstype = new ClasstypeVisitor();
		visitclasstype.visit(enumdecla, enumallchildren);
		enumcitchildren = enumallchildren.get(0);
		enummockchildren = enumallchildren.get(1);
		
		//get all the dependency for the file
		List<Name> dependencynames = getDependencyname(enumdecla.findCompilationUnit().get());		 
		List<String> dependentyfilename = new ArrayList <String>();  
		if(dependencynames.size() > 0) {
			for (Name nm: dependencynames) {				
				String filename = nm.asString().substring(nm.toString().lastIndexOf(".")+1);
				dependentyfilename.add(filename);			
			}			
		}
		
		HashSet<JavaClass> enumallchild = new HashSet<JavaClass>();	
		 	
		for(ClassOrInterfaceType child: enumcitchildren) {						
			JavaClass enumnewchild;			
			String dependentpackage = "";	
			String field = "";
			try {
				ResolvedReferenceType resolveresult = child.resolve();
				ResolvedReferenceTypeDeclaration resolvedtypedeclaration = resolveresult.getTypeDeclaration();
				dependentpackage = resolvedtypedeclaration.getPackageName();
				String resolvedchildclassname = resolvedtypedeclaration.getClassName();
				if(resolvedchildclassname.contains(".")) {
					while(resolvedchildclassname.contains(".")) {
						String parentclassname = resolvedchildclassname.substring(0,resolvedchildclassname.lastIndexOf("."));
						dependentpackage = dependentpackage + parentclassname;
						resolvedchildclassname=parentclassname;			   
					}
				} 						
			}catch(Exception e){
				if(child.toString().contains(".")) {
					field = child.toString().substring(0, child.toString().indexOf("."));
				}
				else {
					field = child.toString();				 
				}
				//if find the class is imported in importdeclaration
				//if found child dependency directly
				if(dependentyfilename.contains(child.toString())) {
					for(Name dn: dependencynames) {
						if(dn.getIdentifier().equals(child.getNameAsString())) {
							dependentpackage = dn.asString().substring(0, dn.asString().lastIndexOf("."));
							break;
						}								
					}
				}
				else if(dependentyfilename.contains(field) ) {
					for(Name dn: dependencynames) {
						if(dn.getIdentifier().equals(field)) {																	
							dependentpackage = dn.asString().substring(0, dn.asString().lastIndexOf("."));
							dependentpackage = dependentpackage + "." + field;
							break;
						}								
					  }
					}
			}
						
			enumnewchild = new JavaClass(dependentpackage,child.getName().toString());													
			enumallchild.add(enumnewchild);
		}
		interfaces.put(parentenum,enumallchild);
		interfacesandmock.setinterfaces(interfaces);
		super.visit(enumdecla, interfacesandmock);
	}
	
	
	public void visit(ClassOrInterfaceDeclaration classdecla, ClassInterfacesCollector interfacesandmock) {
		String packagename = getCurrentpackagename(classdecla.findCompilationUnit().get());
		String classname = classdecla.getName().toString();
		Map<JavaClass, HashSet<JavaClass>> interfaces = interfacesandmock.getinterfaces();
		//HashSet<JavaClass> mockclass = interfacesandmock.getmockclass();
		/*
		 * find the package name for the current declared class
		 * if a class "ClassB" is declared in a package named "PACK", and in another class named "ClassA"
		 * the packagename of ClassB is: PACK.ClassA
		 */		
		ClassOrInterfaceDeclaration tmpcd=classdecla;
		String parentname = "";
		String parentpackagename = packagename;
		// if find a super-declared class
		while(tmpcd.findAncestor(ClassOrInterfaceDeclaration.class).isPresent()) {
			parentname = tmpcd.findAncestor(ClassOrInterfaceDeclaration.class).get().getNameAsString()+"."+parentname;			 			
			tmpcd = tmpcd.findAncestor(ClassOrInterfaceDeclaration.class).get();			
		} 
		if(parentname.length()>0) {
			parentname = parentname.substring(0, parentname.lastIndexOf("."));
		}
		if(packagename == "") {
			parentpackagename = parentname;
		}
		else {
			if(parentname.length() > 0) {
				parentpackagename = packagename + "." + parentname;
			}			
		}
			
		JavaClass parent = new JavaClass(parentpackagename,classname);	 
		
		HashSet<JavaClass> allchild = new HashSet<JavaClass>();	
		HashSet<JavaClass> mockchild = new HashSet<JavaClass>();	
		
		//get all the dependency for the file
		List<Name> dependencynames = getDependencyname(classdecla.findCompilationUnit().get());		 
		List<String> dependentyfilename = new ArrayList <String>();  
		if(dependencynames.size() > 0) {
			for (Name nm: dependencynames) {				
				String filename = nm.asString().substring(nm.toString().lastIndexOf(".")+1);
				dependentyfilename.add(filename);			
			}			
		}
		
		
		if (classdecla.findAll(ClassOrInterfaceDeclaration.class).size()
				+ classdecla.findAll(ClassOrInterfaceType.class).size() > 1) {
			
			HashSet<ClassOrInterfaceType> citchildren = new HashSet<ClassOrInterfaceType>();
			HashSet<ClassOrInterfaceType> mockchildren = new HashSet<ClassOrInterfaceType>();
			List<HashSet<ClassOrInterfaceType>> allchildren = new ArrayList<HashSet<ClassOrInterfaceType>>();
			allchildren.add(citchildren);
			allchildren.add(mockchildren);
			VoidVisitor<List<HashSet<ClassOrInterfaceType>>>  visitclasstype = new ClasstypeVisitor();
			visitclasstype.visit(classdecla, allchildren);
			citchildren = allchildren.get(0);
			mockchildren = allchildren.get(1);
			if(citchildren.size()>0) {
			HashSet<ClassOrInterfaceType> toremove = new HashSet<ClassOrInterfaceType>();
			for(ClassOrInterfaceType citchild: citchildren) {
				if(mockchildren.contains(citchild)) {
					toremove.add(citchild);
				}
			}
			citchildren.removeAll(toremove);
			}
			
			HashSet<JavaClass> cidchildren = new HashSet<JavaClass>();
			ClassDeclarationVisitor cdv = new ClassDeclarationVisitor();
			cdv.visit(classdecla, cidchildren);			
			
			//add class declared in classdecla
			for(JavaClass newchild1: cidchildren) {
				if(newchild1.equals(parent)) {
					continue;
				}
				allchild.add(newchild1); 				 
			}
			
			// find extends class
			if(classdecla.getExtendedTypes().isNonEmpty()) {
				JavaClass newchild2;
				for(int i = 0; i < classdecla.getExtendedTypes().size(); i++) {
					ClassOrInterfaceType child3name = classdecla.getExtendedTypes().get(i);
				    if(child3name.getScope().isPresent()) {
				    	newchild2 =new JavaClass(child3name.getScope().get().toString(),child3name.getName().asString());
				    	}
					else{
						newchild2 = new JavaClass(packagename,child3name.getName().asString());
					}
					allchild.add(newchild2);
				}				 
			}
			
			//find class type (eg, Class test; test.method1();)
			for(ClassOrInterfaceType child: citchildren) {
				//System.out.println("child: "+ child);				
				JavaClass newchild3;			
				String dependentpackage = "";	
				String field = "";
				String childname = "";
				if(child.toString().contains(".")) {
					field = child.toString().substring(0, child.toString().indexOf("."));
					childname = child.toString().substring(child.toString().lastIndexOf(".")+1);
				}
				else {
					field = child.toString();
					childname = child.getNameAsString();
				}
				boolean declarinthesamefile = false;
				for(JavaClass cidchild: cidchildren) {
					// if this child is declared in the same file
				    if(cidchild.getClassName().equals(child.getName().asString())) {			 
						dependentpackage = packagename;
						declarinthesamefile = true;
						break;
					}				    
				}
				 
				//if find the class is imported in importdeclaration
				//if found child dependency directly
				if(declarinthesamefile == false) {
					try {
						ResolvedReferenceType rrt = child.resolve();
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
						if(dependentyfilename.contains(child.toString())) {
							for(Name dn: dependencynames) {
								if(dn.getIdentifier().equals(child.getNameAsString())) {
									dependentpackage = dn.asString().substring(0, dn.asString().lastIndexOf("."));
									break;
								}								
							}
						}
						else if(dependentyfilename.contains(field) ) {
							for(Name dn: dependencynames) {
								if(dn.getIdentifier().equals(field)) {																	
									dependentpackage = dn.asString().substring(0, dn.asString().lastIndexOf("."));
									dependentpackage = dependentpackage + "." + field;
									break;
								}								
							  }
							}
											
						else if(classname.contains("Test") && childname.equals(classname.replace("Test", ""))) {
							dependentpackage = packagename;						 
						}
						//if this child is extends
						else if(child.getParentNode().isPresent()) {
							Node p = child.getParentNode().get();
							if(p.findFirst(ClassOrInterfaceDeclaration.class).isPresent()) {
								ClassOrInterfaceDeclaration c = p.findFirst(ClassOrInterfaceDeclaration.class).get();
								if(c.getExtendedTypes().isNonEmpty()) {
									ClassOrInterfaceType cit = c.getExtendedTypes().get(0);
									if(cit.equals(child)) {
										if(child.toString().contains(".")) {
											if(child.getScope().isPresent()) {
												dependentpackage = child.getScope().get().toString();
											}
											
										}
										else{
											dependentpackage = packagename;
										}
									}
								}
							}					 
						}
					}
					}//end catch	
			
				//add class called in classdecla
				newchild3 = new JavaClass(dependentpackage,child.getName().toString());													
				allchild.add(newchild3);	
		
			}	
			
			//find class type (eg, Class test; test.method1();)
			//TODO: add resolve by javaparser
			for(ClassOrInterfaceType childmock: mockchildren) {				
				JavaClass newchildmock;			
				String dependentpackage = "";	
				String field = "";
				String childname = "";
				if(childmock.toString().contains(".")) {
					field = childmock.toString().substring(0, childmock.toString().indexOf("."));
					childname = childmock.toString().substring(childmock.toString().lastIndexOf(".")+1);
				}
				else {
					field = childmock.toString();
					childname = childmock.getNameAsString();
				}
				//if find the class is imported in importdeclaration
				//if found child dependency directly
				if(dependentyfilename.contains(childmock.toString())) {
					for(Name dn: dependencynames) {
						if(dn.getIdentifier().equals(childmock.getNameAsString())) {
							dependentpackage = dn.asString().substring(0, dn.asString().lastIndexOf("."));
							break;
						}								
					}
				}
				else if(dependentyfilename.contains(field) ) {
					for(Name dn: dependencynames) {
						if(dn.getIdentifier().equals(field)) {																	
							dependentpackage = dn.asString().substring(0, dn.asString().lastIndexOf("."));
							dependentpackage = dependentpackage + "." + field;
							break;
						}								
					  }
					}
				// if this child is declared in the same file
				else if(cidchildren.toString().contains(childmock.getName().asString())) {			 
					dependentpackage = packagename;					
				}
				//if this child is extends
				else if(childmock.getParentNode().isPresent()) {
					Node p = childmock.getParentNode().get();
					if(p.findFirst(ClassOrInterfaceDeclaration.class).isPresent()) {
						ClassOrInterfaceDeclaration c = p.findFirst(ClassOrInterfaceDeclaration.class).get();
						if(c.getExtendedTypes().isNonEmpty()) {
							ClassOrInterfaceType cit = c.getExtendedTypes().get(0);
							if(cit.equals(childmock)) {
								if(childmock.toString().contains(".")) {
									if(childmock.getScope().isPresent()) {
										dependentpackage = childmock.getScope().get().toString();
									}
									
								}
								else{
									dependentpackage = packagename;
								}
							}
						}
					}
					 
				}
			
				//add class called in classdecla
				newchildmock = new JavaClass(dependentpackage,childname);													
				mockchild.add(newchildmock);			
			}
			
	 	}	
		
		   interfaces.put(parent, allchild); 
		   
		   interfacesandmock.setinterfaces(interfaces);
		   interfacesandmock.setmockclass(mockchild);
		   super.visit(classdecla, interfacesandmock);		
	}
	
	
	public String getCurrentpackagename(CompilationUnit cu) {
		String packagename = "";
		Optional<PackageDeclaration> packagedecla = cu.getPackageDeclaration();
		if (packagedecla.isPresent()) {
			packagename = cu.getPackageDeclaration().get().getName().asString();
		}
		return packagename;		
	}

	public JavaClass getrootclass(CompilationUnit cu) {
		String classname = cu.findFirst(ClassOrInterfaceDeclaration.class).get().getName().toString();
		String packagename = getCurrentpackagename(cu);	
		JavaClass firstclass = new JavaClass(packagename,classname);
		return firstclass;		
	}
	
	/*
	 * If one java classA want to call another java classB, first it need to import  java classB. 
	 * we find dependencies by check all the ImportDeclaration and retrieves the name of the import (.* is not included.)
	 *  
	 */
	public List<Name> getDependencyname(CompilationUnit cu) {
		 List<ImportDeclaration> packages = cu.findAll(ImportDeclaration.class);
		 List<Name> packagenames = new ArrayList<Name>();
		 if(packages.size() > 0) {
			 for (ImportDeclaration packagee: packages) {					
				 packagenames.add(packagee.getName());		
			 }	
		 }
		  		 
		 return packagenames;
	}
	
	
	
}
