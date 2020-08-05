package visitor;

import java.util.HashSet;
import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;

import util.JavaClass;

public class ClassDeclarationVisitor extends VoidVisitorAdapter<HashSet<JavaClass>>{
	
	@Override
	public void visit(ClassOrInterfaceDeclaration cd,HashSet<JavaClass> jc) {
		CompilationUnit cu = cd.findCompilationUnit().get();
		String packagename = "";
		String classname = "";
		try {			
			ResolvedReferenceTypeDeclaration resolveDecla = cd.resolve();			
			String truepackagename = resolveDecla.getPackageName();
			String resolvename = resolveDecla.getName();
			if(resolvename.contains(".")) {
				String parentclassname = resolvename.substring(0, resolvename.lastIndexOf("."));
				packagename = truepackagename+"."+ parentclassname;
				
			}else {
				packagename = truepackagename;
			}			
			
		}catch(Exception e) {
			
			Optional<PackageDeclaration> packagedecla = cu.getPackageDeclaration();
			if (packagedecla.isPresent()) {
				packagename = cu.getPackageDeclaration().get().getName().asString();
			}
			
			ClassOrInterfaceDeclaration tmpcd=cd;
			//System.out.println("cd: "+ cd.getName().asString());
			String parentname = "";
			while(tmpcd.findAncestor(ClassOrInterfaceDeclaration.class).isPresent()) {
				parentname = tmpcd.findAncestor(ClassOrInterfaceDeclaration.class).get().getNameAsString()+"."+parentname;			 			
				tmpcd = tmpcd.findAncestor(ClassOrInterfaceDeclaration.class).get();			
			}
		 
			if(parentname.length()>0) {
				parentname = parentname.substring(0, parentname.lastIndexOf("."));
			}
			if(packagename == "") {
				packagename = parentname;
			}
			else {
				if(parentname.length() > 0) {
					packagename = packagename + "." + parentname;
				}			
			}
		}//end catch
		
		classname = cd.getName().asString();	 	 				
		JavaClass javac = new JavaClass(packagename,classname);
		jc.add(javac);
		//System.out.println(javac);
		super.visit(cd, jc);	
	}
	
	
	@Override
	public void visit(EnumDeclaration enumdecla,HashSet<JavaClass> enumdeclaJavaClass) {
		String packagename = "";
		CompilationUnit cu = enumdecla.findCompilationUnit().get();
		try {			
			ResolvedReferenceTypeDeclaration resolveDecla = enumdecla.resolve();			
			String truepackagename = resolveDecla.getPackageName();
			String resolvename = resolveDecla.getName();
			if(resolvename.contains(".")) {
				String parentclassname = resolvename.substring(0, resolvename.lastIndexOf("."));
				packagename = truepackagename+"."+ parentclassname;
				
			}else {
				packagename = truepackagename;
			}			
			
		}catch(Exception e) {
			
			Optional<PackageDeclaration> packagedecla = cu.getPackageDeclaration();
			if (packagedecla.isPresent()) {
				packagename = cu.getPackageDeclaration().get().getName().asString();
			}
			
			ClassOrInterfaceDeclaration tmpcd= new ClassOrInterfaceDeclaration();
			try{
				tmpcd = enumdecla.asClassOrInterfaceDeclaration();
			}
			catch(Exception e1) {
				//do nothing;
			}
			String parentname = "";
			while(tmpcd.findAncestor(ClassOrInterfaceDeclaration.class).isPresent()) {
				parentname = tmpcd.findAncestor(ClassOrInterfaceDeclaration.class).get().getNameAsString()+"."+parentname;			 			
				tmpcd = tmpcd.findAncestor(ClassOrInterfaceDeclaration.class).get();			
			}
		 
			if(parentname.length()>0) {
				parentname = parentname.substring(0, parentname.lastIndexOf("."));
			}
			if(packagename == "") {
				packagename = parentname;
			}
			else {
				if(parentname.length() > 0) {
					packagename = packagename + "." + parentname;
				}			
			}
		}//end catch	
		String classname = enumdecla.getName().toString();
		JavaClass parentenum = new JavaClass(packagename,classname);	
		enumdeclaJavaClass.add(parentenum);
	 super.visit(enumdecla, enumdeclaJavaClass);
}
		 		
}
