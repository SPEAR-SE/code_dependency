package testsmell;

import java.io.File;
 
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
 

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

import util.JavaClass;
import testsmell.helper.Collector;
import testsmell.helper.FileMapping;
import util.Constant;
import util.GetCU;
import util.IsAbstract;
import util.JunitCount;
import util.Output;
 

//Test Smell 1: Duplicate test runs caused by inherit non-abstract tests which contains test method(i.e. @Test)
public class TS_DupTestRun {

	private FileMapping filemapping = new FileMapping();

	public void getDuplicateTestRun(List<File> testfiles) {
		//HashMap<JavaClass, File> file_javaclass_mapping = new HashMap<JavaClass, File>();
		//file_javaclass_mapping = filemapping.getJavaClassFileMapping(testfiles);
		
		IsAbstract isabstract = new IsAbstract();
		JunitCount junitcount = new JunitCount();
		
		HashMap<File, HashSet<File>> childrenmapping = filemapping.getDirectExtension(testfiles);
		HashMap<File, HashSet<JavaClass>> extendgrap = filemapping.getExtendGraph(testfiles);
		
		for(File testfile: testfiles) {
			//System.out.println("Parsing: " + testfile);
			if(childrenmapping.containsKey(testfile) == false) {
				continue;
			}
			else {
				GetCU getcu = new GetCU();
				CompilationUnit fcu = getcu.getCu(testfile);
				if(isabstract.isAbstract(fcu)== false && junitcount.countistestNumber(fcu) > 0) {
					HashSet<File> children = childrenmapping.get(testfile);
					
					Collector c = new Collector();
//					List<MethodDeclaration> filehelpermethod = c.collectFileHelperMethod(testfile,extendgrap,file_javaclass_mapping);
					for(File child: children) {			
						List<MethodDeclaration> overridemethod = c.getOverrideMethod(child);
//						if(overrideHelperMethod(child,filehelpermethod,overridemethod ) == false) {
//							System.out.println("TESTSMELL1: "+ testfile + " AND " + child);
//						}
						if(!fixtureUpdated(child, testfile, overridemethod)) {
							System.out.println("TESTSMELL1: "+ testfile + " AND " + child);
							Output output = new Output();
							String constantpath  = "/Users/zipeng/Projects/10project/";
							String[] duptestrun = {"Smell1","DuplicateTestRun",testfile.toString().replace(constantpath, ""),child.toString().replace(constantpath, "")};
							output.writerCSV(duptestrun, Constant.testsmell);	
							
						}
					}
				}
			}
		}
	}
	
	/*
	 * to verify whether the test file override any of its parent's helperMethod.
	 */
	public boolean overrideHelperMethod(File testfile, List<MethodDeclaration> FileHelperMethod,List<MethodDeclaration> overrideMethod) {
		boolean override = false;
		
		for(int i = 0; i < overrideMethod.size(); i++) {
			MethodDeclaration md = overrideMethod.get(i);
			for(MethodDeclaration helper: FileHelperMethod) {
				if(helper.getNameAsString().equals(md.getNameAsString())) {
					override = true;
					break;
				}
			}
			 
		}		
		return override;
	}
		
	
	/*
	 * to verify whether the child added any new test fixture.
	 * testfixture[0] is @BeforeClass
	 * testfixture[1] is @Before || @BeforeEach
	 */
	public boolean fixtureUpdated(File child, File parent,List<MethodDeclaration> overrideMethods) {
		boolean updated = false;
		JunitCount junitcount = new JunitCount();
		Collector c = new Collector();
	
		//check whether any @BeforeClass is added or override
		List<MethodDeclaration> testfile_beforeClassmethod = c.getTestFixture(child)[0];
		List<MethodDeclaration> superclasss_beforClassemethod = c.getTestFixture(parent)[0];	
		boolean updateBeforeClass = compareListMethodFixture(testfile_beforeClassmethod,superclasss_beforClassemethod,overrideMethods);
		
		//check whether any @Before is added or override
		List<MethodDeclaration> testfile_beforemethod = c.getTestFixture(child)[1];
		List<MethodDeclaration> superclasss_beforemethod = c.getTestFixture(parent)[1];		
		boolean updateBefore = compareListMethodFixture(testfile_beforemethod,superclasss_beforemethod,overrideMethods);

		updated = updateBeforeClass || updateBefore;
		return updated;	
	}
	
	
	public boolean overrideMethodFixture(MethodDeclaration test,List<MethodDeclaration> overrideMethods ) {
		boolean override_parent = false;
		if(overrideMethods.contains(test)) {
			override_parent = true;
		}
		return override_parent;
	}
	
	
	public boolean compareListMethodFixture(List<MethodDeclaration> testmethods, List<MethodDeclaration> supermethods,List<MethodDeclaration> overridemethods) {
		boolean result = false;
		if(testmethods.size() == 0 && supermethods.size() == 0) {
			result = false;
		}else if(testmethods.size() > supermethods.size()) { // child add new test fixture
			result = true;
		}
		else { // check whether child override parent's test fixture
			for(MethodDeclaration testfixture: testmethods) {
				if(overrideMethodFixture(testfixture,overridemethods)) {
					result = true;
					break;
				}
			}
		}
		return result;		
	}
					

		
	}
	
	
 
