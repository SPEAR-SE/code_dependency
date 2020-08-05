package visitor;

 
import java.util.List;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
 
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;


 
public class MethodVisitor {
	
	// return only helper method(i.e, the one without @Test annotation)	
	public class HelperMethodVisitor extends VoidVisitorAdapter<List<MethodDeclaration>> {				
		@Override
		public void visit(MethodDeclaration method, List<MethodDeclaration> helpermethods ) {
			if(method.getAnnotations().isEmpty()) {
				helpermethods.add(method);
			}
			else {
				NodeList<AnnotationExpr> parentfielddecla = null;	
				parentfielddecla = method.getAnnotations();
				boolean test = false;
				for(int i = 0;i < parentfielddecla.size();i++) {			
					AnnotationExpr aeach = parentfielddecla.get(i);
					if(aeach.toString().contains("Test")) {
						test = true;
						break;
					}
				}
				if(test == false) {
					helpermethods.add(method);
				}				 
			}
				
			super.visit(method, helpermethods);
		}
	}
	
	
	//collect test fixture
	/*
	 * testfixture[0] is @BeforeClass
	 * testfixture[1] is @Before || @BeforeEach
	 * testfixture[2] is @AfterClass
	 * testfixture[3] is @After ||@AfterEach
	 * 
	 */
	public class TestFixtureMethodVisitor extends VoidVisitorAdapter<List<MethodDeclaration>[]>{
		@Override
		public void visit(MethodDeclaration method, List<MethodDeclaration>[] testfixture ) {
			if(method.getAnnotations().isNonEmpty()) {
				NodeList<AnnotationExpr> parentfielddecla = null;	
				parentfielddecla = method.getAnnotations();				
				for(int i=0;i < parentfielddecla.size();i++) {			
					AnnotationExpr aeach = parentfielddecla.get(i);
					if(aeach.getNameAsString().equals("BeforeClass")) {
						testfixture[0].add(method);
					}else if(aeach.getNameAsString().equals("Before") ||aeach.getNameAsString().equals("BeforeEach")) {
						testfixture[1].add(method);
					}else if(aeach.getNameAsString().equals("AfterClass")) {
						testfixture[2].add(method);
					}else if(aeach.getNameAsString().equals("After") || aeach.getNameAsString().equals("AfterEach")) {
						testfixture[3].add(method);
					}
				}					 			 
			}
		}
		
	}

	
}
