package visitor;

import java.util.HashMap;
import java.util.HashSet;

import java.util.Optional;
import com.github.javaparser.ast.PackageDeclaration;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import its.datastructure.Method;

public class MethodDeclarationInterfaceVisitor extends VoidVisitorAdapter<HashMap<Method,HashSet<Method>>> {
	public void visit(MethodDeclaration md, HashMap<Method,HashSet<Method>> methodinterfaces) {
		HashSet<Method> methodcollector = new HashSet<Method>();	
		String packagename = "";
		Optional<PackageDeclaration> packagee = md.findRootNode().findFirst(PackageDeclaration.class);
		if(packagee.isPresent()) {
			packagename = packagee.get().getName().toString();
		}
		String methodname = md.getNameAsString();	
		Optional<ClassOrInterfaceDeclaration> ancestorclass = md.findAncestor(ClassOrInterfaceDeclaration.class);
		String classname = "";
		if(ancestorclass.isPresent()) {
			classname = ancestorclass.get().getNameAsString();		
			
		Optional<ClassOrInterfaceDeclaration> superancestorclass = ancestorclass.get().findAncestor(ClassOrInterfaceDeclaration.class);//.get().getNameAsString();	
		while(superancestorclass.isPresent()) {
			classname = superancestorclass.get().getNameAsString() + "." + classname;
			superancestorclass = superancestorclass.get().findAncestor(ClassOrInterfaceDeclaration.class);			
		}
		}
		String signature = md.getSignature().asString();
		//String returntype = md.resolve().getReturnType().describe();
	    Method declaredmethod= new Method(packagename,classname,methodname,signature);
	  
		// collect all called-method in this method
		VoidVisitor<HashSet<Method>> methodcallvisitor = new MethodCallVisitor();
		HashSet<Method> methodcalledcollector = new HashSet<Method>();
		methodcallvisitor.visit(md, methodcalledcollector);
		methodcollector.addAll(methodcalledcollector); //collect called method
		
		methodinterfaces.put(declaredmethod, methodcalledcollector);
		
			
	    super.visit(md, methodinterfaces);
	}
}
