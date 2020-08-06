package analyze;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import its.datastructure.Packagedata;


public class PackageDistance implements Packagedata {
	private File file;
	private File comparefile;
	int distance = 0;
	
	public PackageDistance(File file, File comparefile, int distance) {
		this.file = file;
		this.comparefile = comparefile;
		this.distance = distance;
	}
	
	/* (non-Javadoc)
	 * @see its.callgraph.datastructure.packagedata#setfile(java.io.File)
	 */
	@Override
	public void setfile(File file) {
		this.file = file;
	}
	/* (non-Javadoc)
	 * @see its.callgraph.datastructure.packagedata#setcomparefile(java.io.File)
	 */
	@Override
	public void setcomparefile(File comparefile) {
		this.comparefile = comparefile;
	}
	/* (non-Javadoc)
	 * @see its.callgraph.datastructure.packagedata#setdistance(int)
	 */
	@Override
	public void setdistance(int distance) {
		this.distance = distance;
	}
	/* (non-Javadoc)
	 * @see its.callgraph.datastructure.packagedata#getdistance()
	 */
	@Override
	public int getdistance() {
		return this.distance;
	}
	/* (non-Javadoc)
	 * @see its.callgraph.datastructure.packagedata#getcomparefile()
	 */
	@Override
	public File getcomparefile() {
		return this.comparefile;
	}
	/* (non-Javadoc)
	 * @see its.callgraph.datastructure.packagedata#getfile()
	 */
	@Override
	public File getfile() {
		return this.file;
	}
	
	public List<PackageDistance> analyzePackageDistance(HashSet<File> files,String projectdir) {
		HashSet<File>  newfiles = new HashSet<File>();
		newfiles.addAll(files);
		List<PackageDistance> pds = new ArrayList<PackageDistance>();
		if(newfiles.isEmpty()) {
			return null;
		}
		else if(newfiles.size() == 1) {
			PackageDistance pd = new PackageDistance(null,null,0);
			pd.setfile(file);
			pd.setcomparefile(file);
			pd.setdistance(0);
			pds.add(pd);
			return pds;
		}
		else {			
			//List<Integer> directpackagedistance = new ArrayList<Integer>(); 
			
			Path filepath = null;
			int pathlength = 0;
			for(File file: newfiles) {					
				String subfilestring = file.getAbsolutePath().replaceAll(projectdir, "");
				File tmpfile = new File(subfilestring);
				Path path = tmpfile.toPath();				
				int fpathlen = path.getNameCount();
				if(fpathlen > pathlength) {
					pathlength = fpathlen;
				}		
			}
			//System.out.println(pathlength);
			Iterator<File> fileit = newfiles.iterator();
			while(fileit.hasNext()) {
				File file = fileit.next();			
				String newfilepath = file.getAbsolutePath().replaceAll(projectdir, "");
				filepath = (new File(newfilepath)).toPath();
				HashSet<File> comparefiles = new HashSet<File>();
    			comparefiles.addAll(files);
				int weight = 0;
				comparefiles.remove(file);
				for(File comparefile: comparefiles) {
					PackageDistance pd = new PackageDistance(null,null,0);
					pd.setfile(file);
					pd.setcomparefile(comparefile);
					String newcomparepath = comparefile.getAbsolutePath().replaceAll(projectdir, "");
					Path comparefilepath = (new File(newcomparepath)).toPath();
					
					for(int i = 0; i < pathlength; i++ ) {
						if(filepath.getName(i).equals(comparefilepath.getName(i))){
							continue;
						}
						else {
							weight = pathlength-1-i;	
						//	System.out.println("wight: "+weight);
							pd.setdistance(weight);
							//directpackagedistance.add(weight);						
							break;
						}										
					}
					pds.add(pd);				
				}
				fileit.remove();
			}
			return pds;
		}	
	}
}
