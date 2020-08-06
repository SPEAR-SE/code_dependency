package analyze;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.javaparser.ast.CompilationUnit;

import configuration.Constant;
import configuration.SetEnv;
import its.datastructure.DirectDepend;
import its.datastructure.DirectIndirectDepend;
import its.datastructure.FileClassinfo;
import its.datastructure.TestDependency;
import test.selection.TestSelect;
import util.CountLine;
import util.GetCU;
import util.Output;
 

public class AnalyzeSourceCode_DirectInDirectTest {
	private HashMap<File,HashSet<File>> sourceDirectCallbyTest = new HashMap<File,HashSet<File>>();
	private HashMap<File,HashSet<File>> source_InDirect_Direct_CallbyTest = new HashMap<File,HashSet<File>>();
	public HashMap<File,HashSet<File>> getSourceDirectCallbyTest() {
		return sourceDirectCallbyTest;
	}

	public void setSourceDirectCallbyTest(HashMap<File,HashSet<File>> sourceDirectCallbyTest) {
		this.sourceDirectCallbyTest = sourceDirectCallbyTest;
	}
	public HashMap<File,HashSet<File>> getSource_InDirect_Direct_CallbyTest() {
		return source_InDirect_Direct_CallbyTest;
	}

	public void setSource_InDirect_Direct_CallbyTest(HashMap<File,HashSet<File>> source_InDirect_Direct_CallbyTest) {
		this.source_InDirect_Direct_CallbyTest = source_InDirect_Direct_CallbyTest;
	}

	
	
	
	public AnalyzeSourceCode_DirectInDirectTest getSourceCodeCallbyTest(List<TestDependency> testdependency) {
		AnalyzeSourceCode_DirectInDirectTest asid = new AnalyzeSourceCode_DirectInDirectTest();
		HashMap<File,HashSet<File>> sourceDirectCallbyTest = new HashMap<File,HashSet<File>>();
		HashMap<File,HashSet<File>> source_InDirect_Direct_CallbyTest = new HashMap<File,HashSet<File>>();
	    if(testdependency.size()>0) {
	    	for(TestDependency testdepend: testdependency) {
				if(testdepend.gettdd() == null) {
					continue;
				}
				DirectDepend tdd = testdepend.gettdd();
				HashSet<File> sourcefiles = tdd.getDirectDependSourceCodefile();
				File testfile = tdd.getfile();				
				for(File sourcef: sourcefiles) {			
					Set<File> sourcecallbytestKey = sourceDirectCallbyTest.keySet();
					if(sourcecallbytestKey.contains(sourcef)) {
						sourceDirectCallbyTest.get(sourcef).add(testfile);
					}
					else {
						HashSet<File> tmptest = new HashSet<File>();
						tmptest.add(testfile);
						sourceDirectCallbyTest.put(sourcef, tmptest);
					}			
				}
				
				//from indirect_direct depend info, get source code that call by test.
				if(testdepend.gettdid() == null) {
					continue;
				}
				DirectIndirectDepend tdid = testdepend.gettdid();
				HashSet<File> sourcefilescallbyindirect = tdid.getDirectIndirectDependSourceCode();
				File indirtestfile = tdid.getFile();
				for(File sf: sourcefilescallbyindirect) {
					Set<File>sourcecallbyindirectkey = source_InDirect_Direct_CallbyTest.keySet();
					if(sourcecallbyindirectkey.contains(sf)) {
						source_InDirect_Direct_CallbyTest.get(sf).add(indirtestfile);
					}
					else {
						HashSet<File> tmptest = new HashSet<File>();
						tmptest.add(indirtestfile);
						source_InDirect_Direct_CallbyTest.put(sf, tmptest);
					}
				}
				
				
			}
	    }	
	    asid.setSourceDirectCallbyTest(sourceDirectCallbyTest);
	    asid.setSource_InDirect_Direct_CallbyTest(source_InDirect_Direct_CallbyTest);
		//return sourceDirectCallbyTest;
		return asid;
		
	}
	
	
	public void analyzeSourceCodeCallbyTest(List<TestDependency> testdependency,String javadir,List<FileClassinfo>allfileinfo) {
		if(testdependency.size()>0) {
			AnalyzeSourceCode_DirectInDirectTest asid = getSourceCodeCallbyTest(testdependency);
			HashMap<File,HashSet<File>>sourceDirectcallbytest = asid.getSourceDirectCallbyTest();
			HashMap<File,HashSet<File>>sourceDirectIndirectcallbytest = asid.getSource_InDirect_Direct_CallbyTest();
			
			SetEnv setenv = new SetEnv();
			List<File> sourcefiles = setenv.getJavaFiles(javadir);			
			List<File> alltestfiles = setenv.getTestFiles(javadir);
			TestSelect ts = new TestSelect();
			HashSet<File> realtest = ts.findallrealtest(allfileinfo);			
			sourcefiles.removeAll(realtest); //get list of sourcefiles
			alltestfiles.removeAll(realtest); //get list of utility
			
			
			
			String[] title = {"filename","path","LOC","Abstract",
					"Utility",//(in test dir or name contains test but not realtest)
					"No. of unittest","No. of nonunittest",
					"NO.of_indirect_direct_test",
					"unittest","NonunitTest"};
			Output outputtitle = new Output();
			outputtitle.writerCSV(title, Constant.source_test_relation);
			
			//get information for each source files(source code file + utility file)
			for(File sourcef: sourcefiles) {
				System.out.println("analyzing sourcefile depends test: " + sourcef);
				boolean isutility = false;
				if(alltestfiles.contains(sourcef)) {
					isutility = true;
				}
				boolean isabstract = false;
				for(FileClassinfo fci: allfileinfo) {
					if(fci.getfilename().equals(sourcef)) {
						if(fci.getisabstract()) {
							isabstract = true;
						}
						break;
					}
				}
				
				HashSet<File>  nonunit = new HashSet<File> ();
				HashSet<File>  unit = new HashSet<File> ();					
								
				if(sourceDirectcallbytest.size() > 0) {
					Set<File>sourcecallbytestKey = sourceDirectcallbytest.keySet();						
					if(sourcecallbytestKey.contains(sourcef)) {
						HashSet<File> directtestsf = new HashSet<File>();
						directtestsf= sourceDirectcallbytest.get(sourcef);
						if(directtestsf.size() > 0) {
							for(File test: directtestsf) {								 
								for(TestDependency testdepend: testdependency) {
									if(testdepend.getTestfile().equals(test)) {
										if(testdepend.gettdd().getIsunit() == true) {
											unit.add(test);
										}
										else {
											nonunit.add(test);
										}
										break;
									}									
								}
							}						
						}
					}
				}
				
				HashSet<File> direct_indirect_test_of_sourcefile = new HashSet<File>();
				if(sourceDirectIndirectcallbytest.size() > 0) {
					Set<File> key = sourceDirectIndirectcallbytest.keySet();
					if(key.contains(sourcef)) {
						direct_indirect_test_of_sourcefile = sourceDirectIndirectcallbytest.get(sourcef);					 
					}				
				}
				GetCU getcu = new GetCU();
				CompilationUnit cu = getcu.getCu(sourcef);
				CountLine cloc = new CountLine();
				int locnum = cloc.countLineinCU(cu);
				
				Output output = new Output();					
				String[] sourceandtest = {sourcef.getName(), 
						sourcef.getAbsolutePath().replaceAll(javadir, ""),
						String.valueOf(locnum),
						String.valueOf(isabstract),
						String.valueOf(isutility),
						String.valueOf(unit.size()), String.valueOf(nonunit.size()),
						String.valueOf(direct_indirect_test_of_sourcefile.size()),
						unit.toString(),nonunit.toString()};
				output.writerCSV(sourceandtest, Constant.source_test_relation);
			 
			  }									
		}		
				
				
}
				
	
}
