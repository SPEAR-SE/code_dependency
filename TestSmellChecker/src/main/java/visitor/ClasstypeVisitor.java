package visitor;

 
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import util.IsMock;


public class ClasstypeVisitor extends VoidVisitorAdapter<List<HashSet<ClassOrInterfaceType>>> {
	private HashSet<ClassOrInterfaceType> citype;
	private HashSet<ClassOrInterfaceType> mockclass;

	public HashSet<ClassOrInterfaceType> getcitype() {
		return this.citype;
	}
	public void setcitype(HashSet<ClassOrInterfaceType> citype) {
		this.citype = citype;
	}	
	public HashSet<ClassOrInterfaceType> getmockclass() {
		return this.mockclass;
	}
	public void setmockclass(HashSet<ClassOrInterfaceType> mockclass) {
		this.mockclass = mockclass;
		
	}
	
	public void visit(ClassOrInterfaceType classtype, List<HashSet<ClassOrInterfaceType>> callclassArray) {
		List<String> whitelist = Arrays.asList("String","Arrays","HashMap","Map","HashSet",
				"List","Set","Exception","assert");
		
		HashSet<ClassOrInterfaceType> citype = callclassArray.get(0);
		HashSet<ClassOrInterfaceType> mockclass = callclassArray.get(1);
	
		boolean ismock = false;
		//System.out.println("classtype: "+ classtype);
		
	
		if(!classtype.findAncestor(ClassOrInterfaceType.class).isPresent()) {
			if(!whitelist.contains(classtype.getNameAsString())) {
				IsMock mock = new IsMock();
				ismock = mock.IsmockObject(classtype);
				if(ismock == false) {
					citype.add(classtype);
				}
				else {	
					mockclass.add(classtype);
				}
					
			 }
			}
				
		super.visit(classtype,callclassArray);
		
	}
	
	
 
	
}
