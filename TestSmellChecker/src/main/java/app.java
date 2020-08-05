import java.io.File;
import java.util.List;
 
import testsmell.TS_DupTestRun;
import testsmell.TS_InheritForPolymorphism;
import testsmell.TS_ScatterTestFixture;
import util.FetchJavaFiles;

public class app {

	public static void main(String[] args) {
		String projectdirpath = "/Users/zipeng/Projects/10project/californium";
	 
				
		String jardir = projectdirpath;
		FetchJavaFiles fetchJavaFiles = new FetchJavaFiles();		
		List<File> testfiles = fetchJavaFiles.getTestFiles(projectdirpath);
		
		TS_DupTestRun testsmell_pattern1 = new TS_DupTestRun();
		testsmell_pattern1.getDuplicateTestRun(testfiles);
		 
		TS_ScatterTestFixture testsmell_pattern2 = new TS_ScatterTestFixture();
		testsmell_pattern2.getScatterTestFixture(testfiles);
		
		List<File> projectfiles = fetchJavaFiles.getJavaFiles(projectdirpath);
		 		
		TS_InheritForPolymorphism testsmell_pattern3 = new TS_InheritForPolymorphism();
		testsmell_pattern3.getInheritForPolymorphism(projectfiles, testfiles);
		
 
	}
	
 

}
