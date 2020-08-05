package util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class FetchJavaFiles {
    /*
     * find all .java files in project (include test files) 
     */
	public  List<File> getJavaFiles(String javadir) {
		List<File> javafiles = new ArrayList<File>();
		File parsefile = new File(javadir);
		if(parsefile.isDirectory()) {
			File[] files = parsefile.listFiles();
			if(files.length > 0) {
				for(File file : files) {
					javafiles.addAll(getJavaFiles(file.toString()));
				}
			}				
		}
		else {
			if(javadir.endsWith(".java")) {
				javafiles.add(parsefile);
			}
		}
		return javafiles;
	}
	
	//find all the test files
	public List<File> getTestFiles(String javadir){
		File ITest = new File(javadir);		
		List<File> ITFiles = new ArrayList<File>();
		Stack<File> filestack = new Stack<File>();
		File popfile = new File("");		
		filestack.push(ITest);		 
		while(!filestack.isEmpty()){
			popfile = filestack.pop();
			if(popfile.isDirectory()) {
				File[] alltestfiles = popfile.listFiles();
				for(File itfile: alltestfiles) {
					filestack.push(itfile);
				}
			}
			if((popfile.toString().toLowerCase().contains("test")||
					popfile.getName().contains("Test"))
					&& popfile.toString().endsWith(".java")) {
				
				ITFiles.add(popfile);
			}
		}
		return ITFiles;
	}

}
