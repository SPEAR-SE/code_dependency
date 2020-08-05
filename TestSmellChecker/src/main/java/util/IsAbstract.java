package util;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

public class IsAbstract {
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

}
