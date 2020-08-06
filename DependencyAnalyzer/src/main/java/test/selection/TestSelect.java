package test.selection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import configuration.Constant;
import its.datastructure.DirectIndirectDepend;
import its.datastructure.FileClassinfo;
import util.Output;



public class TestSelect {
	
	
	/*
	 *  find all the changed files, categorize them to: 
	 *  tests, sourcecode, utility(those in test directory but have no @test)
	 */
	public List<HashSet<File>> findchangedfiles(List<FileClassinfo> allfileinfo,int day, String projectdir)throws FileNotFoundException{
		List<HashSet<File>> changedfiles = new ArrayList<HashSet< File>>();
		
		HashSet<File> realtest = new HashSet<File>();
		HashSet<File> utility = new HashSet<File>();
		HashSet<File> sourcecode = new HashSet<File>();
		
		String diffresultpath = Constant.difffile + "_"+day+".txt";		
		BufferedReader br =  new BufferedReader(new FileReader(new File(diffresultpath)));						 
		File filename  = new File("");		
		String line ;
		String commit = "";
		try {
			while((line = br.readLine()) != null) {
			    
				if(line.startsWith("commit ")) {
					commit = line.substring(7);
				}
				
				if(line.contains(".java")) {					
					String changedfile = line.substring(0, line.indexOf(".java"));
					//System.out.println("changedfile:" + changedfile);	
					
					if(!new File(projectdir + "/" + line).exists()) {
						continue;
					}
 
					try {
						if(ignore_moved_files(commit, new File(line), projectdir)) {
							continue;
						}
					} catch (Exception e) {
						System.out.print(e);
					}
					// filename - find the changed rootclass.
					for(FileClassinfo fi: allfileinfo) {
						filename = fi.getfilename();
						//System.out.println("filename:" + filename);	
						String stripedfileename = filename.toString().replace(projectdir, "");
						stripedfileename = stripedfileename.substring(1);
						stripedfileename = stripedfileename.substring(0,stripedfileename.indexOf(".java"));
						if(stripedfileename.toString().equals(changedfile)) {							
							if(fi.getistest()) {
								realtest.add(filename);
							}
							else if(filename.getName().toLowerCase().contains("test")||
									filename.toString().contains("test/")) {
								utility.add(filename);
							}
							else {
								sourcecode.add(filename);
							}
							break;
						}
					  }
				}
			}
					
				
			 
			} catch (IOException e) {
				System.out.println(e.toString());
			}
		
			try {
				br.close();
			} catch (IOException e) {			
				System.out.println(e.toString());
			}
			changedfiles.add(realtest);
			changedfiles.add(sourcecode);
			changedfiles.add(utility);
			//System.out.println("changedfiles: "+ changedfiles);
			return changedfiles;			
					
	}
	
	public String getprocStream(InputStream in)  {
	    BufferedReader is = new BufferedReader(new InputStreamReader(in));
	        String line = "";        
	        try {
				while ((line = is.readLine()) != null) {
					System.out.println(line);
					break;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        
	        return line;
	}
	
	public boolean ignore_moved_files(String commit, File changedfile, String projectdir ) throws Exception{
		Boolean ignore = false;
		//need to run git show commit --name-status | grep filename 
		   try {
		        Runtime rt = Runtime.getRuntime();
		        String command = "git show " +  commit + " --name-status | grep " + changedfile ;		       
		        String[] cmd = {"/bin/sh", "-c",   command};
		        Process proc = rt.exec(cmd, null , new File(projectdir)); 
		        String l = getprocStream(proc.getInputStream());	 
		        if(l.startsWith("R100")) {
		        	ignore = true;
		        }
		    } catch (Exception ex) {
		        ex.printStackTrace();
		    }
	
		return ignore;
	}
	//add parent source code of changed source code --no need
	/*public HashSet<File> get_parentsourcecode_of_changedfile(File changedsourcecodes, List<DirectIndirectDepend> directindirectdependencies){
		HashSet<File> parentsourcecode_of_changed_sourcecode = new HashSet<File>(); 
		for(DirectIndirectDepend dependency: directindirectdependencies) {	
				if(dependency.getTestfile().equals(changedsourcecodes)) {		
					parentsourcecode_of_changed_sourcecode.addAll(dependency.getParent_directIndirect_sourcecode());
					break;
				}				
			}
		return parentsourcecode_of_changed_sourcecode;		 
	}
	// add parent utility code of changed source code-- no need
	public HashSet<File> get_parentutility_of_changedfile(File changedsourcecodes, List<DirectIndirectDepend> directindirectdependencies){
		HashSet<File> parentutility_of_changed_sourcecode = new HashSet<File>(); 
		for(DirectIndirectDepend dependency: directindirectdependencies) {		
				if(dependency.getTestfile().equals(changedsourcecodes)) {
					parentutility_of_changed_sourcecode.addAll(dependency.getParent_directIndirect_utility());
					break;
				}				
			}
		return parentutility_of_changed_sourcecode;		 
	}*/
	
	
	/*
	 * check whether there's any dependencies(direct + indirect) between:file and updatedfile
	 */
	public boolean checkFileDependency(File file,HashSet<File> all_dependencies_of_file, File updatedfile) {
		boolean hasdependency = false;			
		if(file.equals(updatedfile)) {
			hasdependency = true;	
		}
		else {
			for(File depends:all_dependencies_of_file) {
				if(depends.equals(updatedfile)) {
					hasdependency = true;
				}
			} 
		}			
		return hasdependency;
	}
	
		
	public SelectResult Selecttests(List<FileClassinfo> allfileinfo,List<DirectIndirectDepend> alldependencies,int day, String projectdir) {
		SelectResult sr = new SelectResult(0, new HashSet<File>(), new HashSet<File>(), new HashSet<File>(), new HashSet<File>(),
				new ArrayList<HashSet<File>>(), new HashMap<File,HashSet<File>>());
		HashSet<File> affectedTest = new HashSet<File>();	 
		HashSet<File> affectedfiles_because_realtest_changes = new HashSet<File>();
		HashSet<File> affectedfiles_because_utility_changes = new HashSet<File>();
		HashSet<File> affectedfiles_because_sourcecode_changes = new HashSet<File>();
		Map<File,HashSet<File>> selectdetailresult = new HashMap<File,HashSet<File>>();
				
		//find all the real test files in the study system.
		HashSet<File>realtestfiles = new HashSet<File>();
		realtestfiles=findallrealtest(allfileinfo);
		
		//get information about changed files
		List<HashSet<File>> changedfiles = new ArrayList<HashSet<File>>();
		try {
			changedfiles = findchangedfiles(allfileinfo,day,projectdir);
		} catch (FileNotFoundException e) {			 
			e.printStackTrace();
		}
		HashSet<File> changedrealtest = new HashSet<File>(changedfiles.get(0));
		//System.out.println("changedrealtest: "+ changedrealtest);
		HashSet<File> changedsourcecode = new HashSet<File>(changedfiles.get(1));
		
		HashSet<File> changedutility = new HashSet<File>(changedfiles.get(2));
		
		
		/*
		 * if changed files is source code, find all(direct and indirect) parent source code that depend on it
		 * add it to changed sourcecode. (because if A changes, then all the source code / utility call it might change.)
		 */	
/*		HashSet<File> parentsourcecode_of_changed_sourcecode = new HashSet<File>();
		HashSet<File> parentutility_of_changed_sourcecode = new HashSet<File>();
		for(File changesourcecodefile: changedsourcecode) {				 
			parentsourcecode_of_changed_sourcecode.addAll(get_parentsourcecode_of_changedfile(changesourcecodefile,alldependencies));			
			//System.out.println("get_parentsourcecode_of_changedfile(changesourcecodefile,alldependencies): "+ get_parentsourcecode_of_changedfile(changesourcecodefile,alldependencies));		
			parentutility_of_changed_sourcecode.addAll(get_parentutility_of_changedfile(changesourcecodefile,alldependencies));	
		}		
		changedsourcecode.addAll(parentsourcecode_of_changed_sourcecode);
		changedutility.addAll(parentutility_of_changed_sourcecode);*/
		
		//System.out.println("TotalChangedFiles: " + changedfiles);
		//System.out.println("changedsourcecode: "+ changedsourcecode);
		//System.out.println("changedutility: "+ changedutility);
		//System.out.println("changedtest:" + changedrealtest);
		
		
		//for each changed files, check whether test have dependencies with it.		
		for(File testfile: realtestfiles) {
			selectdetailresult.put(testfile, new HashSet<File>());
			//find all the files depend on test file: all direct-indirect depend on this test + all direct-indirect parent of this test
			HashSet<File> all_dir_indir_dependencies_of_testfile = new HashSet<File>();
			HashSet<File> all_realtestdependencies_of_file = new HashSet<File>();
			HashSet<File> all_realtest_parent_of_file = new HashSet<File>(); 			
			for(DirectIndirectDepend testdependency: alldependencies) {			
				if(testdependency.getFile().equals(testfile)) {
					all_dir_indir_dependencies_of_testfile = testdependency.gettestalldepend();
					all_realtestdependencies_of_file = testdependency.getDirectIndirectDependTest_Real();
					//find all parent-realtest of testfile
					all_realtest_parent_of_file = testdependency.getParent_directIndirect_realtest();
				}
			}
			
			
			/*check dependency. if testfile depend on changedfile, select this test file. 
			 * to make sure the test can be run successfully, we also select all the files that testfile depends.
			 * other tests depends on testfile will also be affected, so we need to select all the parent-realtest files.
			 * (need to check how to get all the parent-real-test)  
			 */
			for(File changedfile_realtest: changedrealtest) {			 				
				if(checkFileDependency(testfile, all_dir_indir_dependencies_of_testfile, changedfile_realtest)==true) {
					affectedTest.add(testfile);
					affectedTest.addAll(all_realtestdependencies_of_file);
					affectedTest.addAll(all_realtest_parent_of_file);	
					
					affectedfiles_because_realtest_changes.add(testfile);
					affectedfiles_because_realtest_changes.addAll(all_realtestdependencies_of_file);
					affectedfiles_because_realtest_changes.addAll(all_realtest_parent_of_file);	
					
					selectdetailresult.get(testfile).addAll(all_realtestdependencies_of_file);
					selectdetailresult.get(testfile).add(changedfile_realtest);
					selectdetailresult.get(testfile).addAll(all_realtest_parent_of_file);					
				}
			}
			
			for(File changedfile_sourcecode: changedsourcecode) {
				if(checkFileDependency(testfile, all_dir_indir_dependencies_of_testfile, changedfile_sourcecode)==true) {
					affectedTest.add(testfile);
					affectedTest.addAll(all_realtestdependencies_of_file);
					affectedTest.addAll(all_realtest_parent_of_file);
					
					affectedfiles_because_sourcecode_changes.add(testfile);
					affectedfiles_because_sourcecode_changes.addAll(all_realtestdependencies_of_file);
					affectedfiles_because_sourcecode_changes.addAll(all_realtest_parent_of_file);	
					
					selectdetailresult.get(testfile).addAll(all_realtestdependencies_of_file);
					selectdetailresult.get(testfile).add(changedfile_sourcecode);
					selectdetailresult.get(testfile).addAll(all_realtest_parent_of_file);
				}
			}
			
			for(File changefile_utility: changedutility) {
				if(checkFileDependency(testfile, all_dir_indir_dependencies_of_testfile, changefile_utility)==true) {
					affectedTest.add(testfile);
					affectedTest.addAll(all_realtestdependencies_of_file);	
					affectedTest.addAll(all_realtest_parent_of_file);
					
					affectedfiles_because_utility_changes.add(testfile);
					affectedfiles_because_utility_changes.addAll(all_realtestdependencies_of_file);	
					affectedfiles_because_utility_changes.addAll(all_realtest_parent_of_file); 	
					
					selectdetailresult.get(testfile).addAll(all_realtestdependencies_of_file);
					selectdetailresult.get(testfile).add(changefile_utility);
					selectdetailresult.get(testfile).addAll(all_realtest_parent_of_file);
				}
			}			
		}
		//System.out.println("affectedTest:" + affectedTest);
//		//remove duplicate? why my hashset not remove duplicate?
//		affectedTest = remove_duplicate(affectedTest);
//		affectedfiles_because_realtest_changes = remove_duplicate(affectedfiles_because_realtest_changes);
//		affectedfiles_because_sourcecode_changes = remove_duplicate(affectedfiles_because_sourcecode_changes);
//		affectedfiles_because_utility_changes = remove_duplicate(affectedfiles_because_utility_changes);	 
//		//  
		
		sr.setAffectedfiles_because_realtest_changes(affectedfiles_because_realtest_changes);
		sr.setAffectedfiles_because_sourcecode_changes(affectedfiles_because_sourcecode_changes);
		sr.setAffectedfiles_because_utility_changes(affectedfiles_because_utility_changes);
		sr.setAffectedTest(affectedTest);
		sr.setChangedfiles(changedfiles);
		sr.setSelectdetailresult(selectdetailresult);
		sr.setNumb_realtestfiles_analysis(realtestfiles.size());
				
		return sr;
	}
	
//	public HashSet<File> remove_duplicate(HashSet<File> dup) {
//		HashSet<File> doublecheck = new HashSet<File>();
//		doublecheck.addAll(dup);
//		for (Iterator<File> iterator = doublecheck.iterator(); iterator.hasNext();) {
//			File f = iterator.next();
//		    iterator.remove();
//		    
//		    if(doublecheck.contains(f)) {
//				System.out.println("OMG WHY WHY WHY WHY WHY WHY????????");
//				dup.remove(f);
//			}			    
//		}		
//		return dup;
//	}
//	
	
	//print things out
	public void printSelectInfo(int day, SelectResult sr,List<FileClassinfo> allfileinfo, List<DirectIndirectDepend>alldependencies) {
		// to print basic information about daily commit: the number of commits, commits date, total changed files.
		String line ;
		String date = "";
		String totalcommits = "";
		String diffresultpath = Constant.difffile + "_"+day+".txt";
		
		
		System.out.println("TestSelection: " + diffresultpath);	 
		File tmpfile = new File(diffresultpath);
				FileReader fr = null;
				try {
					fr = new FileReader(tmpfile);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}		
				BufferedReader br = new BufferedReader(fr);
				HashSet<String> modifiedfiles = new HashSet<String>() ;
				try {
					while((line = br.readLine()) != null) {
		
						if(line.contains("analyze_date")) {
							date = line.substring(line.lastIndexOf(":") + 1);
						}
						if(line.contains("total_commits_in_the_day")) {
							totalcommits = line.substring(line.lastIndexOf(":") + 1);
						}
						if(line.contains(".java")) {
							String mfile = line.substring(0, line.indexOf(".java"));
							modifiedfiles.add(mfile);
						}
					}
				} catch (IOException e) {
					System.out.println(e.toString());
				}
				try {
					br.close();
				} catch (IOException e) {			
					System.out.println(e.toString());
				}
				
				HashSet<File> affectedTest = sr.getAffectedTest();				
				//HashSet<File> NotAffectedTest = new HashSet<File>();
				HashSet<File> affectedfiles_because_realtest_changes = sr.getAffectedfiles_because_realtest_changes();
				HashSet<File> affectedfiles_because_utility_changes = sr.getAffectedfiles_because_utility_changes();
				HashSet<File> affectedfiles_because_sourcecode_changes = sr.getAffectedfiles_because_sourcecode_changes();
				
				
				List<HashSet<File>> changedfiles = sr.getChangedfiles();
				int realtestfiles = sr.getNumb_realtestfiles_analysis();
				
				int loc = 0;
				int istestnumber = 0;
			    int beforeClassNumber=0;
			    int beforeNumber=0;
			    int afterClassNumber=0;
			    int afterNumber=0;
		        for(File affectfile: affectedTest) {
		        	for(FileClassinfo fci: allfileinfo) {
		        		if(fci.getfilename().equals(affectfile)) {
		        			loc = loc + fci.getloc(); 
		        			istestnumber = istestnumber + fci.getRealtestNumber();
		        			beforeClassNumber = beforeClassNumber + fci.getbeforeClassNumber();
		        			afterClassNumber = afterClassNumber + fci.getafterClassNumber();
		        			beforeNumber = beforeNumber + fci.getbeforeNumber();
		        			afterNumber = afterNumber + fci.getafterNumber();
		        		}
		        	}
		        }
			
		         int percent = 100 * affectedTest.size()/realtestfiles;
				//output the result
				Output outputresult = new Output();			 		
				String[] data = {date,totalcommits ,
						//String.valueOf(first_commit), String.valueOf(last_commit),
						String.valueOf(modifiedfiles.size()),
						String.valueOf(changedfiles.get(0).size()),
						String.valueOf(changedfiles.get(1).size()),
						String.valueOf(changedfiles.get(2).size()),
						String.valueOf(realtestfiles),
						String.valueOf(affectedTest.size()),
						String.valueOf(percent), 
						String.valueOf(loc),
						String.valueOf(istestnumber),
						String.valueOf(beforeClassNumber),String.valueOf(afterClassNumber),
						String.valueOf(beforeNumber),String.valueOf(afterNumber),						
						String.valueOf(affectedfiles_because_realtest_changes.size()),
						String.valueOf(affectedfiles_because_utility_changes.size()),
						String.valueOf(affectedfiles_because_sourcecode_changes.size()),
						String.valueOf(100* affectedfiles_because_realtest_changes.size()/realtestfiles),						
						String.valueOf(100* affectedfiles_because_utility_changes.size()/realtestfiles),						
						String.valueOf(100* affectedfiles_because_sourcecode_changes.size()/realtestfiles)
						};
				
				outputresult.writerCSV(data,Constant.testselectresult);	
				
				String simple_date = date.replaceAll("-", "");
				
				
				/* tse brutal*/
				/* output select test class daily */
				String select_day = Constant.dayselect + "_" + simple_date  + ".csv";
				Output output = new Output();
				String[]head = {"Date","TestClassName","PackageName", "TotalCommits","TestDependencies"};
				output.writerCSV(head,select_day);
				String packageName = "";
				int dependNumber = 0;
				for (File test: affectedTest) {
					for(FileClassinfo fci: allfileinfo) {
		        		if(fci.getfilename().equals(test)) {
		        			packageName = fci.getPackage();	
		        			break;
		        		}
					}
				
					for(DirectIndirectDepend testdependency: alldependencies) {
						if(testdependency.getFile().equals(test)) {
							dependNumber = testdependency.gettestalldepend().size();
							break;
					    }
					}
					
					
					String[]select_data = {date, test.getName(),packageName, totalcommits,String.valueOf(dependNumber)};
					output.writerCSV(select_data,select_day);
				}
				
				
	}
	
	
	
	public void printSelectInfoTitle() {
		Output output = new Output();
		String[]head = {"Date","TotalCommit",
				//"firstcommit","lastcommit",
				"No.changedfiles",
				"No.changed_realtest","No.changed_sourcecode","No.changed_utility",
				"Total_Real_Test","Select_Real_Test", "Selected Percentage",
				//"Selected(becauseof affected)", "Selected(becauseof dependency)",
				"LOC of Selected", 
				"No. @test","No. @BeforeClass","No. @AfterClass",
				"No. @Before Running(=before*test)","No. @After Running",
				 "Select_because_realtest_changes", 
				 "Select_because_utility_changes",
				 "Select_because_sourcecode_changes",
				 "SelectPercent_because_realtest",
				 "SelectPercent_because_utility_changes",				 
				 "SelectPercent_because_sourcecode_changes"
				  };
		output.writerCSV(head,Constant.testselectresult);
	}
	
	
	
	//find all the real test files in the study system.
	public HashSet<File> findallrealtest(List<FileClassinfo> allfileinfo){		
		HashSet<File>realtestfiles = new HashSet<File>();
		for(FileClassinfo fc: allfileinfo) {
			if(fc.getistest()) {
				realtestfiles.add(fc.getfilename());
			}
		}
		return realtestfiles;
	}
	
	 
		
		
		
}
