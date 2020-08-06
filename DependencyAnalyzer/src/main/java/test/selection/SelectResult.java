package test.selection;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class SelectResult {
	
	 
	private int totalnumb_realtestfiles_analysis = 0;
	private HashSet<File> AffectedTest = new HashSet<File>();
	private HashSet<File> affectedfiles_because_realtest_changes = new HashSet<File>();
	private HashSet<File> affectedfiles_because_utility_changes = new HashSet<File>();
	private HashSet<File> affectedfiles_because_sourcecode_changes = new HashSet<File>();
	

	private List<HashSet<File>> changedfiles = new ArrayList<HashSet<File>>();
	
	private Map<File,HashSet<File>> selectdetailresult = new HashMap<File,HashSet<File>>();
	
	public Map<File, HashSet<File>> getSelectdetailresult() {
		return selectdetailresult;
	}

	public void setSelectdetailresult(Map<File, HashSet<File>> selectdetailresult) {
		this.selectdetailresult = selectdetailresult;
	}

	public HashSet<File> getAffectedTest() {
		return AffectedTest;
	}
	
	public void setAffectedTest(HashSet<File> affectedTest) {
		AffectedTest = affectedTest;
	}
	
	public HashSet<File> getAffectedfiles_because_realtest_changes() {
		return affectedfiles_because_realtest_changes;
	}
	
	public void setAffectedfiles_because_realtest_changes(HashSet<File> affectedfiles_because_realtest_changes) {
		this.affectedfiles_because_realtest_changes = affectedfiles_because_realtest_changes;
	}
	
	public HashSet<File> getAffectedfiles_because_utility_changes() {
		return affectedfiles_because_utility_changes;
	}
	
	public void setAffectedfiles_because_utility_changes(HashSet<File> affectedfiles_because_utility_changes) {
		this.affectedfiles_because_utility_changes = affectedfiles_because_utility_changes;
	}
	
	public HashSet<File> getAffectedfiles_because_sourcecode_changes() {
		return affectedfiles_because_sourcecode_changes;
	}
	
	public void setAffectedfiles_because_sourcecode_changes(HashSet<File> affectedfiles_because_sourcecode_changes) {
		this.affectedfiles_because_sourcecode_changes = affectedfiles_because_sourcecode_changes;
	}

	public List<HashSet<File>> getChangedfiles() {
		return changedfiles;
	}

	public void setChangedfiles(List<HashSet<File>> changedfiles) {
		this.changedfiles = changedfiles;
	}

	public int getNumb_realtestfiles_analysis() {
		return totalnumb_realtestfiles_analysis;
	}

	public void setNumb_realtestfiles_analysis(int numb_realtestfiles_analysis) {
		this.totalnumb_realtestfiles_analysis = numb_realtestfiles_analysis;
	}

	public SelectResult(int numb_realtestfiles_analysis, HashSet<File> affectedTest,
			HashSet<File> affectedfiles_because_realtest_changes, HashSet<File> affectedfiles_because_utility_changes,
			HashSet<File> affectedfiles_because_sourcecode_changes, List<HashSet<File>> changedfiles,
			Map<File, HashSet<File>> selectdetailresult) {
		this.totalnumb_realtestfiles_analysis = numb_realtestfiles_analysis;
		AffectedTest = affectedTest;
		this.affectedfiles_because_realtest_changes = affectedfiles_because_realtest_changes;
		this.affectedfiles_because_utility_changes = affectedfiles_because_utility_changes;
		this.affectedfiles_because_sourcecode_changes = affectedfiles_because_sourcecode_changes;
		this.changedfiles = changedfiles;
		this.selectdetailresult = selectdetailresult;
	}

	 
}
