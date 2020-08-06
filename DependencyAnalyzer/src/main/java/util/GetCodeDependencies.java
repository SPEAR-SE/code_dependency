package util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;

import configuration.SetEnv;
import its.datastructure.DirectDepend;
import its.datastructure.DirectIndirectDepend;
import its.datastructure.FileClassinfo;
import its.datastructure.JavaClass;
import its.datastructure.TestDependency;

public class GetCodeDependencies {
	

	public List<DirectDepend> get_direct_dependencies(String projectdirpath,String jardir,
			List<FileClassinfo> allfileinfo, Map<JavaClass, HashSet<JavaClass>> ci_includeextends,
			Map<JavaClass, HashSet<JavaClass>> pa){
		
		List<DirectDepend> alldirectdepend= new ArrayList<DirectDepend>();
		
		SetEnv envset = new SetEnv();
		envset.setTypesolver(projectdirpath, jardir, 2);
		TypeSolver ts = envset.getTypesolver();
		envset.setParserConfiguration(ts);	
		
		List<File> projectfiles = envset.getJavaFiles(projectdirpath);							
		
		for(File file : projectfiles) {
			GetCU getcu = new GetCU();
			CompilationUnit cu = getcu.getCu(file);
			DirectDepend directdependinfo = new DirectDepend(new File(""),new HashSet<File>(),
					new HashSet<File>(),new HashSet<File>(),new HashSet<File>(),new HashSet<File>(),
					new HashSet<File>(),new HashSet<File>(),new HashSet<File>(),
					new HashSet<File>(),new HashSet<JavaClass>(),
					new HashSet<File>(),new HashSet<File>(),new HashSet<File>(),new HashSet<File>(),new HashSet<File>(),
					new HashSet<File>(),new HashSet<File>(),true,new HashSet<File>());
			if(cu != null) {
				System.out.println("Getting codes DIRECT dependencies: " + file.getAbsolutePath());			
				GetDirectDependData getdirectdependdata = new GetDirectDependData();
				directdependinfo = getdirectdependdata.getDirectDepends(file, cu, allfileinfo, pa,projectfiles);//, ci								
				alldirectdepend.add(directdependinfo);
				System.out.println("success");
				//System.out.println("directdependinfo: " + directdependinfo.getfile() + " :: " + directdependinfo.getIsTestfile());
			}		
		}		
		
		return alldirectdepend;
	}
	
	
	
	public List<DirectIndirectDepend>  get_direct_indirect_dependencies(List<DirectDepend> alldirectdepend,List<FileClassinfo> allfileinfo) {
		
		List<DirectIndirectDepend> alldirectindirectdepend = new ArrayList<DirectIndirectDepend>();
		
		for(FileClassinfo fci: allfileinfo) {
			//find all direct and indirect dependencies for file(which fileclassinfo is fci)	
			File file = fci.getfilename();
			System.out.println("Getting codes INDIR_DIR dependencies: " + file.getAbsolutePath());
			
			
			DirectIndirectDepend directindirectdepend = new DirectIndirectDepend(new File(""), new HashSet<File>(),
		    		new HashSet<File>(), new HashSet<File>(),
		    		new HashSet<File>(), new HashSet<JavaClass>(),
		    		new HashSet<File>(), new HashSet<File>(),
		    		new HashSet<File>(), new HashSet<File>(),
		    		new HashSet<File>());			
			directindirectdepend.setfile(file);
			
			HashSet<File> DirectIndirectDepend_sourcecode_test_utility = new HashSet<File>();
			HashSet<File> DirectIndirectDependSourceCode = new HashSet<File>();
			HashSet<File> DirectIndirectDependUtility = new HashSet<File>();
			HashSet<File> DirectIndirectDependTest_Real = new HashSet<File>();	
			
			/*
			 * get direct and indirect depend test by directly extract from  test direct depend.
			 *  A direct depend B, B direct depend C, we'll get A directandindirect depend with [B,C]. kinda like a depend call graph.
			 *  three values need to be found: DirectIndirectDependSourceCode,DirectIndirectDependUtility,DirectIndirectDependTest_Real
			 */
			List<DirectDepend> cpdirectdepend = new ArrayList<DirectDepend>(alldirectdepend);
			for(DirectDepend filedirectdepend : alldirectdepend) {
				if(filedirectdepend.getfile()!=null) {
				 if(filedirectdepend.getfile().equals(file)) { 
					//find file's direct depend []
					HashSet<File> directdependsourcecode = filedirectdepend.getDirectDependSourceCodefile();					 
					HashSet<File> directdependtestandutility = filedirectdepend.getDirectDependTest_and_UtilityFile();				
					HashSet<File> all_file_directdepend = new HashSet<File>();
					all_file_directdepend.addAll(directdependsourcecode);
					all_file_directdepend.addAll(directdependtestandutility);	
					
					//first, add all direct depend in 
					//DirectIndirectDepend_sourcecode_test_utility.addAll(all_file_directdepend);
					
					//use stack to find all the dependencies for each directdepend.
					Stack<File> tmpsourcedepend = new Stack<File>();
					for(File f: all_file_directdepend) {
						tmpsourcedepend.push(f);
					}					
					while(!tmpsourcedepend.isEmpty()) {
						File pop = tmpsourcedepend.pop();
						DirectIndirectDepend_sourcecode_test_utility.add(pop);					
						for(DirectDepend dirdepend : cpdirectdepend) {								
							if(dirdepend.getfile()!= null) {
								if(dirdepend.getfile().equals(pop)) {
									if((dirdepend.getDirectDependSourceCodefile()!= null) && (dirdepend.getDirectDependSourceCodefile().size()>0)) {
										//add all direct dependency of pop to stack if it is not already added.
										for(File sourcefile: dirdepend.getDirectDependSourceCodefile()) {
											if(!DirectIndirectDepend_sourcecode_test_utility.contains(sourcefile)) {
												tmpsourcedepend.add(sourcefile);
											}
										}
									}
									break;
								}
							}
						}
					}//end of while. found all the direct and indirect dependency
					
					
				}
			}
		}
			
			for(File depends: DirectIndirectDepend_sourcecode_test_utility) {
				for(FileClassinfo fileinfo_depends: allfileinfo) {
					if(fileinfo_depends.getfilename().equals(depends)) {
						if(fileinfo_depends.getistest()) {
							DirectIndirectDependTest_Real.add(depends);
						}
						else if(fileinfo_depends.getfilename().getName().toLowerCase().contains("test")||
								fileinfo_depends.getfilename().toString().contains("test/")) {
							DirectIndirectDependUtility.add(depends);
						}
						else {
							DirectIndirectDependSourceCode.add(depends);
						}
						
						break;
					}
				}
			}	
			
			directindirectdepend.setDirectIndirectDependSourceCode(DirectIndirectDependSourceCode);
			directindirectdepend.setDirectIndirectDependTest_Real(DirectIndirectDependTest_Real);
			directindirectdepend.setDirectIndirectDependutility(DirectIndirectDependUtility);
			directindirectdepend.settestalldepend(DirectIndirectDepend_sourcecode_test_utility);
			alldirectindirectdepend.add(directindirectdepend);
						
		}//end for fileclassinfo
		return alldirectindirectdepend;		
	}
	
	
	
	
	
	
	
	
	//add  all parents (sourcecode, test, utility) that depend on file. 
	public List<DirectIndirectDepend> get_parent_direct_indirect_dependencies(List<DirectIndirectDepend> alldepends,List<FileClassinfo> allfileinfo){
		
		for(DirectIndirectDepend directindirectdependency: alldepends) {
			
			HashSet<File> parent_directIndirect_sourcecode =  new HashSet<File>();
			HashSet<File> parent_directIndirect_utility =  new HashSet<File>();
			HashSet<File> parent_directIndirect_realtest =  new HashSet<File>();
			HashSet<File> parent_directIndirect_all =  new HashSet<File>();
			
			File file = directindirectdependency.getFile();
			System.out.println("Getting Parent INDIR_DIR dependencies: " + file.getAbsolutePath());
			
			/*
			 * for debug
			 
			if(file.toString().contains("JunitSuiteTest.java")) {
				System.out.println("");
			}
			/**************/
			
			List<DirectIndirectDepend> dir_indir_depend = new ArrayList<DirectIndirectDepend>(alldepends);
			for(DirectIndirectDepend traveldependency: dir_indir_depend) {				
				HashSet<File> DirectIndirectDepend_sourcecode_test_utility = traveldependency.gettestalldepend();
				if(DirectIndirectDepend_sourcecode_test_utility.contains(file)) {
					parent_directIndirect_all.add(traveldependency.getFile());
				}
			}
			
			
			//seperate parent dependencies to: realtest, utility, sourcecode
			for(File parent_directIndirect : parent_directIndirect_all) {
				for(FileClassinfo fileinfo_depends: allfileinfo) {
					if(fileinfo_depends.getfilename().equals(parent_directIndirect)) {
						if(fileinfo_depends.getistest()) {
							parent_directIndirect_realtest.add(parent_directIndirect);
						}
						else if(fileinfo_depends.getfilename().getName().toLowerCase().contains("test")||
								fileinfo_depends.getfilename().toString().contains("test/")) {
							parent_directIndirect_utility.add(parent_directIndirect);
						}
						else {
							parent_directIndirect_sourcecode.add(parent_directIndirect);
						}				
						break;
					}
			    }
			}
			directindirectdependency.setParent_directIndirect_all(parent_directIndirect_all);
			directindirectdependency.setParent_directIndirect_sourcecode(parent_directIndirect_sourcecode);
			directindirectdependency.setParent_directIndirect_realtest(parent_directIndirect_realtest);
			directindirectdependency.setParent_directIndirect_utility(parent_directIndirect_utility);
			
			//System.out.println(file + "::PARENTindir:::"+ dependency.getParent_directIndirect_all());
			//System.out.println(file + "::indir:::"+ dependency.gettestalldepend());
		}
		return alldepends;
		
	}
}
	

