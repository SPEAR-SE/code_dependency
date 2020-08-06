package util;

import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.AnnotationExpr;

public class JunitCount {
	public int countistestNumber(CompilationUnit cu) {
		int istestNumber = 0;
		if(cu!=null) {
			List<AnnotationExpr> annotations = cu.findAll(AnnotationExpr.class);
			if(annotations.size() > 0) {
				for(AnnotationExpr annotation: annotations) {
					if(annotation.getNameAsString().equals("Test")) {
						istestNumber++;
					}
				}
			}			
		}
		return istestNumber;
	}
	
	public int countbeforeClassNumber(CompilationUnit cu) {
		int beforeClass = 0;
		if(cu!=null) {
			List<AnnotationExpr> annotations = cu.findAll(AnnotationExpr.class);
			if(annotations.size() > 0) {
				for(AnnotationExpr annotation: annotations) {
					if(annotation.getNameAsString().equals("BeforeClass")) {
						beforeClass++;
					}
				}
			}			
		}
		return beforeClass;
	}
	
	public int countbeforeNumber(CompilationUnit cu) {
		int beforeNumber = 0;
		if(cu!=null) {
			List<AnnotationExpr> annotations = cu.findAll(AnnotationExpr.class);
			if(annotations.size() > 0) {
				for(AnnotationExpr annotation: annotations) {
					if(annotation.getNameAsString().equals("Before")||
							annotation.getNameAsString().equals("BeforeEach")) {
						beforeNumber++;
					}
				}
			}			
		}
		return beforeNumber;
	}
	
	public int countafterNumber(CompilationUnit cu) {
		int afterNumber = 0;
		if(cu!=null) {
			List<AnnotationExpr> annotations = cu.findAll(AnnotationExpr.class);
			if(annotations.size() > 0) {
				for(AnnotationExpr annotation: annotations) {
					if(annotation.getNameAsString().equals("After")||
							annotation.getNameAsString().equals("AfterEach")) {
						afterNumber++;
					}
				}
			}			
		}
		return afterNumber;
	}
	
	public int countafterClassNumber(CompilationUnit cu) {
		int afterClassNumber = 0;
		if(cu!=null) {
			List<AnnotationExpr> annotations = cu.findAll(AnnotationExpr.class);
			if(annotations.size() > 0) {
				for(AnnotationExpr annotation: annotations) {
					if(annotation.getNameAsString().equals("AfterClass")) {
						afterClassNumber++;
					}
				}
			}			
		}
		return afterClassNumber;
	}
}
