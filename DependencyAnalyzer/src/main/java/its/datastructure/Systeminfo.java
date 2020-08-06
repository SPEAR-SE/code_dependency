package its.datastructure;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import configuration.Constant;
import util.GetCallGraphParent;
import util.GetCodeDependencies;
import util.Output;

public class Systeminfo {
	private Map<JavaClass, HashSet<JavaClass>> ci = new HashMap<JavaClass, HashSet<JavaClass>>();	
	private Map<JavaClass, HashSet<JavaClass>> ci_includeextends = new HashMap<JavaClass, HashSet<JavaClass>>();
	private Map<JavaClass, HashSet<JavaClass>> pa = new HashMap<JavaClass, HashSet<JavaClass>>();
	private List<DirectDepend> alldirectdepend = new ArrayList<DirectDepend>();
	private List<DirectIndirectDepend> alldepends = new ArrayList<DirectIndirectDepend>();
	
	public Map<JavaClass, HashSet<JavaClass>> getCi() {
		return ci;
	}
	
	public void setCi(Map<JavaClass, HashSet<JavaClass>> ci) {
		this.ci = ci;
	}
	
	public Map<JavaClass, HashSet<JavaClass>> getCi_includeextends() {
		return ci_includeextends;
	}
	
	public void setCi_includeextends(Map<JavaClass, HashSet<JavaClass>> ci_includeextends) {
		this.ci_includeextends = ci_includeextends;
	}

	public Map<JavaClass, HashSet<JavaClass>> getPa() {
		return pa;
	}

	public void setPa(Map<JavaClass, HashSet<JavaClass>> pa) {
		this.pa = pa;
	}

	public List<DirectDepend> getAlldirectdepend() {
		return alldirectdepend;
	}

	public void setAlldirectdepend(List<DirectDepend> alldirectdepend) {
		this.alldirectdepend = alldirectdepend;
	}

	public List<DirectIndirectDepend> getAlldepends() {
		return alldepends;
	}

	public void setAlldepends(List<DirectIndirectDepend> alldepends) {
		this.alldepends = alldepends;
	}
	
	
	
	public Systeminfo getsysteminfo(String projectdirpath,String jardir,List<FileClassinfo>allfileinfo) {
		Map<JavaClass, HashSet<JavaClass>> ci = new HashMap<JavaClass, HashSet<JavaClass>>();	
		Map<JavaClass, HashSet<JavaClass>> ci_includeextends = new HashMap<JavaClass, HashSet<JavaClass>>();
		Map<JavaClass, HashSet<JavaClass>> pa = new HashMap<JavaClass, HashSet<JavaClass>>();
		
		Systeminfo systeminfo = new Systeminfo();
		
		ci = extractInterfaceFromFileinfos(allfileinfo);	 
		ci_includeextends = extractInterface_includeextends_FromFileinfos(allfileinfo);
		GetCallGraphParent gcgp = new GetCallGraphParent();
		pa = gcgp.getcallgraphparent(ci);
		
		GetCodeDependencies getcodedependencies = new GetCodeDependencies();		
		List<DirectDepend> alldirectdepend = new ArrayList<DirectDepend>();
		alldirectdepend = getcodedependencies.get_direct_dependencies(projectdirpath, jardir, allfileinfo, ci_includeextends, pa);
		System.out.println("Successfully get all direct depend of the system. now get the indirect dependency: ");
		List<DirectIndirectDepend> alldepends =  new ArrayList<DirectIndirectDepend>();
		alldepends = getcodedependencies.get_direct_indirect_dependencies(alldirectdepend, allfileinfo);
		alldepends = getcodedependencies.get_parent_direct_indirect_dependencies(alldepends, allfileinfo);
				 
		
		systeminfo.setCi(ci);
		systeminfo.setCi_includeextends(ci_includeextends);
		systeminfo.setPa(pa);
		systeminfo.setAlldirectdepend(alldirectdepend);
		systeminfo.setAlldepends(alldepends);
		return systeminfo;
	}

	/**
	 * return Map<JavaClass, HashSet<JavaClass>>
	 * @param allfileinfo
	 * @return
	 */
	public Map<JavaClass, HashSet<JavaClass>> extractInterfaceFromFileinfos(List<FileClassinfo> allfileinfo) {
		Map<JavaClass, HashSet<JavaClass>> classInterfaces = new HashMap<JavaClass, HashSet<JavaClass>>();		
		for(FileClassinfo fi: allfileinfo) {			
			if(classInterfaces.size() == 0) {
				classInterfaces.putAll(fi.getInterfaces());			
			}
			else {
				Set<JavaClass> keys = fi.getInterfaces().keySet();
				for(JavaClass key: keys) {
					if(classInterfaces.keySet().contains(key)) {
						if(classInterfaces.get(key)!=null && fi.getInterfaces()!= null && fi.getInterfaces().get(key) !=null ) {
							classInterfaces.get(key).addAll(fi.getInterfaces().get(key));
						}
						
					}
					else {
						classInterfaces.put(key, fi.getInterfaces().get(key));
					}
				}			
			}
		}	
		//System.out.println("classInterface in extractInterface: " + classInterfaces);
		return classInterfaces;
	}

	/**
	 * return Map<JavaClass, HashSet<JavaClass>>
	 * @param allfileinfo
	 * @return
	 */
	public Map<JavaClass, HashSet<JavaClass>> extractInterface_includeextends_FromFileinfos(List<FileClassinfo> allfileinfo) {
		Map<JavaClass, HashSet<JavaClass>> classInterfaces = new HashMap<JavaClass, HashSet<JavaClass>>();		
		for(FileClassinfo fi: allfileinfo) {			
			if(classInterfaces.size() == 0) {
				classInterfaces.putAll(fi.getInterfaces_includes_extends());			
			}
			else {
				Set<JavaClass> keys = fi.getInterfaces_includes_extends().keySet();
				for(JavaClass key: keys) {
					if(classInterfaces.keySet().contains(key)) {
						if (classInterfaces.get(key) != null && fi.getInterfaces_includes_extends() != null && fi.getInterfaces_includes_extends().get(key) !=null)
						classInterfaces.get(key).addAll(fi.getInterfaces_includes_extends().get(key));
					}
					else {
						classInterfaces.put(key, fi.getInterfaces_includes_extends().get(key));
					}
				}			
			}
		}	
		//System.out.println("classInterface in extractInterface: " + classInterfaces);
		return classInterfaces;
	}


}
