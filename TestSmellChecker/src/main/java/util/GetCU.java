package util;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.resolution.UnsolvedSymbolException;
 

public class GetCU {
	public  CompilationUnit getCu(File parsefile) {
		try {				
			CompilationUnit cu = StaticJavaParser.parse(parsefile);;
			return cu;
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			Logger logger = Logger.getLogger("Null Pointer");
			logger.log(Level.SEVERE, "Null pointer exception happen, maybe there's no test file exists...", true);
			System.out.println("Null pointer exception, maybe there's no test file exists");
			Output output = new Output();
			output.writetotxt(e.getMessage(), Constant.exception);
			return null;
		} catch (UnsolvedSymbolException e) {
			Logger logger = Logger.getLogger("UnsolveSymbolException");
			logger.log(Level.SEVERE, e.toString(), true);
			System.out.println("Unsolve Symbol Exception");	
			Output output = new Output();
			output.writetotxt(e.getMessage(), Constant.exception);
			return null;			
		} catch (RuntimeException e) {
			Logger logger = Logger.getLogger("Runtime Exception");
			logger.log(Level.SEVERE, e.toString(), true);
			System.out.println(e.toString());	
			Output output = new Output();
			output.writetotxt(e.getMessage(), Constant.exception);
			return null;
	    } catch (StackOverflowError e) {
	    	Logger logger = Logger.getLogger("Runtime Exception");
			logger.log(Level.SEVERE, e.toString(), true);
			System.out.println(e.toString());	
			Output output = new Output();
			output.writetotxt(e.getMessage(), Constant.exception);
			return null;
	    } catch (AssertionError e) {
			Logger logger = Logger.getLogger("Exception");
			logger.log(Level.WARNING, "Parse Fail: "+ parsefile.toString());
			Output output = new Output();
			output.writetotxt(e.getMessage(), Constant.exception);
			return null;
	    } catch (Exception e) {
			Logger logger = Logger.getLogger("Exception");
			logger.log(Level.WARNING, "Parse Fail: "+ parsefile.toString());
			Output output = new Output();
			output.writetotxt(e.getMessage(), Constant.exception);
			return null;
		}
	}

}
