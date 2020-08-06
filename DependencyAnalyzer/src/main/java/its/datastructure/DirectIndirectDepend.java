package its.datastructure;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.github.javaparser.ast.CompilationUnit;

import configuration.Constant;

public class DirectIndirectDepend {
	public DirectIndirectDepend(File testfile, HashSet<File> testalldepend,
			HashSet<File> directIndirectDependSourceCode, HashSet<File> directIndirectDependTest,
			HashSet<File> directIndirectDependTest_Real, HashSet<JavaClass> directIndirectDependClasses,
			HashSet<File> directIndirectDependutility, HashSet<File> parent_directIndirect_sourcecode,
			HashSet<File> parent_directIndirect_utility, HashSet<File> parent_directIndirect_realtest,
			HashSet<File> parent_directIndirect_all) {
		super();
		this.file = testfile;
		this.testalldepend = testalldepend;
		DirectIndirectDependSourceCode = directIndirectDependSourceCode;
		DirectIndirectDependTest = directIndirectDependTest;
		DirectIndirectDependTest_Real = directIndirectDependTest_Real;
		DirectIndirectDependClasses = directIndirectDependClasses;
		DirectIndirectDependutility = directIndirectDependutility;
		this.parent_directIndirect_sourcecode = parent_directIndirect_sourcecode;
		this.parent_directIndirect_utility = parent_directIndirect_utility;
		this.parent_directIndirect_realtest = parent_directIndirect_realtest;
		this.parent_directIndirect_all = parent_directIndirect_all;
	}
	private File file;
	private HashSet<File>testalldepend = new HashSet<File>();
	private HashSet<File> DirectIndirectDependSourceCode = new HashSet<File>();
	private HashSet<File> DirectIndirectDependTest = new HashSet<File>();
	private HashSet<File> DirectIndirectDependTest_Real = new HashSet<File>();
	private HashSet<JavaClass>DirectIndirectDependClasses = new HashSet<JavaClass>();
	//in test dir but not real test
	private HashSet<File> DirectIndirectDependutility = new HashSet<File>();
	
	private HashSet<File> parent_directIndirect_sourcecode =  new HashSet<File>();
	private HashSet<File> parent_directIndirect_utility =  new HashSet<File>();
	private HashSet<File> parent_directIndirect_realtest =  new HashSet<File>();
	private HashSet<File> parent_directIndirect_all =  new HashSet<File>();
	
	public File getFile() {
		return this.file;
	}
	public void setfile(File file) {
		this.file = file;
	}
	public void setDirectIndirectDependClasses(HashSet<JavaClass>DirectIndirectDependClasses) {
		this.DirectIndirectDependClasses = DirectIndirectDependClasses;
	}
	public HashSet<JavaClass> getDirectIndirectDependClasses() {
		return this.DirectIndirectDependClasses;
	}
	
	public void settestalldepend(HashSet<File>testalldepend) {
		this.testalldepend = testalldepend;
	}
	public HashSet<File> gettestalldepend() {
		return this.testalldepend;
	}
	public void setDirectIndirectDependSourceCode(HashSet<File> DirectIndirectDependSourceCode) {
		this.DirectIndirectDependSourceCode = DirectIndirectDependSourceCode;
	}
	public void setDirectIndirectDependTest(HashSet<File> DirectIndirectDependTest) {
		this.DirectIndirectDependTest = DirectIndirectDependTest;
	}
	public void setDirectIndirectDependTest_Real(HashSet<File> DirectIndirectDependTest_Real) {
		this.DirectIndirectDependTest_Real = DirectIndirectDependTest_Real;
	}
	public HashSet<File> getDirectIndirectDependSourceCode(){
		return this.DirectIndirectDependSourceCode;
	}
	public HashSet<File> getDirectIndirectDependTest(){
		return this.DirectIndirectDependTest;
	}
	public HashSet<File> getDirectIndirectDependTest_Real(){
		return this.DirectIndirectDependTest_Real;
	}
	
	
	
	
	
	
	public HashSet<File> getDirectIndirectDependutility() {
		return DirectIndirectDependutility;
	}
	public void setDirectIndirectDependutility(HashSet<File> directIndirectDependutility) {
		DirectIndirectDependutility = directIndirectDependutility;
	}
	public HashSet<File> getParent_directIndirect_sourcecode() {
		return parent_directIndirect_sourcecode;
	}
	public void setParent_directIndirect_sourcecode(HashSet<File> parent_directIndirect_sourcecode) {
		this.parent_directIndirect_sourcecode = parent_directIndirect_sourcecode;
	}
	public HashSet<File> getParent_directIndirect_utility() {
		return parent_directIndirect_utility;
	}
	public void setParent_directIndirect_utility(HashSet<File> parent_directIndirect_utility) {
		this.parent_directIndirect_utility = parent_directIndirect_utility;
	}
	public HashSet<File> getParent_directIndirect_realtest() {
		return parent_directIndirect_realtest;
	}
	public void setParent_directIndirect_realtest(HashSet<File> parent_directIndirect_realtest) {
		this.parent_directIndirect_realtest = parent_directIndirect_realtest;
	}
	public HashSet<File> getParent_directIndirect_all() {
		return parent_directIndirect_all;
	}
	public void setParent_directIndirect_all(HashSet<File> parent_directIndirect_all) {
		this.parent_directIndirect_all = parent_directIndirect_all;
	}



}
