package util;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.utils.Log;
import com.github.javaparser.utils.ProjectRoot;
import com.github.javaparser.utils.SourceRoot;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.MemoryTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

public  class SetParseEnv {
	private TypeSolver typesolver = new CombinedTypeSolver();
	/*
	 * find all jar for jar type solver
	 */
	public  List<Path> getJar(String jardir) {
		List<Path> jarpath = new ArrayList<Path>();
		File parsefile = new File(jardir);
		if(parsefile.isDirectory()) {
			File[] files = parsefile.listFiles();
			if(files.length > 0) {
				for(File file : files) {
					/*if(file.toString().contains("/src/")) {
						continue;
					}*/
					jarpath.addAll(getJar(file.toString()));
				}
			}				
		}
		else {
			if(jardir.endsWith(".jar")) {
				Path jarpa = parsefile.toPath();
				jarpath.add(jarpa);
			}
		}
		return jarpath;
	}

	/*
	 * set the parse configuration(if need to resolve type)
	 */
	public TypeSolver setTypesolver(String projectdir, String jardir, int para)  {				
		File parserdir = new File(projectdir);	
		List<Path> jarpath = getJar(jardir.toString());
		CombinedTypeSolver combinedSolver = new CombinedTypeSolver();
		JavaParserTypeSolver javaParserTypeSolver ;		
		if(parserdir.toString().endsWith("java")) {
			 javaParserTypeSolver = new JavaParserTypeSolver(parserdir.getAbsolutePath().substring(0, parserdir.getAbsolutePath().lastIndexOf("/")));						
		}
		else {
			javaParserTypeSolver = new JavaParserTypeSolver(parserdir);	
		}
		TypeSolver reflectionTypeSolver = new ReflectionTypeSolver();
		MemoryTypeSolver memoryTypeSolver = new MemoryTypeSolver();
		switch(para) {
		case 0:{
			break;
		}
		case 1:{
			combinedSolver.add(javaParserTypeSolver);
			break;
		}
		case 2:{
			combinedSolver.add(reflectionTypeSolver); 
			combinedSolver.add(javaParserTypeSolver);	
			break;
		}
		case 3:{
			combinedSolver.add(reflectionTypeSolver); 
			combinedSolver.add(javaParserTypeSolver);
			if(jarpath.size() > 0) {
				for (Path jp : jarpath) {
					TypeSolver jarTypeSolver;
					try {
						jarTypeSolver = new JarTypeSolver(jp);
						combinedSolver.add(jarTypeSolver);					
					} catch (IOException e) {							
						Log.error( "no availabe jar in the directory when setting jarTypeSolver");
					}					
				}		
            }
			break;
		}
		case 4:{
			combinedSolver.add(javaParserTypeSolver);
	    	combinedSolver.add(reflectionTypeSolver);			    					 					
			if(jarpath.size() > 0) {
				for (Path jp : jarpath) {
					TypeSolver jarTypeSolver;
					try {
						jarTypeSolver = new JarTypeSolver(jp);
						combinedSolver.add(jarTypeSolver);
					} catch (IOException e) {							
						Log.error( "no availabe jar in the directory when setting jarTypeSolver");
					}					
				}				
            }
			combinedSolver.add(memoryTypeSolver);
			break;
	      }    		  									
		}
		
		this.typesolver = combinedSolver;
		return combinedSolver;		    	
    }
	
	/*
	 * get typesolver
	 */
	public  TypeSolver getTypesolver()  {	
		return this.typesolver;
	}
	/*
	 * get parser configuration
	 */
	public  ParserConfiguration setParserConfiguration(TypeSolver typesolver) {		
		JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typesolver);
		ParserConfiguration parserConfiguration = new ParserConfiguration().setSymbolResolver(symbolSolver);
		return parserConfiguration;
	}
	
	/*
	 * set parser configuration
	 */
	public void setStaticParserConfiguration( TypeSolver typesolver) {		
		ParserConfiguration parserConfiguration = setParserConfiguration(typesolver);
        StaticJavaParser.setConfiguration(parserConfiguration);	 
		//JavaParser.setStaticConfiguration(parserConfiguration);
	}
	
	public JavaParser createNewJavaParser( TypeSolver typesolver) {		
		ParserConfiguration parserConfiguration = setParserConfiguration(typesolver);
		JavaParser javaparser = new JavaParser(parserConfiguration);
		return javaparser;
	}
	
	/*
	 * get project root.
	 */
	public  ProjectRoot getProjectRoot(String dirpath) {
		File files = new File(dirpath);
		ProjectRoot projectRoot= new ProjectRoot(files.toPath());
		return projectRoot;
	}
	
	/*
	 *  get source Root
	 */
	public  List<SourceRoot> parseProject(String dirpath) {	
		List<SourceRoot> sourceRoots = getProjectRoot(dirpath).getSourceRoots();		
		return sourceRoots;
	}
	
	

}
