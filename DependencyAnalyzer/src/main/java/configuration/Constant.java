package configuration;

public class Constant {

	public static String exception = "output/Exception";
	public static String classfiledpend = "output/DirectIndirectDependDetail.csv";
	
	public static String methodcallgraph = "output/methodcallgraph";
	public static String classcallgraph = "output/classcallgraph"; 
	public static String parents = "output/parents"; 
	public static String interfaces = "output/interfaces"; 
	
	public static String testcalledmethods = "output/TestCallMethodResult" ;
	public static String testcalledclasses = "output/TestCallClassesResult";
	
	public static String NoNeedToRunIntergrationTest = "output/noNeedToRunIntergrationTest";
	public static String affectedIntegrationTest = "output/affectedIntegrationTest";
	public static String affectedIntegrationTestDetail = "output/affectedIntegrationTestDetail";
	
	public static String pythonscript = "script/fetchgitdiff.py";
	public static String dailypythoncript = "script/fetchgitdiffdaily.py";
	public static String python_daily_recent = "script/git_diff_daily_recent.py";
	public static String alldiffresult = "diff_between_commits.txt";
	public static String difffile = "diff_to_file";
	
	public static String testselectresult = "output/SelectResult.csv";
	
 
	/*
	 * output of test dependencies
	 */
	public static String directdependdetail = "output/DirectDependDetail.csv";
	public static String totaltestdependcy = "output/Summary.csv";
	public static String directdependencydetail = "output/directdependencies.csv";
	
	public static String parentdependdetail = "output/ParentDependDetail.csv";
	
	/*
	 * output of source code and test relationship
	 */
	public static String source_test_relation = "output/source_test_relation.csv";
 
	/* output select result each day*/
	public static String dayselect = "output/select";
}
