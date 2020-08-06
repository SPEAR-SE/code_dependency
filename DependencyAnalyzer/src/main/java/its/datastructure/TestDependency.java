package its.datastructure;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import analyze.PackageDistance;

public class TestDependency {
	private File testfile = new File("");
	 
	private DirectDepend testdirectdependency;
	private DirectIndirectDepend tdid;
	private List<PackageDistance>directpackagedistance = new ArrayList<PackageDistance>();
	 	
	public TestDependency(File testfile, 
			List<PackageDistance>directpackagedistance,
			DirectDepend testdirectdepend,DirectIndirectDepend tdid){
		this.testfile = testfile;
		
		this.directpackagedistance = directpackagedistance;	
	
		this.testdirectdependency = testdirectdepend;
		this.tdid = tdid;
	}
	public File getTestfile() {
		return this.testfile;
	}
	public void setTestfile(File testfile) {
		this.testfile = testfile;
	}
	
	public void settdd(DirectDepend tdd) {
		this.testdirectdependency = tdd;
	}
	public DirectDepend gettdd() {
		return this.testdirectdependency;
	}
	public void settdid(DirectIndirectDepend tdid) {
		this.tdid = tdid;
	}
	public DirectIndirectDepend gettdid() {
		return this.tdid;
	}
	

	public void setDirpackagedistance(List<PackageDistance>directpackagedistance ) {
		this.directpackagedistance = directpackagedistance;
	}
	public List<PackageDistance> getDirpackagedistance() {
		return this.directpackagedistance ;
	}
	
	
}
