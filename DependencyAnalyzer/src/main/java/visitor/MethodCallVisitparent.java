package visitor;

import java.util.HashSet;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;

import configuration.Constant;
import its.datastructure.Method;
import util.Output;


public class MethodCallVisitparent extends VoidVisitorAdapter<HashSet<Method>> {
	public void visit(MethodCallExpr mc, HashSet<Method> collector){ 		
		String methodname = mc.getNameAsString();		
		String packagename = "";
		String classname = "";
		String signature = "";
		//String returntype = "";
		
		try {
			
			ResolvedMethodDeclaration resolvedmethod = mc.resolve();
			packagename = resolvedmethod.getPackageName();			
			classname = resolvedmethod.getClassName();
			signature = resolvedmethod.getSignature();
			//returntype = resolvedmethod.getReturnType().describe();
			
		}catch (UnsolvedSymbolException e) {				
		    Output output = new Output();			
			output.writetotxt(e.toString(),Constant.exception);
			
			
		} catch(RuntimeException runex) {
			Output output = new Output();						
			output.writetotxt(runex.toString(),Constant.exception);
											
		} catch (OutOfMemoryError e) {
			Output output = new Output();		
			output.writetotxt(e.toString(),Constant.exception);			
			JavaParserFacade.clearInstances();
		
		} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
				
		Method callmethod = new Method(packagename,classname,methodname,signature);		
		collector.add(callmethod);

		super.visit(mc, collector);
	}

}

		
