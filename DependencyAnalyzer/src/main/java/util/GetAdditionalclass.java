package util;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


//import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import com.github.javaparser.ast.expr.Expression;

import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.Name;

import com.github.javaparser.ast.expr.SimpleName;

import its.datastructure.JavaClass;
import visitor.ClassInterfacesCollector;
/* get other interface/parent by import statement
 * 'other' means classes that cannot identitify by javaparser
 * for example, javaparser cannot identify classes like below:
 * import an class :               import packagename.classname;
 * then use this class directly:   classname.methodname();
 */
public class GetAdditionalclass {
	public boolean isClass(String dependname,List<File> projectfiles) {
		boolean isclass = false;
		if(projectfiles != null) {
			for(File fileinfo: projectfiles) {
				String filename = fileinfo.getName().replaceAll(".java", "");
				if(filename.equals(dependname)) {
					isclass = true;
					break;
				}
			}
		}
		return isclass;
	}
	
	public ClassInterfacesCollector getotherjavaclass(CompilationUnit cu, List<Name> dependname,
			ClassInterfacesCollector cifc,List<File> projectfiles){
				
		Map<JavaClass, HashSet<JavaClass>> interfacee = cifc.getinterfaces();
		HashSet<JavaClass> mockclass = cifc.getmockclass();
		String currentpackagename = "";
		if(cu.getPackageDeclaration().isPresent()) {
			currentpackagename = cu.getPackageDeclaration().get().getNameAsString();
		}
		ClassOrInterfaceDeclaration root = new ClassOrInterfaceDeclaration();
		if(cu.findFirst(ClassOrInterfaceDeclaration.class).isPresent()) {
			root = cu.findFirst(ClassOrInterfaceDeclaration.class).get();
		}
		for(Name dn: dependname) {

			String importidentif = dn.getIdentifier();
			String importqualifier = "";
			
			if(dn.getQualifier().isPresent()) {
				importqualifier = dn.getQualifier().get().toString();
			}
			JavaClass calClass = new JavaClass(importqualifier,importidentif);
			//ClassOrInterfaceType caltype = JavaParser.parseClassOrInterfaceType(importidentif) ;
			/*
			 * if class is not detected by javaparser
			 * and the imported class may called in the compiliant unit
			 */				
			if((interfacee.values().toString().contains(calClass.toString())==false) &&
					root.toString().contains(importidentif) ) {		//&& isClass(importidentif,projectfiles)									
				IsMock mock = new IsMock();								
				List<SimpleName> names = cu.findAll(SimpleName.class);
				if(names.isEmpty() == false) {
					for (SimpleName name: names) {
						if(name.getIdentifier().equals(importidentif)) {
							JavaClass newcalljavaclass = new JavaClass(importqualifier,importidentif);
							boolean ismock2 = false;					
							ismock2 = mock.IsmockObject(name);
							if(ismock2 == true) {
								mockclass.add(newcalljavaclass);
							}							
							else {								
							// find parent								
							String AncestorDeclaredClass = "";
							ClassOrInterfaceDeclaration parentclass = new ClassOrInterfaceDeclaration();
							if(name.findAncestor(ClassOrInterfaceDeclaration.class).isPresent()) {
								parentclass = name.findAncestor(ClassOrInterfaceDeclaration.class).get();
								AncestorDeclaredClass = parentclass.getName().asString();
							}		
							String parentpackagename = getParentClassDeclaration(parentclass);
							if(parentpackagename.length() > 0) {
								parentpackagename = currentpackagename +"."+parentpackagename;
								}
							else {
								parentpackagename = currentpackagename;
								}								
							JavaClass newparent = new JavaClass(parentpackagename,AncestorDeclaredClass);
							if(newparent.toString().equals("")) {
								continue;
							}
							else {
								if(interfacee.containsKey(newparent)) {
									interfacee.get(newparent).add(newcalljavaclass);
								}
								else {							
								    HashSet<JavaClass> tmpinterface = new HashSet<JavaClass>();
								    tmpinterface.add(newcalljavaclass);
									interfacee.put(newparent, tmpinterface);
								}
								 
							}
							
							//keep finding for parent-parent
						    ClassOrInterfaceDeclaration tmpparent = parentclass;
							while (tmpparent.findAncestor(ClassOrInterfaceDeclaration.class).isPresent()) {	
								  
								 tmpparent = tmpparent.findAncestor(ClassOrInterfaceDeclaration.class).get();
								 String tmpparentname = tmpparent.getName().asString();
									
									String tmpparentpackage = "";
									String parparentpackagename = getParentClassDeclaration(tmpparent);
									if(parparentpackagename.length() > 0) {
										tmpparentpackage = currentpackagename +"."+parparentpackagename;
										}
									else {
										tmpparentpackage = currentpackagename;
										}
									
								JavaClass newtmpparent = new JavaClass(tmpparentpackage,tmpparentname);
									if(newtmpparent.toString().equals("")) {
										continue;
									}
									else {
										if(interfacee.containsKey(newtmpparent)) {
											interfacee.get(newtmpparent).add(newcalljavaclass);
										}
										else {							
										    HashSet<JavaClass> tmpinterface = new HashSet<JavaClass>();
										    tmpinterface.add(newcalljavaclass);
											interfacee.put(newtmpparent, tmpinterface);
										}								 
									}
									
								}
								
								
								}
 

							}							
							
						}
					 
				}
				
				List<MethodCallExpr> methodcalls = cu.findAll(MethodCallExpr.class);
				for(MethodCallExpr md: methodcalls) {
					
					String callclassname = "";
					if(md.getScope().isPresent()) {			
					Expression methodcallclass = md.getScope().get();
					if(methodcallclass.isNameExpr()) {
					 callclassname = methodcallclass.asNameExpr().getNameAsString();				 
					}
					if(methodcallclass.isFieldAccessExpr()) {
						callclassname = methodcallclass.asFieldAccessExpr().getNameAsString();
					}	

					if(callclassname.equals(importidentif)) {
						JavaClass newcalljavaclass = new JavaClass(importqualifier,importidentif);
						boolean ismock1 = false;
						ismock1 = mock.IsmockObject(md);
						if(ismock1==true) {
							mockclass.add(newcalljavaclass);
						}
						else {
							// the import class is used						
							
							// find parent
							String AncestorDeclaredClass = "";
							ClassOrInterfaceDeclaration parentclass = new ClassOrInterfaceDeclaration();
							if(methodcallclass.findAncestor(ClassOrInterfaceDeclaration.class).isPresent()) {
								parentclass = methodcallclass.findAncestor(ClassOrInterfaceDeclaration.class).get();
								AncestorDeclaredClass = parentclass.getNameAsString();
							}
							String parentpackagename = getParentClassDeclaration(parentclass);
							if(parentpackagename.length() > 0) {
								parentpackagename = currentpackagename +"."+parentpackagename;
								}
							else {
								parentpackagename = currentpackagename;
								}
							
							JavaClass newparent = new JavaClass(parentpackagename,AncestorDeclaredClass);
							if(newparent.toString().equals("")) {
								continue;
							}
							else {
								if(interfacee.containsKey(newparent)) {
									interfacee.get(newparent).add(newcalljavaclass);
								}
								else {							
								    HashSet<JavaClass> tmpinterface = new HashSet<JavaClass>();
								    tmpinterface.add(newcalljavaclass);
									interfacee.put(newparent, tmpinterface);
								}							
							}
							
							//keep finding for parent-parent
						    ClassOrInterfaceDeclaration tmpparent = parentclass;
							while (tmpparent.findAncestor(ClassOrInterfaceDeclaration.class).isPresent()) {	
								  
								 tmpparent = tmpparent.findAncestor(ClassOrInterfaceDeclaration.class).get();
								 String tmpparentname = tmpparent.getName().asString();
									
									String tmpparentpackage = "";
									String parparentpackagename = getParentClassDeclaration(tmpparent);
									if(parparentpackagename.length() > 0) {
										tmpparentpackage = currentpackagename +"."+parparentpackagename;
										}
									else {
										tmpparentpackage = currentpackagename;
										}
									
								JavaClass newtmpparent = new JavaClass(tmpparentpackage,tmpparentname);
									if(newtmpparent.toString().equals("")) {
										continue;
									}
									else {
										if(interfacee.containsKey(newtmpparent)) {
											interfacee.get(newtmpparent).add(newcalljavaclass);
										}
										else {							
										    HashSet<JavaClass> tmpinterface = new HashSet<JavaClass>();
										    tmpinterface.add(newcalljavaclass);
											interfacee.put(newtmpparent, tmpinterface);
										}								 
									}
									
								}
						}

						
					}						
					}
				}
					
			}
			
			
		}
		cifc.setinterfaces(interfacee);
		cifc.setmockclass(mockclass);
		return cifc;
}
	
	
   public String getParentClassDeclaration(ClassOrInterfaceDeclaration parentclass) {
	   String parentpackagename = "";
		ClassOrInterfaceDeclaration newparentclass = parentclass;
		while(newparentclass.findAncestor(ClassOrInterfaceDeclaration.class).isPresent()) {
			parentpackagename = newparentclass.findAncestor(ClassOrInterfaceDeclaration.class).get().getNameAsString()+"."+parentpackagename;			 			
			newparentclass = newparentclass.findAncestor(ClassOrInterfaceDeclaration.class).get();			
		}
		if(parentpackagename.length() > 0) {
			parentpackagename = parentpackagename.substring(0, parentpackagename.lastIndexOf("."));
		}
		return parentpackagename ;
   }
	

}
