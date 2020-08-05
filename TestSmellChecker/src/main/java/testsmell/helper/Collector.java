package testsmell.helper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;

import util.JavaClass;
import util.GetCU;
import visitor.MethodVisitor.HelperMethodVisitor;
import visitor.MethodVisitor.TestFixtureMethodVisitor;
import visitor.MethodVisitor;

public class Collector {
	private MethodVisitor methodvisitor = new MethodVisitor();
	HelperMethodVisitor visitmethod = methodvisitor.new HelperMethodVisitor();
	TestFixtureMethodVisitor visittestfixture = methodvisitor.new TestFixtureMethodVisitor();
	
	//get all the helper methods declared in testfile;
	public List<MethodDeclaration> collectFileHelperMethod(File testfile,HashMap<File, HashSet<JavaClass>> extendgraph,HashMap<JavaClass,File>filejavaMapping){			
		GetCU getcu = new GetCU();
		CompilationUnit cu = getcu.getCu(testfile);	
		if(cu == null) {
			return null;
		}
				
		//get helper method that declared in this file
		List<MethodDeclaration> declarations = new ArrayList<MethodDeclaration>();
		visitmethod.visit(cu, declarations);
		
		//get helper methods that it inherit
		HashSet<JavaClass> ancestors = extendgraph.get(testfile);
		if(ancestors.size() > 0) {
			for(JavaClass jc: ancestors) {
				File parentfile = new File("");
				if(filejavaMapping.keySet().contains(jc)) {
					parentfile = filejavaMapping.get(jc);
				}				
				if(parentfile.exists()) {
					CompilationUnit parentcu = getcu.getCu(parentfile);
					if(parentcu == null) {
						continue;
					}
					visitmethod.visit(parentcu,declarations);
				}
				
			}	
		}
	
		
		return declarations;		
	}
	
	
	public List<MethodDeclaration> getOverrideMethod(File testfile) {
		List<MethodDeclaration> overridemethod = new ArrayList<MethodDeclaration>();
	
		GetCU getcu = new GetCU();
		CompilationUnit cu = getcu.getCu(testfile);	
		if(cu == null) {
			return null;
		}
		
		List<MethodDeclaration> declarations = cu.findAll(MethodDeclaration.class);
		if(declarations.isEmpty()) {
			return overridemethod;
		}
		for(MethodDeclaration method: declarations) {
			NodeList<AnnotationExpr> parentfielddecla = null;	
			parentfielddecla = method.getAnnotations();
			for(int i = 0;i < parentfielddecla.size();i++) {			
				AnnotationExpr aeach = parentfielddecla.get(i);
				if(aeach.toString().contains("Override")) {
					overridemethod.add(method);
				}
			}
					
		}		
		return overridemethod;
	}
	
	
	 public List<MethodDeclaration>[] getTestFixture(File testfile) {
		List<MethodDeclaration> element = new ArrayList<MethodDeclaration>();
		List<MethodDeclaration>[] testfixture = new List[4];
		for(int i = 0; i < 4; i++) {
			testfixture[i] = element;
		}
		GetCU getcu = new GetCU();
		CompilationUnit cu = getcu.getCu(testfile);		
		visittestfixture.visit(cu, testfixture);
		return testfixture;		
	}
	 
	

}
