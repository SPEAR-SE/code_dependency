package util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.type.ClassOrInterfaceType;

import its.datastructure.JavaClass;

public class GetClassExtends {
	public HashSet<JavaClass> getextends(CompilationUnit cu) {		
		HashSet<JavaClass> extendsclasses = new HashSet<JavaClass>();
		if(cu == null) {
			return extendsclasses;
		}
		GetDependency getdependency = new GetDependency();
		List<Name> depends = getdependency.getDependencyname(cu);
		
		GetRootClass grc = new GetRootClass();
		JavaClass rootclass = grc.getRootJavaClass(cu);
		String rootclassname ="";
		if(rootclass !=null) {
			rootclassname= rootclass.getClassName();
		}
		
		String packagename = "";		
		Optional<PackageDeclaration> packagedecla = cu.getPackageDeclaration();
		if (packagedecla.isPresent()) {
			packagename = cu.getPackageDeclaration().get().getName().asString();
		}
			
		List<ClassOrInterfaceDeclaration> classdeclarations = cu.findAll(ClassOrInterfaceDeclaration.class);
		if(classdeclarations.size() > 0) {
			for(ClassOrInterfaceDeclaration classdecla : classdeclarations) {
			
				if(classdecla.getExtendedTypes().isEmpty()) {
					continue;
				}
				//if there is some extends
				 NodeList<ClassOrInterfaceType> ns = classdecla.getExtendedTypes();		
				 if(ns.isNonEmpty()) {
					 for(int i = 0; i < ns.size();i++) {
						 ClassOrInterfaceType ci = ns.get(i);						 
						 if(ci.getName().asString().equals(rootclassname)) {
							 continue;
						 }
						 String classname = "";
						 if(ci.toString().contains(".")) {
							 if(ci.getScope().isPresent()) {
								 packagename = ci.getScope().get().asString();
							 }
							 						 
							 classname = ci.getName().asString();
							 
						 }
						 else {
							classname = ci.getName().asString();
						 }
						 
						 if(depends.size() > 0) {
							 for(Name depend: depends) {
								 if(depend.getIdentifier().equals(classname)) {
									 Optional<Name> packname = depend.getQualifier();
									 if(packname.isPresent()) {
										 packagename = packname.get().asString();
										 break;
									 }							 
								 }							 
							 }
						 }
						 
						 
						 JavaClass extendclass = new JavaClass(packagename,classname);

						 extendsclasses.add(extendclass);
					 }
				 }
			}
		}
		
		return extendsclasses;
	}
	
	
}
