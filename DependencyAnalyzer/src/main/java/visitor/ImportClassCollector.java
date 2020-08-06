package visitor;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import its.datastructure.JavaClass;
import util.getFileInfoInSystem;

public class ImportClassCollector extends VoidVisitorAdapter< List<Name>> {
	public void visit(ImportDeclaration dependency, List<Name> dependname ) {
		dependname.add(dependency.getName());
		super.visit(dependency, dependname);
	}

	
}
