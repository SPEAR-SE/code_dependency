package test.selection;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import its.datastructure.JavaClass;

public class AffectInfo {
	private List<JavaClass> affectedclass = new ArrayList<JavaClass>();
	private List<File> affectedfiles = new ArrayList<File>();
	private Boolean affectstatus = false;
	private Boolean affectbyrealtest = false;
	private Boolean affectbytest = false;
	private Boolean affectbysourcecode = false;
	
    public AffectInfo( List<JavaClass> affectedclass, List<File>affectedfiles,Boolean affectstatus,
    		Boolean affectbyrealtest, Boolean affectbytest, Boolean affectbysourcecode) {
		this.affectedclass = affectedclass;
		this.affectedfiles = affectedfiles;
		this.affectstatus = affectstatus;
		this.affectbyrealtest = affectbyrealtest;
		this.affectbytest = affectbytest;
		this.affectbysourcecode = affectbysourcecode;
	}
    
    public Boolean getaffectbytest() {
    	return this.affectbytest;
    }
    public void setaffectbytest(Boolean affectbytest) {
    	this.affectbytest = affectbytest;
    }
    
    public Boolean getaffectbyrealtest() {
    	return this.affectbyrealtest;
    }
    public void setaffectbyrealtest(Boolean affectbyrealtest) {
    	this.affectbyrealtest = affectbyrealtest;
    }
    public Boolean getaffectbysourcecode() {
    	return this.affectbysourcecode;
    }
    public void setaffectbysourcecode(Boolean affectbysourcecode) {
    	this.affectbysourcecode = affectbysourcecode;
    }
    
	public void setAffectedClass( List<JavaClass> affectedclass) {
		this.affectedclass = affectedclass;
	}
	
	public void setAffectedfiles(List<File> affectedfiles) {
		this.affectedfiles = affectedfiles;
	}
	
	public List<JavaClass> getAffectedClass() {
		return this.affectedclass;
	}
	
	public List<File> getAffectedfiles() {
		return this.affectedfiles;
	}
	
	public void setAffected(Boolean affectstatus) {
		this.affectstatus = affectstatus;
	}
	
	public Boolean getAffectedStatus() {
		return this.affectstatus ;
	}

}
