package util;

import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;

import its.datastructure.JavaClass;

public class GetRootClass {
public JavaClass getRootJavaClass(CompilationUnit cu) {
		
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
		return rootjavaclass;
	}

}
