package test.selection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
import util.GetCU;
import util.GetCallGraphParent;
import util.GetCodeDependencies;
import util.GetDirectDependData;
import util.PoslishFileClassinfo;
import util.getFileInfoInSystem;



public class UpdateForEachCommit {
	
	public List<File> get_changed_files(int day, String projectdir) throws FileNotFoundException{
		List<File> changedfiles = new ArrayList<File>();
		String diffresultpath = Constant.difffile + "_"+day+".txt";		
		BufferedReader br;
		 
		br = new BufferedReader(new FileReader(new File(diffresultpath)));
 	
		String line = "";
			
		try {
			while((line = br.readLine()) != null) {
				if(line.contains(".java") ) {					
					String changedfilepath = projectdir + '/' + line ;
					File changedfile = new File(changedfilepath);
					changedfiles.add(changedfile);								
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

		// checkout the last commit of a day.
		String commit = get_commit_from_log(diffresultpath);
		System.out.println("checkout commit: " + commit);
		if(! commit.equals("")) {
			CheckoutCommit cc = new CheckoutCommit();
	 		cc.checkout_a_commit_file(commit, projectdir);
		}
		
		
		return changedfiles;
	}
	
	
	
	public String get_commit_from_log(String filepath) {
		String commit = "";
		String line = "";
		BufferedReader br;
		try {			 
			br = new BufferedReader(new FileReader(new File(filepath)));	
			while((line = br.readLine()) != null) {				
				if(line.contains("commit ")) {
					commit = line.substring(7);
					
					break;
				}
			}
			try {
				br.close();
			} catch (IOException e) {			
				System.out.println(e.toString());
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			System.out.println(e.toString());
	    }
       return commit;
	}
	

	
	
	/* step1. for each added/updated file, re-analyze its file-class relationship
	 * step2. for each deleted file, nothing need to do because if any file called this will be updated。 
	 * step3. add this info to allfileinfo. especially need to update setDependenciesName, setcalledjavaclass,setInterfaceMapForUpdate,setInterfaces_includes_extends
	 * step4. re-polish fileclassinfo
	 * step5. re-gain system info
	 * step6. do test select.  */
	
	public List<FileClassinfo> update_allfileinfo(List<FileClassinfo> allfileinfo, List<File> changedfiles,String projectdirpath, String jardir) {
		SetEnv envset = new SetEnv();	
		TypeSolver typesolver = envset.setTypesolver(projectdirpath, jardir, 2);
		envset.setStaticParserConfiguration(typesolver);			
		
		List<File> projectfiles = envset.getJavaFiles(projectdirpath);
		
		getFileInfoInSystem gf = new getFileInfoInSystem(null, null);
		
		//re-analyze file-class relationship for added/updated file.
		for(File file_changed : changedfiles) {
			if(!file_changed.exists()) {
				FileClassinfo toremove = null;
				for(FileClassinfo fi: allfileinfo) {
					// if file is updated:
					if (fi.getfilename().equals(file_changed)) {
						toremove = fi; 
						break;
					}
				}
				allfileinfo.remove(toremove);
				continue;
			}
			System.out.println("re-analyze file-class relationship for changed file:" + file_changed);
			FileClassinfo updated_file_info = gf.getonefileinfo(file_changed,projectfiles,typesolver);
			Boolean found = false;
			for(FileClassinfo fi: allfileinfo) {
				// if file is updated:
				if (fi.getfilename().equals(file_changed)) {
					fi = updated_file_info;
					found = true;
					break;
				}		
			}	
			//else, if file is newly added:
			if(!found) {
				if(updated_file_info !=null) {
				    allfileinfo.add(updated_file_info);
			    }
			}		     
		}
		// re-polish fileclassinfo
		allfileinfo = re_polish_allfileinfo(allfileinfo, projectfiles,changedfiles);
		return allfileinfo;
	}
	
	public List<FileClassinfo> re_polish_allfileinfo(List<FileClassinfo> allfileinfo, List<File> projectfiles,List<File> changedfiles){
		//if changes is very small, sacrify precision to time.
//		if(changedfiles.size() <=3) {
//			return allfileinfo;
//		}
		PoslishFileClassinfo gia = new PoslishFileClassinfo();
	 	gia.polishFileClassInfo_allimport(allfileinfo);	
	 	gia.polistTest(allfileinfo, projectfiles);
	 	gia.givepackagename(allfileinfo,projectfiles);
	 	gia.polishFileClassInfo_extends(allfileinfo); 	
	 	return allfileinfo;
	}
	
	public HashSet<File>  find_file_need_to_recal(List<File>changedfiles, List<DirectDepend> old_direct_depends) {
		HashSet<File> files_need_to_recalculate = new HashSet<File>();
		if(old_direct_depends != null) {		
			for(DirectDepend dd : old_direct_depends) {
				for(File changefile: changedfiles) {
					if( dd.getdependParentsource().contains(changefile) || dd.getdependParenttest().contains(changedfiles)) {
						files_need_to_recalculate.add(dd.getfile());
					}
					break;
				}		
			}
		}
		return files_need_to_recalculate;
	}
	
	
	public Systeminfo re_calculate_systeminfo(String projectdir, String jardir,List<FileClassinfo> new_allfileinfo,Systeminfo systeminfo, List<File>changedfiles) {
    	Systeminfo updated_newsysteminfo = new Systeminfo();
//    	updated_newsysteminfo = updated_newsysteminfo.getsysteminfo(projectdir, jardir, new_allfileinfo);
//		return updated_newsysteminfo;
    	
    	List<DirectDepend> old_direct_depends = systeminfo.getAlldirectdepend();
		List<DirectDepend> new_direct_depends = new ArrayList<DirectDepend>();
		if(old_direct_depends.size() > 0) {
			new_direct_depends.addAll(old_direct_depends);
		}
		HashSet<File> files_need_to_recalculate = find_file_need_to_recal(changedfiles, old_direct_depends);
		
		if(files_need_to_recalculate.size() > 0) {
			SetEnv envset = new SetEnv();
			envset.setTypesolver(projectdir, jardir, 2);
			TypeSolver ts = envset.getTypesolver();
			envset.setParserConfiguration(ts);	
			Map<JavaClass, HashSet<JavaClass>> new_ci = new HashMap<JavaClass, HashSet<JavaClass>>();	
			Map<JavaClass, HashSet<JavaClass>> new_ci_includeextends = new HashMap<JavaClass, HashSet<JavaClass>>();
			Map<JavaClass, HashSet<JavaClass>> new_pa = new HashMap<JavaClass, HashSet<JavaClass>>();
			
			Systeminfo new_systeminfo = new Systeminfo();
			
			new_ci = new_systeminfo.extractInterfaceFromFileinfos(new_allfileinfo);	 
			new_ci_includeextends = new_systeminfo.extractInterface_includeextends_FromFileinfos(new_allfileinfo);
			GetCallGraphParent gcgp = new GetCallGraphParent();
			new_pa = gcgp.getcallgraphparent(new_ci);
			
			GetCodeDependencies getcodedependencies = new GetCodeDependencies();		
			List<File> projectfiles = envset.getJavaFiles(projectdir);	
			for(File recal_file: files_need_to_recalculate) { 								 
					GetCU getcu = new GetCU();
					CompilationUnit cu = getcu.getCu(recal_file);
					DirectDepend directdependinfo = new DirectDepend(new File(""),new HashSet<File>(),
							new HashSet<File>(),new HashSet<File>(),new HashSet<File>(),new HashSet<File>(),
							new HashSet<File>(),new HashSet<File>(),new HashSet<File>(),
							new HashSet<File>(),new HashSet<JavaClass>(),
							new HashSet<File>(),new HashSet<File>(),new HashSet<File>(),new HashSet<File>(),new HashSet<File>(),
							new HashSet<File>(),new HashSet<File>(),true,new HashSet<File>());
					if(cu != null) {
						System.out.println("Getting changed codes DIRECT dependencies: " + recal_file.getAbsolutePath());			
						GetDirectDependData getdirectdependdata = new GetDirectDependData();
						directdependinfo = getdirectdependdata.getDirectDepends(recal_file, cu, new_allfileinfo, new_pa,projectfiles);								
						
						for(DirectDepend dd :old_direct_depends) {
							if (dd.getfile().equals(recal_file)) {
								new_direct_depends.remove(dd);
								break;
							}
						}						
						new_direct_depends.add(directdependinfo);
 			 		 
					}		
				}
			List<DirectIndirectDepend> new_alldepends =  new ArrayList<DirectIndirectDepend>();
			new_alldepends = getcodedependencies.get_direct_indirect_dependencies(new_direct_depends, new_allfileinfo);
			new_alldepends = getcodedependencies.get_parent_direct_indirect_dependencies(new_alldepends, new_allfileinfo);
			
			updated_newsysteminfo.setCi(new_ci);
			updated_newsysteminfo.setCi_includeextends(new_ci_includeextends);
			updated_newsysteminfo.setPa(new_pa);
			updated_newsysteminfo.setAlldirectdepend(new_direct_depends);
			updated_newsysteminfo.setAlldepends(new_alldepends);
			return updated_newsysteminfo;
			}
		return systeminfo;
	}
		
		
		
		
	
}
	
	
 
