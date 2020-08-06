package util;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.expr.Name;

public class GetDependency {
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
