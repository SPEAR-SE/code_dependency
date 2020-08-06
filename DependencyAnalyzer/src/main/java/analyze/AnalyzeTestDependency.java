package analyze;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;

import configuration.Constant;
import configuration.SetEnv;
import its.datastructure.DirectDepend;
import its.datastructure.DirectIndirectDepend;
import its.datastructure.FileClassinfo;
import its.datastructure.JavaClass;
import its.datastructure.Systeminfo;
import its.datastructure.TestDependency;
import util.Output;
import util.getFileInfoInSystem;

public class AnalyzeTestDependency {
	public List<FileClassinfo> getAllfileinfo(String projectdirpath,String jardir){
		Map<JavaClass, HashSet<JavaClass>> ci = new HashMap<JavaClass, HashSet<JavaClass>>();		
		Map<JavaClass, HashSet<JavaClass>> pa = new HashMap<JavaClass, HashSet<JavaClass>>();
		List<FileClassinfo> allfileinfo = new ArrayList<FileClassinfo>();
		getFileInfoInSystem getclassgraph = new getFileInfoInSystem(ci,pa);				
		try {
			allfileinfo = getclassgraph.getFileInfoList(projectdirpath, jardir);
						
		} catch (Exception e1) {			
			System.out.println(e1);
		}
		return allfileinfo;
	}
	
	


	public List<TestDependency> getTestsInformation(String projectdirpath,String javadir,String jardir,
			List<FileClassinfo>allfileinfo,Systeminfo newsysteminfo){
		List<TestDependency> testsinfo = new ArrayList<TestDependency>();
		
		SetEnv envset = new SetEnv();	
		List<File> testfiles = envset.getTestFiles(javadir);	

		List<DirectDepend> alldirectdepend = newsysteminfo.getAlldirectdepend();
		List<DirectIndirectDepend> alldepends = newsysteminfo.getAlldepends();
		
		printCSVTitle();
		for(File testfile : testfiles) { 
			 
			TestDependency td = getTestDependency(testfile,projectdirpath,alldirectdepend,alldepends);
					//analyzesTest(testfile,projectdirpath,jardir,allfileinfo,ci_includeextends,pa);
			
			//if parse fail, found no tdd nor tdid.
			if(td.gettdd() == null ||td.gettdid()==null) {
				continue;
			}
			
			testsinfo.add(td);
						
			//caculator distance
			List<Integer> distance = new ArrayList<Integer>();
			if(td == null) {
				continue;
			}
			else if(td.getDirpackagedistance() != null) {
				Iterator<PackageDistance> tdi = td.getDirpackagedistance().iterator();
				while(tdi.hasNext()) {
					distance.add(tdi.next().getdistance());
				}		
			}
			Collections.sort(distance);
			int mindistance = 0;
			int maxdistance = 0;
			int sum = 0;
			for(int d:distance) {
				sum = sum + d;				
			}
			int averagedistance = 0;
			int middistance = 0;
			if(!distance.isEmpty()) {
				averagedistance = sum / distance.size() ;
				middistance = distance.get(distance.size()/2);
			    mindistance = distance.get(0);
				maxdistance = distance.get(distance.size() - 1);
			}
						
						
			boolean istestboolean = false ;			
			int lineofcode = 0;
			int istestNumber = 0;
			int beforeClassNumber = 0;
			int afterClassNumber = 0;
			int beforeNumber = 0;
			int afterNumber = 0;
			int istestNumber_extend = 0;
			int beforeClassNumber_extend = 0;
			int afterClassNumber_extend = 0;
			int beforeNumber_extend = 0;
			int afterNumber_extend = 0;
			for(FileClassinfo fci: allfileinfo) {
				if(fci.getfilename().equals(testfile)) {
					lineofcode = fci.getloc();
					if(fci.getistest()== true) {
						istestboolean=fci.getistest();	
						istestNumber = fci.getRealtestNumber();
						beforeClassNumber = fci.getbeforeClassNumber();
						beforeNumber = fci.getbeforeNumber();
						afterNumber = fci.getafterNumber();
						afterClassNumber = fci.getafterClassNumber();
						istestNumber_extend = fci.getRealtestNumber_extend();
						beforeClassNumber_extend = fci.getbeforeClassNumber_extend();
						beforeNumber_extend = fci.getbeforeNumber_extend();
						afterNumber_extend = fci.getafterNumber_extend();
						afterClassNumber_extend = fci.getafterClassNumber_extend();
					}			
					break;
				}
			}
			
			String getcalledjavaclass = "";
			if(td.gettdd()==null) {
				continue;
			}
				if(td.gettdd().getCalledJavaClass()!=null) {
					getcalledjavaclass = td.gettdd().getCalledJavaClass().toString();	
				}	
			
			
			
			//output a csv named "total test dependencies.csv"
			String subfilestring = testfile.getAbsolutePath().replaceAll(projectdirpath, "");
			String[]outputtounit = {testfile.getName(),String.valueOf(istestboolean),
					subfilestring,String.valueOf(lineofcode),
					String.valueOf(td.gettdd().getboolabstract()),
					String.valueOf(td.gettdd().getAllextend().size()),
					String.valueOf(istestNumber),					
					String.valueOf(beforeClassNumber),String.valueOf(beforeNumber),
					String.valueOf(afterClassNumber),String.valueOf(afterNumber),
					String.valueOf(istestNumber_extend),
					String.valueOf(beforeClassNumber_extend),String.valueOf(beforeNumber_extend),
					String.valueOf(afterClassNumber_extend),String.valueOf(afterNumber_extend),
					String.valueOf(td.gettdd().getmockfiles().size()),
					String.valueOf(td.gettdd().getDirectDependSourceCodefile().size()), 
					String.valueOf(td.gettdd().getsourceAbstract().size()),
					String.valueOf(td.gettdd().getsourceUtil().size()),
					String.valueOf(td.gettdd().getDirectDependTest_and_UtilityFile().size()),
					String.valueOf(td.gettdd().gettestAbstract().size()),
					String.valueOf(td.gettdd().gettestUtil().size()),
					String.valueOf(td.gettdd().getRealTestfile().size()),
					String.valueOf(td.gettdd().getistestAbstract().size()),
					String.valueOf(td.gettdd().getistestUtil().size()),
					
					String.valueOf(td.gettdid().gettestalldepend().size()),
					String.valueOf(td.gettdid().getDirectIndirectDependSourceCode().size()),
					String.valueOf(td.gettdid().getDirectIndirectDependTest().size()),
					String.valueOf(td.gettdid().getDirectIndirectDependTest_Real().size()),
					
					String.valueOf(td.gettdd().getdependParentsource().size()),
					String.valueOf(td.gettdd().getdependParenttest().size()),
					String.valueOf(td.gettdd().gettestparentabstractfile().size()),
					String.valueOf(td.gettdd().gettestparentutilfile().size()),
					String.valueOf(td.gettdd().getistestparentfile().size()),
					String.valueOf(td.gettdd().getistestparentabstractfile().size()),
					String.valueOf(td.gettdd().getistestparentutilfile().size()),
					
					String.valueOf(mindistance),String.valueOf(maxdistance),
					String.valueOf(averagedistance),String.valueOf(middistance),
					String.valueOf(distance),			
					td.gettdd().getDirectDependSourceCodefile().toString().replaceAll(projectdirpath, ""),
					td.gettdd().getDirectDependTest_and_UtilityFile().toString().replaceAll(projectdirpath, ""),
					getcalledjavaclass,
					td.gettdd().getAllextend().toString()
					};
			Output output = new Output();
			output.writerCSV(outputtounit, Constant.totaltestdependcy);
		
			}
		return testsinfo;
		}

/*	@Deprecated use getTestDependency
	public TestDependency analyzesTest(File testfile,String projectdirpath,String jardir,
			List<FileClassinfo> allfileinfo, Map<JavaClass, HashSet<JavaClass>> ci_includeextends,Map<JavaClass, HashSet<JavaClass>> pa) {
		TestDependency testdependency = new TestDependency(new File(""),null,null,null);
		testdependency.setTestfile(testfile);
		
		
		SetEnv envset = new SetEnv();
		envset.setTypesolver(projectdirpath, jardir, 3);
		TypeSolver ts = envset.getTypesolver();
		envset.setParserConfiguration(ts);	
		
		List<File> projectfiles = envset.getJavaFiles(projectdirpath);
		GetCU getcu = new GetCU();
		CompilationUnit cu = getcu.getCu(testfile);
		
		System.out.println("Analyzing test: " + testfile.getAbsolutePath());			
		if(cu != null) {

			GetDirectDependData gtd = new GetDirectDependData();
			DirectDepend tdd =gtd.getDirectDepends(testfile, cu, allfileinfo, pa,projectfiles);//, ci
			testdependency.settdd(tdd);			
			 			
			DirectIndirectDepend alldependencies = new DirectIndirectDepend();
			DirectIndirectDepend tdid = alldependencies.getTestAllDepends(cu,allfileinfo,testfile,ci_includeextends,projectfiles);
			testdependency.settdid(tdid);				   
		    
		    PackageDistance pd = new PackageDistance(null,null,0);
		    List<PackageDistance> packagedistances = pd.analyzePackageDistance(tdd.getTestsourcefile(),projectdirpath);
		    testdependency.setDirpackagedistance(packagedistances);		 
		    
		    return testdependency;		    
		}
		else {
			return testdependency;
		}
	}*/
	
	public TestDependency getTestDependency(File testfile,String projectdirpath,List<DirectDepend> alldirectdepend,List<DirectIndirectDepend> alldepends) {
		TestDependency testdependency = new TestDependency(new File(""),null,null,null);
		testdependency.setTestfile(testfile);
		for(DirectDepend dd: alldirectdepend) {
			if(dd.getfile().equals(testfile)) {
				testdependency.settdd(dd);
				PackageDistance pd = new PackageDistance(null,null,0);
			    List<PackageDistance> packagedistances = pd.analyzePackageDistance(dd.getDirectDependSourceCodefile(),projectdirpath);
			    testdependency.setDirpackagedistance(packagedistances);
			    break;
			}			
		}
		for(DirectIndirectDepend did: alldepends) {
			if(did.getFile().equals(testfile)) {
				testdependency.settdid(did);
				break;
			}			
		}
		    
	    return testdependency;		    
	}
	

public void printCSVTitle() {
	Output output = new Output();
	//String[] csvtitle = {"Test_File", "Depend_on_File","Depend_ClassinFile"};
	//output.writerCSV(csvtitle,Constant.directdependdetail);
	//output.writerCSV(csvtitle, Constant.classfiledpend);
	String[]unittest = {"TestFileName","RealTest","FullPath","LinesOfCode",
			"IsAbstract","InheritDepths",
			"No.@test",
			"No.@beforeClass","No.@before",
			"No.@afterClass","No.@after",
			"extendNo.@test",
			"extendNo.@beforeClass","extendNo.@before",
			"extendNo.@afterClass","extendNo.@after",
			"MockFiles",
			"Sourcecode_DirectDepend","DirDependSource&&Abstract","DirDependSource&&Util",			
			"Test_DirectDepend","Test(Abstract)_DirDepend","Test(Util)_DirDepend",
			"RealTest_DirectDependTest","RealTest&&Abstract","RealTest&&Util",
			"TotalDirectIndirecDepend","SourceCode_TotalDepend","Test_TotalDepend","RealTest_TotalDepend",
			"Parent_SourceCode",
			"Parent_Test","Parent_Test&&Abstract","Parent_Test&&Util",
			"Parent_RealTest",
			"Parent_RealTest&&Abstract","Parent_RealTest&&Util",
			"MiniPackageDistance","MaxPackageDistance","AveragePackageDistance","MiddlePackageDistance",
			"PackageDistance",  
			"DirectDependsSourceCodes","DirectDependsTestFiles","DependClasses","AllExtendClasses"};
	output.writerCSV(unittest, Constant.totaltestdependcy);
	
}	






}
