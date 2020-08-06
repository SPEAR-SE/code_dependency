package visitor;

import java.util.HashSet;
import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import its.datastructure.JavaClass;

public class enumVisitor extends VoidVisitorAdapter<HashSet<JavaClass>>{
	public void visit(EnumDeclaration enumdecla,HashSet<JavaClass> enumdeclaJavaClass) {
		String packagename = "";
		CompilationUnit cu = enumdecla.findCompilationUnit().get();
		if(cu != null) {
			Optional<PackageDeclaration> packagedecla = cu.getPackageDeclaration();
			if (packagedecla.isPresent()) {
				packagename = cu.getPackageDeclaration().get().getName().asString();
			}
		}		
		String classname = enumdecla.getName().toString();
		JavaClass parentenum = new JavaClass(packagename,classname);	
		enumdeclaJavaClass.add(parentenum);
	 super.visit(enumdecla, enumdeclaJavaClass);
}
}