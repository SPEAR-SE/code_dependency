package visitor;

import java.util.HashSet;

import java.util.Optional;


import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import its.datastructure.Method;

public class MethodDeclarationVisitor extends VoidVisitorAdapter<HashSet<Method>> {
	public void visit(MethodDeclaration md, HashSet<Method> declaredmethods) {
		
		String packagename = "";
		Optional<PackageDeclaration> packagee = md.findRootNode().findFirst(PackageDeclaration.class);
		if(packagee.isPresent()) {
			packagename = packagee.get().getName().toString();
		}
		String methodname = md.getNameAsString();	
		String classname = "";
		if(md.findAncestor(ClassOrInterfaceDeclaration.class).isPresent()) {
			classname = md.findAncestor(ClassOrInterfaceDeclaration.class).get().getNameAsString();	
		}
		String signature = md.getSignature().asString();
		//String returntype = md.resolve().getReturnType().describe();
	    Method declaredmethod= new Method(packagename,classname,methodname,signature);	
	    declaredmethods.add(declaredmethod);
		//System.out.println("in methoddecla: " + declaredmethods);
	    super.visit(md, declaredmethods);
	}
}
