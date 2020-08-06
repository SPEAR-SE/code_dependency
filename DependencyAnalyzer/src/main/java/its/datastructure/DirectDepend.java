package its.datastructure;

import java.io.File;
import java.util.HashSet;


public class DirectDepend {
	private File file;
	private Boolean isunit = true;
	private boolean boolabstract = false;
	private HashSet<File >mockfiles = new HashSet<File>();
	
	private HashSet<JavaClass> calledjavaclass;
	private HashSet<JavaClass> parentsjavaclass;
	private HashSet<JavaClass> allextend;
	private HashSet<File>testsourcefile = new HashSet<File>();
	private HashSet<File> dependparentsource= new HashSet<File>();
	
	private HashSet<File>dependtest = new HashSet<File>();
	private HashSet<File>dependparenttest = new HashSet<File>();
	
	private HashSet<File> istestutil = new HashSet<File>();
	private HashSet<File> istestabstract = new HashSet<File>();
	private HashSet<File> testutil = new HashSet<File>();
	private HashSet<File> testabstract = new HashSet<File>();
	private HashSet<File> sourceutil = new HashSet<File>();
	private HashSet<File> sourceabstract = new HashSet<File>();
	//file contains @Test
	private HashSet<File> realtestfile = new HashSet<File>();
	private HashSet<File> istestparentfile = new HashSet<File>();
	
	private HashSet<File> istestparentabstractfile = new HashSet<File>();
	private HashSet<File> istestparentutilfile = new HashSet<File>();
	private HashSet<File> testparentabstractfile = new HashSet<File>();
	private HashSet<File> testparentutilfile = new HashSet<File>();
		
	public DirectDepend(File testfile,HashSet<File>testsourcefile,HashSet<File>dependtest,
			HashSet<File> istestutil,HashSet<File>istestabstract,HashSet<File> testutil,
			HashSet<File> testabstract,HashSet<File> sourceutil,HashSet<File> sourceabstract,
			HashSet<File>istestfile,
			HashSet<JavaClass> calledjavaclass,
			HashSet<File> dependparentsource, HashSet<File>dependparenttest,HashSet<File> istestparentfile,
			HashSet<File> istestparentabstractfile, HashSet<File> istestparentutilfile,
			HashSet<File> testparentabstractfile,HashSet<File> testparentutilfile,Boolean isunit,
			HashSet<File >mockfiles
			) {
		this.testsourcefile = testsourcefile;
		this.dependtest = dependtest;
		
		this.istestabstract = istestabstract;
		this.istestutil= istestutil;
		this.testabstract = testabstract;
		this.testutil= testutil;
		this.sourceabstract = sourceabstract;
		this.sourceutil= sourceutil;
		
		this.realtestfile = istestfile;
		this.file = testfile;
		this.calledjavaclass = calledjavaclass;
		
		this.dependparentsource=dependparentsource;
		this.dependparenttest = dependparenttest;
		this.testparentabstractfile = testparentabstractfile;
		this.testparentutilfile = testparentutilfile;
		
		this.istestparentfile = istestparentfile;
		this.istestparentabstractfile = istestparentabstractfile;
		this.istestparentutilfile = istestparentutilfile;
	
		this.isunit = isunit;
		
		this.mockfiles = mockfiles;
		
		
	}

	public void setmockfiles(HashSet<File >mockfiles) {
		this.mockfiles = mockfiles;
	}
	public HashSet<File> getmockfiles() {
		return this.mockfiles;
	}
	public void setIsunit(Boolean isunit) {
		this.isunit = isunit;
	}
	public Boolean getIsunit() {
		return this.isunit;
	}
	public void settestparentabstractfile(HashSet<File>testparentabstractfile) {
		this.testparentabstractfile = testparentabstractfile;
	}
	public void setistestparentabstractfile(HashSet<File>istestparentabstractfile) {
		this.istestparentabstractfile = istestparentabstractfile;
	}
	public void setistestparentutilfile(HashSet<File>testparentutilfile) {
		this.testparentutilfile = testparentutilfile;
	}
	public void settestparentutilfile(HashSet<File>testparentutilfile) {
		this.testparentutilfile = testparentutilfile;
	}
	public HashSet<File> gettestparentabstractfile() {
		return this.testparentabstractfile;
	}
	public HashSet<File> getistestparentabstractfile() {
		return this.istestparentabstractfile;
	}
	public HashSet<File> gettestparentutilfile() {
		return this.testparentutilfile;
	}
	public HashSet<File> getistestparentutilfile() {
		return this.istestparentutilfile;
	}
	
	public void setdependParentsource(HashSet<File> dependparentsource) {
		this.dependparentsource=dependparentsource;
	}
	public HashSet<File> getdependParentsource() {
		return this.dependparentsource;
	}
	public void setdependParenttest(HashSet<File> dependparenttest) {
		this.dependparenttest=dependparenttest;
	}
	public HashSet<File> getdependParenttest() {
		return this.dependparenttest;
	}
	public void setistestparentfile(HashSet<File> istestparentfile) {
		this.istestparentfile=istestparentfile;
	}
	public HashSet<File> getistestparentfile() {
		return this.istestparentfile;
	}
	
	public void setParentsjavaclass(HashSet<JavaClass> parentsjavaclass) {
		this.parentsjavaclass = parentsjavaclass;
	}
	public HashSet<JavaClass> getParentsjavaclass(){
		return this.parentsjavaclass;
	}
	
	public File getfile() {
		return this.file;
	}
	public void setfile(File file) {
		this.file = file;
	}
	public HashSet<JavaClass> getCalledJavaClass(){
		return calledjavaclass;
	}
	public void setCalledJavaClass(HashSet<JavaClass> calledjavaclass){
		this.calledjavaclass = calledjavaclass;
	}
	public HashSet<File> getistestUtil(){
		return this.istestutil;
	}
	public HashSet<File> gettestUtil(){
		return this.testutil;
	}
	public HashSet<File> getsourceUtil(){
		return this.sourceutil;
	}
	public HashSet<File> getistestAbstract(){
		return this.istestabstract;
	}
	public HashSet<File> gettestAbstract(){
		return this.testabstract;
	}
	public HashSet<File> getsourceAbstract(){
		return this.sourceabstract;
	}
	
	public HashSet<File> getRealTestfile(){
		return this.realtestfile;
	}
	
	
	public HashSet<File> getDirectDependSourceCodefile() {
		return this.testsourcefile;
	}
	
	public HashSet<File> getDirectDependTest_and_UtilityFile(){
		return this.dependtest;
	}
	public void setDirectDependSourceCodefile(HashSet<File>testsourcefile) {
		this.testsourcefile = testsourcefile;
	}
	public void setDirectDependTest_and_UtilityFile(HashSet<File>dependtest) {
		this.dependtest = dependtest;
	}
	
	public void setistestUtil(HashSet<File>istestutil) {
		this.istestutil = istestutil;
	}
	public void settestUtil(HashSet<File>testutil) {
		this.testutil = testutil;
	}
	public void setsourceUtil(HashSet<File>sourceutil) {
		this.sourceutil = sourceutil;
	}
	public void setistestAbstract(HashSet<File>istestabstract) {
		this.istestabstract = istestabstract;
	}
	public void settestAbstract(HashSet<File>testabstract) {
		this.testabstract = testabstract;
	}
	public void setsourceAbstract(HashSet<File>sourceabstract) {
		this.sourceabstract = sourceabstract;
	}
	public void setRealTestfile(HashSet<File>istestfile) {
		this.realtestfile = istestfile;
	}

	public HashSet<JavaClass> getAllextend() {
		return allextend;
	}

	public void setAllextend(HashSet<JavaClass> allextend) {
		this.allextend = allextend;
	}

	public boolean getboolabstract() {
		return boolabstract;
	}

	public void setboolabstract(boolean boolabstract) {
		this.boolabstract = boolabstract;
	}
	
	
		

}
