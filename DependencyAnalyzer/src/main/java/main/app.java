package main;

import java.io.IOException;
import java.util.List;

import analyze.AnalyzeSourceCode_DirectInDirectTest;
import analyze.AnalyzeTestDependency;
import configuration.Constant;
import its.datastructure.DirectIndirectDepend;
import its.datastructure.FileClassinfo;
import its.datastructure.Systeminfo;
import its.datastructure.TestDependency;
import test.selection.Select_Test_For_EachCommit;


public class app {

	public static void main(String[] args) throws Exception {
		// input the test project full path here.
		String dir = "/Users/zipeng/Projects/10project/cucumber";
		int TotalAnalyzeCommits = 2;
		

		String projectdir = dir;		
		String testdir = dir;				 					 		 					
		String jardir = dir;
		  
	
		String version = "";
		String masterbranch = "";
		version = getVersion(projectdir);
	
		masterbranch = getMasterbranch(projectdir);
		
		runpythonscript_daily(TotalAnalyzeCommits,projectdir,version,masterbranch);

		AnalyzeTestDependency alt = new AnalyzeTestDependency();
		List<FileClassinfo> allfileinfo = alt.getAllfileinfo(projectdir, jardir);
		
		Systeminfo newsysteminfo = new Systeminfo();
		newsysteminfo = newsysteminfo.getsysteminfo(projectdir, jardir, allfileinfo);
		 	
		 
		List<DirectIndirectDepend> alldepends =   newsysteminfo.getAlldepends();
		  		
		List<TestDependency> alldependencies = alt.getTestsInformation(projectdir, testdir, jardir,allfileinfo,newsysteminfo);
		
		/*RQ2, RQ3*/
		AnalyzeSourceCode_DirectInDirectTest analyzesourcecode = new AnalyzeSourceCode_DirectInDirectTest();
		analyzesourcecode.analyzeSourceCodeCallbyTest(alldependencies,projectdir,allfileinfo);
		
		/*RQ1*/
		Select_Test_For_EachCommit st = new Select_Test_For_EachCommit();
		st.select_test_for_each_commit(TotalAnalyzeCommits, allfileinfo, projectdir, jardir,alldepends, newsysteminfo);
 				
	}
	
	 
	
	public static String getVersion(String projectdir) {
		String version = "";
		String projectname = projectdir.substring(projectdir.lastIndexOf("/")+1);
		//System.out.println(projectname);
		switch(projectname) {
		  case "accumulo": version = "rel/1.9.2";
		    break;
		  case "hadoop": version = "rel/release-3.2.0";
			break;
		  case "cxf": version = "cxf-3.3.0";
	         break;	  
		  case "flink": version = "release-1.7.1";
	         break;
		  case "hbase": version = "rel/2.1.2";
	         break;
		  case "jclouds": version = "rel/jclouds-2.1.2";
	         break;	
		  case "wicket": version = "rel/wicket-8.0.0";
	         break;
		  case "cassandra": version = "cassandra-3.11.3";
	         break;
		  case "kafka": version = "2.1.0";
	         break;
		  case "camel": version = "camel-2.22.3";
	         break;
		  case "bookkeeper": version = "release-4.9.0";
	         break;
		  case "hive": version = "rel/release-3.1.0";
	         break;   
		  case "californium": version = "2.3.0";
	         break;
	      case "cucumber": version = "v6.2.2";
	         break;
	      case "jetty": version = "jetty-10.0.0.beta0";
	         break; 
		}
		return version;		
	}
	public static String getMasterbranch(String projectdir) {
		String masterbranch = "";
		String projectname = projectdir.substring(projectdir.lastIndexOf("/")+1);
		switch(projectname) {
		  case "accumulo": masterbranch = "master";
		    break;
		  case "hadoop": masterbranch = "trunk";
			break;
		  case "cxf": masterbranch = "master";
	         break;	  
		  case "flink": masterbranch = "master";
	         break;
		  case "hbase": masterbranch = "master";
	         break;
		  case "jclouds": masterbranch = "master";
	         break;
		  case "wicket": masterbranch = "master";
	         break;
		  case "cassandra": masterbranch = "cassandra-3.11.3";
	         break;
		  case "kafka": masterbranch = "trunk";
	         break;
		  case "camel": masterbranch = "master";
	         break;
		  case "bookkeeper": masterbranch = "master";
	         break;
		  case "hive": masterbranch = "master";
	         break;
		  case "californium": masterbranch = "master";
		      break;
		  case "cucumber": masterbranch = "main";
		      break;
		  case "jetty": masterbranch = "jetty-10.0.x";
		      break;
		      
		}
		return masterbranch;		
	}
	/*
	 * use python script to generate all changed files between two commits
	 */
 
	 
	public static void runpythonscript_daily(int arg,String arg2,String version,String masterbranch) {
		//use python script to get all the changed javaclasses.	
		String[] command = {"/usr/local/bin/python3.7",
				Constant.dailypythoncript,
				String.valueOf(arg),arg2,version,masterbranch
				};
 
		try {
			Process p = Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			System.out.println(e.toString());
		}
	}

 

}
