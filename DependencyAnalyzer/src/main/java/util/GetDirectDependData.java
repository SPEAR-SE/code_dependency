package util;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.github.javaparser.ast.CompilationUnit;

import its.datastructure.DirectDepend;
import its.datastructure.FileClassinfo;
import its.datastructure.JavaClass;

public class GetDirectDependData {
	/**
	 * return all the files that the testfile depends on.
	 * @param cu
	 * @param allfileinfo
	 * @param file
	 * @param ci
	 * @return HashSet<File>
	 */
	public DirectDepend getDirectDepends(File file,CompilationUnit cu,List<FileClassinfo> allfileinfo,
			Map<JavaClass, HashSet<JavaClass>> pa,List<File> projectfiles) {	//Map<JavaClass, HashSet<JavaClass>> ci,
		
		DirectDepend directdepend = new DirectDepend(new File(""),new HashSet<File>(),
				new HashSet<File>(),new HashSet<File>(),new HashSet<File>(),new HashSet<File>(),
				new HashSet<File>(),new HashSet<File>(),new HashSet<File>(),
				new HashSet<File>(),new HashSet<JavaClass>(),
				new HashSet<File>(),new HashSet<File>(),new HashSet<File>(),new HashSet<File>(),new HashSet<File>(),
				new HashSet<File>(),new HashSet<File>(),true,new HashSet<File>());
		//GetRootClass getrootclass = new GetRootClass();
		//JavaClass rootc = getrootclass.getRootJavaClass(cu);
		
		directdepend.setfile(file);
		
		HashSet<File>testsourcefile = new HashSet<File>();
		HashSet<File>sourceabstract = new HashSet<File>();
		HashSet<File>sourceutil = new HashSet<File>();
		
		HashSet<File>dependtest = new HashSet<File>();
		HashSet<File>realtestfiledirectdepend = new HashSet<File>();
		HashSet<File>testutil = new HashSet<File>();
		HashSet<File>testabstract = new HashSet<File>();
		HashSet<File>realtestutil = new HashSet<File>();
		HashSet<File>realtestabstract = new HashSet<File>();
		
		HashSet<File>dependparenttest = new HashSet<File>();
		HashSet<File>testparentabstractfile = new HashSet<File>();
		HashSet<File>testparentutilfile = new HashSet<File>();
		
		HashSet<File>istestparentfile = new HashSet<File>();
		HashSet<File>istestparentabstractfile = new HashSet<File>();
		HashSet<File>istestparentutilfile = new HashSet<File>();
		
		HashSet<File>dependparentsource = new HashSet<File>();
		HashSet<File> mockfiles = new HashSet<File>();
		
		Boolean trueUnitTest = false;
		Boolean istest = false;
		//int istestNumber = 0;
		//find classes that test in this test file.
		HashSet<JavaClass>calledjavaclass = new HashSet<JavaClass>();
		HashSet<JavaClass>parentsjavaclass = new HashSet<JavaClass>();
		HashSet<JavaClass> mockjavaclass = new HashSet<JavaClass>();
		HashSet<JavaClass> allextendclass = new HashSet<JavaClass>();
		//get all the called classes in the file
		/*////////////////////////////////
		if(rootc !=null) {
			calledjavaclass = ci.get(rootc);
			testdirectdepend.setCalledJavaClass(calledjavaclass);			
		}			 
		////////////*/ 
		
		//find the dependency of testfile
		//find the class declared in the test file.	
		HashSet<JavaClass>declaredjavaclass = new HashSet<JavaClass>();
		HashSet<String> dependname = new HashSet<String>();
		 String possiblefilename = "";
		//if this is a test, add the related source code/utility to testsourcefile files in case it is not imported.
		if(file.getName().contains("Test")) {
			possiblefilename = file.getName().replace("Test", "");				
			for(File pfile: projectfiles) {				
				if(pfile.getName().equals(possiblefilename)) {
					testsourcefile.add(pfile);				 
					if(pfile.getName().toLowerCase().contains("abstract")) {
						sourceabstract.add(pfile);
					}
					if(pfile.getName().toLowerCase().contains("util")) {
						sourceutil.add(pfile);
					}
					break;
				}
			}
			
		}
	
		boolean isabstract = false;
		for(FileClassinfo fci: allfileinfo) {
			if(fci.getfilename().equals(file)) {
				declaredjavaclass = fci.getClassDeclaredInFile();	
				mockjavaclass = fci.getmockclass();
				dependname = fci.getDependenciesName();
				allextendclass = fci.getAllDirectandIndirectExtendsclasses();
				directdepend.setAllextend(allextendclass);
				isabstract = fci.getisabstract();
				directdepend.setboolabstract(isabstract);
				istest = fci.getistest();		
				//newcode()
				calledjavaclass = fci.getcalledjavaclass();
				directdepend.setCalledJavaClass(calledjavaclass);	
				break;
			}
		}
			
			//get all the parents of the file
			for(JavaClass dj: declaredjavaclass) {	
				if(pa.get(dj)!=null) {
					parentsjavaclass.addAll(pa.get(dj));
				}					
			}
		
		//System.out.println(file + " calledjavaclass:" + calledjavaclass);
		//System.out.println("dependname" + dependname);
		if(calledjavaclass !=null) {
			HashSet<JavaClass> newcalledjavaclass = new HashSet<JavaClass>();
			newcalledjavaclass.addAll(calledjavaclass);
			//remove classes that declared in the same file.
			newcalledjavaclass.removeAll(declaredjavaclass);
			for(JavaClass njc: newcalledjavaclass) {
				String filename = "";
				boolean found = false;
				for(FileClassinfo fc: allfileinfo) {
			        /*
			         * dependname contains all the possible depend java files. 
			         * it can help us avoid finding dependency because of some common java class such as Arrays/HashMap...
			         */
					
					if(fc.getClassDeclaredInFile().contains(njc)
							&&dependname.contains(fc.getfilename().getName().substring(0, fc.getfilename().getName().lastIndexOf(".")))) {
						 
						filename = fc.getfilename().getAbsolutePath();
						if(fc.getfilename().equals(file)) {
							found = true;
							continue;
						}
						else {
												
							if(fc.getfilename().getName().toLowerCase().contains("test") ||
									fc.getfilename().toString().contains("test/")) {							
								if(fc.getistest() == true) {
									realtestfiledirectdepend.add(fc.getfilename());
									if(fc.getisabstract()==true) {
										realtestabstract.add(fc.getfilename());
									}
									if(fc.getisutil() == true) {
										realtestutil.add(fc.getfilename());
									}
								}
								
								dependtest.add(fc.getfilename());
								if(fc.getisabstract()==true) {
									testabstract.add(fc.getfilename());
								}
								if(fc.getisutil() == true) {
									testutil.add(fc.getfilename());
								}
							}
							else {
								testsourcefile.add(fc.getfilename());
								 
								if(fc.getisabstract()==true) {
									sourceabstract.add(fc.getfilename());
								}
								if(fc.getisutil() == true) {
									sourceutil.add(fc.getfilename());
								}
							}				
						}
						found =true;
						break;
					}			
				}
				 
			/*	Output output = new Output();
				String[]filedepend = {testfile.getAbsolutePath(),filename,njc.toString()};					
				output.writerCSV(filedepend,Constant.directdependdetail);*/
			}
			
			
			directdepend.setDirectDependTest_and_UtilityFile(dependtest);
			directdepend.setDirectDependSourceCodefile(testsourcefile);
			
			//System.out.println("dependtest:"+ dependtest);
			//System.out.println("dependsourcefile" + testsourcefile);
			
			directdepend.setRealTestfile(realtestfiledirectdepend);
			directdepend.setistestUtil(realtestutil);
			directdepend.settestUtil(testutil);
			directdepend.setsourceUtil(sourceutil);
			directdepend.setistestAbstract(realtestabstract);	
			directdepend.settestAbstract(testabstract);	
			directdepend.setsourceAbstract(sourceabstract);	
			
			
		    if(istest == true) {
		    	if(testsourcefile.size() == 1) {
		    		trueUnitTest = true;
		    	}
		    }
		    directdepend.setIsunit(trueUnitTest);
		}
		
		if(parentsjavaclass !=null) {
			HashSet<JavaClass> newparentsjavaclass = new HashSet<JavaClass>();
			newparentsjavaclass.addAll(parentsjavaclass);		
			newparentsjavaclass.removeAll(declaredjavaclass);
			for(JavaClass njc: newparentsjavaclass) {
				//String filename = "";
				for(FileClassinfo fc: allfileinfo) {				
					if(fc.getClassDeclaredInFile().contains(njc)) {
						//filename = fc.getfilename().getAbsolutePath();
						if(fc.getfilename().equals(file)) {
							continue;
						}
						else {
							
							if(fc.getfilename().getName().contains("test") ||
									fc.getfilename().toString().contains("test/")) {							
								if(fc.getistest() == true) {
									istestparentfile.add(fc.getfilename());
									if(fc.getisabstract()==true) {
										istestparentabstractfile.add(fc.getfilename());
									}
									if(fc.getisutil() == true) {
										istestparentutilfile.add(fc.getfilename());
									}
								}
								dependparenttest.add(fc.getfilename());
								if(fc.getisabstract()==true) {
									testparentabstractfile.add(fc.getfilename());
								}
								if(fc.getisutil() == true) {
									testparentutilfile.add(fc.getfilename());
								}
							}
							else {
								dependparentsource.add(fc.getfilename());
							}				
						}				
						break;
					}			
				}
			/*	Output output = new Output();
				String[]filedepend = {testfile.getAbsolutePath(),filename,njc.toString()};					
				output.writerCSV(filedepend,Constant.parentdependdetail);*/
			}
			directdepend.setdependParenttest(dependparenttest);
			directdepend.setdependParentsource(dependparentsource);
			 
			directdepend.setistestparentfile(istestparentfile);
			 									
		}
		
		// to check which file is mocked
		if(mockjavaclass !=null) {
			HashSet<JavaClass> newmock= new HashSet<JavaClass>();
			newmock.addAll(mockjavaclass);		
			
			for(JavaClass njc: newmock) {
				//String filename = "";
				for(FileClassinfo fc: allfileinfo) {				
					if(fc.getClassDeclaredInFile().contains(njc)) {
						//filename = fc.getfilename().getAbsolutePath();
						if(fc.getfilename().equals(file)) {
							continue;
						}
						else {							
							mockfiles.add(fc.getfilename());
							break;	   
						     										
					    }			
				     }				
			    }			
		   }
		}
		directdepend.setmockfiles(mockfiles);
		return directdepend;
	}
	
	

}
