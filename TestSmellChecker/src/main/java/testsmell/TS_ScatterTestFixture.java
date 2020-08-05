package testsmell;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import util.JavaClass;
import testsmell.helper.Collector;
import testsmell.helper.FileMapping;
import util.Constant;
import util.GetCU;
import util.JunitCount;
import util.Output;
import visitor.MethodVisitor;

public class TS_ScatterTestFixture {
	
	private FileMapping filemapping = new FileMapping();
	//private MethodVisitor methodvisitor = new MethodVisitor();
	private Collector collector = new Collector();
	
	public boolean compareMethodFixture(MethodDeclaration test, MethodDeclaration superclass,CompilationUnit superclasscu) {
		boolean result = false;
		String methodname = superclass.getNameAsString();
		String superclass_classname = "";
		try {
			superclass_classname = superclass.resolve().getClassName();
		}catch(Exception e){				
			superclass_classname = superclasscu.findFirst(ClassOrInterfaceDeclaration.class).get().getName().asString();					
		}
		
		/*
		 * look up this in file:  test invoke subclass by super.method() or superclassname.method();
		 */
		String superkeyword = "super."+methodname;
		String classkeyword = superclass_classname+"."+methodname;		
		if(test.toString().contains(superkeyword)||superclass.toString().contains(classkeyword) ) {
			result = true;
		} 		
		return result;	
	}
	
	public boolean compareListMethodFixture(List<MethodDeclaration> test, List<MethodDeclaration> superclass,CompilationUnit superclasscu) {
		boolean result = false;
		if(test.size() == 0 && superclass.size() >= 0) {
			result = false;
		}else if(test.size() > 0 && superclass.size() == 0) {
			result = true;
		}
		else {
			for(MethodDeclaration testfixture: test) {
				boolean samemethod = samemethod(testfixture, superclass);
				if(samemethod == false){
					result = true;
					break;
				}else {
						for(MethodDeclaration superclassmethod: superclass) {
							if(superclassmethod.getNameAsString().equals(testfixture.getNameAsString())) {
								result = compareMethodFixture(testfixture, superclassmethod, superclasscu);
								break;
							}
						}
				}
			}
		}
		return result;		
	}
	
	public boolean samemethod(MethodDeclaration child, List<MethodDeclaration> superclass) {
		boolean contain_same_method = false;
		List<String> supermethods = new ArrayList<String>();
		for(MethodDeclaration superfixture: superclass) {
			supermethods.add(superfixture.getNameAsString());
		}
		for(String supermethod: supermethods) {
			if(supermethod.equals(child.getNameAsString())) {
				contain_same_method = true;
				break;
			}
		}
		return contain_same_method;
	}
 
	/* testfixture[0] is @BeforeClass
	 * testfixture[1] is @Before || @BeforeEach
	 * testfixture[2] is @AfterClass
	 * testfixture[3] is @After ||@AfterEach
	 */
	public boolean compareTestFixture(File testfile, File superclass) {
		boolean scatter = false;
		
		JunitCount junitcount = new JunitCount();
		GetCU getcu = new GetCU();
		CompilationUnit testfilecu = getcu.getCu(testfile);	
		CompilationUnit superclasscu = getcu.getCu(superclass);	
		if(testfilecu == null || superclasscu == null) {
			return false;
		}
		
		//compare @BeforeClass
		List<MethodDeclaration> testfile_beforeClassmethod = collector.getTestFixture(testfile)[0];
		List<MethodDeclaration> superclasss_beforClassemethod = collector.getTestFixture(superclass)[0];	
		if(junitcount.countbeforeClassNumber(testfilecu) > 0 && junitcount.countbeforeClassNumber(superclasscu) > 0 ) {
			scatter = compareListMethodFixture(testfile_beforeClassmethod,superclasss_beforClassemethod,superclasscu);	
		}
				
		//compare @Before
		List<MethodDeclaration> testfile_beforemethod = collector.getTestFixture(testfile)[1];
		List<MethodDeclaration> superclasss_beforemethod = collector.getTestFixture(superclass)[1];		
		if(junitcount.countbeforeNumber(testfilecu) > 0 && junitcount.countbeforeNumber(superclasscu) > 0 ) {
			scatter = compareListMethodFixture(testfile_beforemethod,superclasss_beforemethod,superclasscu);
		}
		
		//compare @AfterClass
		List<MethodDeclaration> testfile_afterClassmethod = collector.getTestFixture(testfile)[2];
		List<MethodDeclaration> superclasss_afterClassmethod = collector.getTestFixture(superclass)[2];
		if(junitcount.countafterClassNumber(testfilecu) > 0 && junitcount.countafterClassNumber(superclasscu) > 0 ) {
			scatter = compareListMethodFixture(testfile_afterClassmethod,superclasss_afterClassmethod,superclasscu);
		}
		
		//compare @After
		List<MethodDeclaration> testfile_aftermethod = collector.getTestFixture(testfile)[3];
		List<MethodDeclaration> superclasss_aftermethod = collector.getTestFixture(superclass)[3];
		if(junitcount.countafterNumber(testfilecu) > 0 && junitcount.countafterNumber(superclasscu) > 0 ) {
			scatter = compareListMethodFixture(testfile_aftermethod,superclasss_aftermethod,superclasscu);
		}
		
		return scatter;
		
	}
	
	
	public void getScatterTestFixture(List<File>testfiles) {
		HashMap<File, HashSet<JavaClass>> file_all_extendsclass = new HashMap<File, HashSet<JavaClass>>();
		file_all_extendsclass = filemapping.getExtendGraph(testfiles);
		
		HashMap<JavaClass,File> javaclass_file_mapping = new HashMap<JavaClass,File>();	
		javaclass_file_mapping = filemapping.getJavaClassFileMapping(testfiles);
		
		JunitCount junitcount = new JunitCount();
		GetCU getcu = new GetCU();
		
		for(File file: file_all_extendsclass.keySet()) {			
			CompilationUnit cu = getcu.getCu(file);
			for(JavaClass extendsclass: file_all_extendsclass.get(file)) {
				File extendfile = javaclass_file_mapping.get(extendsclass);
				if(extendfile == null) {
					continue;
				}
				boolean scatter = compareTestFixture(file, extendfile);
				if(scatter) {
					System.out.println("TESTSMELL2: "+ file + " || " + extendfile);
					Output output = new Output();
					String constantpath  = "/Users/zipeng/Projects/10project/";
					String[] smell2 = {"Smell2","Redefine testfixture",file.toString().replace(constantpath, ""),extendfile.toString().replace(constantpath, "")};
					output.writerCSV(smell2, Constant.testsmell);	
				}
			}
		}
	}
				
				
				
				
				
				
				
				
				
				//CompilationUnit extendcu = getcu.getCu(extendfile);
				
				
//				if(junitcount.countbeforeClassNumber(extendcu) > 0 && junitcount.countbeforeClassNumber(cu) > 0 ) {
//					//found test smell 2: @beforeclass is re-defined
//					System.out.println("Smell2:Redefine @beforeclass "+ file + " || " + extendfile);
//					Output output = new Output();
//					String constantpath  = "/Users/zipeng/Projects/10project/";
//					String[] smell2 = {"Smell2","Redefine @BeforeClass",file.toString().replace(constantpath, ""), extendfile.toString().replace(constantpath, "")};
//					output.writerCSV(smell2, Constant.testsmell);	
//				}
//				if(junitcount.countbeforeNumber(cu) > 0 && junitcount.countbeforeNumber(extendcu) > 0) {
//					//found test smell 2 : @before is re-defined
//					System.out.println("Smell2:Redefine @before "+ file + " || " + extendfile);
//					Output output = new Output();
//					String constantpath  = "/Users/zipeng/Projects/10project/";
//					String[] duptestrun = {"Smell2","Redefine @BeforeClass",file.toString().replace(constantpath, ""), extendfile.toString().replace(constantpath, "")};		
//					String[] smell2 = {"Smell2","Redefine @Before",file.toString().replace(constantpath, ""), extendfile.toString().replace(constantpath, "")};
//					output.writerCSV(smell2, Constant.testsmell);	
//				}
		 
			 		 
	 
		
 
}
