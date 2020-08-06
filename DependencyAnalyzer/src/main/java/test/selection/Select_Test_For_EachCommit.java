package test.selection;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import its.datastructure.DirectIndirectDepend;
import its.datastructure.FileClassinfo;
import its.datastructure.Systeminfo;

public class Select_Test_For_EachCommit {
	
	public void select_test_for_each_commit(int TotalAnalyzeCommits,List<FileClassinfo> allfileinfo, String projectdirpath, String jardir,List<DirectIndirectDepend> alldepends, Systeminfo newsysteminfo) {
		UpdateForEachCommit updates = new UpdateForEachCommit();
		List<FileClassinfo> old_fileinfo = new ArrayList<FileClassinfo>();
		old_fileinfo.addAll(allfileinfo);
		TestSelect testselection = new TestSelect();
		testselection.printSelectInfoTitle();
		
		for(int i = 0; i < TotalAnalyzeCommits; i++) {
			
		    List<File> changedfiles = new ArrayList<File>();
			try {
				changedfiles = updates.get_changed_files(i, projectdirpath);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (changedfiles.size() > 0) {
				List<FileClassinfo> updated_allfileinfo = updates.update_allfileinfo(allfileinfo, changedfiles, projectdirpath, jardir);
				if(!updated_allfileinfo.equals(old_fileinfo)) {
					Systeminfo update_systeminfo = updates.re_calculate_systeminfo(projectdirpath, jardir, updated_allfileinfo,newsysteminfo,changedfiles);
					List<DirectIndirectDepend> updated_alldepends = update_systeminfo.getAlldepends();

					SelectResult sr = testselection.Selecttests(allfileinfo, updated_alldepends, i, projectdirpath);
					testselection.printSelectInfo(i, sr, allfileinfo,updated_alldepends);
				}
				else {
					SelectResult sr = testselection.Selecttests(allfileinfo, alldepends, i,projectdirpath);
					testselection.printSelectInfo(i, sr, allfileinfo,alldepends);
				}
				
			}
			
			else {
				SelectResult sr = testselection.Selecttests(allfileinfo, alldepends, i,projectdirpath);
				testselection.printSelectInfo(i, sr, allfileinfo,alldepends);
			}
		
	    }
	}
	
 
}
