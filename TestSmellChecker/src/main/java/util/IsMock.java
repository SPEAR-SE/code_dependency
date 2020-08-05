package util;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;

public class IsMock {
	public List<String >getmockkeywords(CompilationUnit cu){
		List<String> mockkeywords = new ArrayList<String>();
		mockkeywords.add("mock");
		mockkeywords.add("createMock");
		mockkeywords.add("createMockBuilder");
		mockkeywords.add("createStrictMock");
		//mockkeywords.add("");
		if(cu != null && cu.getImports() !=null) {
			NodeList<ImportDeclaration> importdeclarations = cu.getImports();
			for(ImportDeclaration importdecla :importdeclarations  ) {
				Name importname = importdecla.getName();
				if(importname.getQualifier().isPresent()) {
					String qualifier = importname.getQualifier().get().asString();
					String identifier = importname.getIdentifier();
					if(qualifier.contains("org.mockito")
							|| qualifier.contains("org.easymock")
							|| qualifier.contains("org.powermock")
							|| qualifier.contains("com.squareup.okhttp.mockwebserver")) {
						mockkeywords.add(identifier);
					}
				}
			}
		}
		return mockkeywords;
	}
	public boolean ismock(MethodCallExpr methodcall) {
		boolean ismock = false;
		String methodcallname = "";
		List<String> mockkeywords = new ArrayList<String>();
		if(methodcall.findCompilationUnit().isPresent()) {
			CompilationUnit cu = methodcall.findCompilationUnit().get();
			mockkeywords = getmockkeywords(cu);
			methodcallname = methodcall.getName().getIdentifier();
			
			if(mockkeywords.size() > 0) {
				for(String mockkeyword: mockkeywords) {
					if(methodcallname.equals(mockkeyword)) {
						ismock = true;
						break;
					}
				}
			}
			
		}

		
		return ismock;
	}
	
	public boolean ismockvariable(VariableDeclarator variableexp) {
		boolean ismock = false;
		if(!variableexp.findAll(MethodCallExpr.class).isEmpty()) {
			for(MethodCallExpr mce:variableexp.findAll(MethodCallExpr.class) ) {
				if(ismock(mce)) {
					ismock = true;
				}
			}			
		}
		return ismock;
	}
	
	public boolean ismockfield(FieldDeclaration fielddecla) {
		boolean ismock = false;
		if(fielddecla.getAnnotations().isNonEmpty()) {
			NodeList<AnnotationExpr> annotationexps = fielddecla.getAnnotations();
			for(AnnotationExpr annotation: annotationexps) {
				if(annotation.getNameAsString().equals("Mock")||annotation.getNameAsString().equals("MockBean")) {
					ismock = true;
					break;
				}
			}
		}
		return ismock;
	}

	
	/*
	 *  find mock in  : mock(classtype.class)
	 *  find mock in :  classtype a = mock();
	 *  find mock in : @mock
	 */
	public boolean IsmockObject(Node object) {
		boolean ismock = false;
		
		MethodCallExpr parentMethod = null ;
		if(object.findAncestor(MethodCallExpr.class).isPresent()) {
			parentMethod = object.findAncestor(MethodCallExpr.class).get();
		}
		VariableDeclarationExpr parentvariable = null;
		if(object.findAncestor(VariableDeclarationExpr.class).isPresent()) {
			parentvariable = object.findAncestor(VariableDeclarationExpr.class).get();
		}
		FieldDeclaration parentfielddecla = null;
		if(object.findAncestor(FieldDeclaration.class).isPresent()) {
			parentfielddecla = object.findAncestor(FieldDeclaration.class).get();
		}
		//check whether object is mocked in method: mock(object.class)				
		if(parentMethod != null) {
			if(ismock(parentMethod) && parentMethod.getArguments().isNonEmpty()) {
				NodeList<Expression> methodargs = parentMethod.getArguments();
				for(Expression methodarg: methodargs) {
					if(methodarg.containsWithin(object)) {
						//System.out.println(" mock in method: "+ object);
						ismock = true;					
						break;
					}
				}						
			}		
		}
		
		//check whether object is in variable declaration expression : object a = mock();
		if(parentvariable != null) {
			if(parentvariable.getVariables().isNonEmpty()) {
				NodeList<VariableDeclarator> parentvar = parentvariable.getVariables();
				for(VariableDeclarator variabledecla: parentvar) {
					if(ismockvariable(variabledecla) && variabledecla.getType().isClassOrInterfaceType()) {
						if(variabledecla.getType().equals(object)) {							
							ismock = true;						
							break;
						}								
					}							
				}
			}
		}
		
		//check whether object is mocked in field : @Mock	
		if(parentfielddecla != null) {
			if(ismockfield(parentfielddecla)) {
				ismock = true;			
			}
		}
		
		return ismock;
	}
	
	
}
