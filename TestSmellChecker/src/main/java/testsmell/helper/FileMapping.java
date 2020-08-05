package testsmell.helper;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.github.javaparser.ast.CompilationUnit;


import util.GetClassExtends;
import util.JavaClass;
import util.GetCU;
import visitor.ClassDeclarationVisitor;


public class FileMapping {
	//return one to one mapping: javaclass - the file that contains the javaclass.
	public HashMap<JavaClass,File> getJavaClassFileMapping(List<File> testfiles){		
		HashMap<JavaClass,File> javaclass_file_mapping = new HashMap<JavaClass,File>();
				
		for(File testfile: testfiles) {
			GetCU getcu = new GetCU();
			CompilationUnit cu = getcu.getCu(testfile);
			if(cu == null) {
				continue;
			}

			HashSet<JavaClass> jcs = new HashSet<JavaClass>();			
			ClassDeclarationVisitor cdv = new ClassDeclarationVisitor();
			cdv.visit(cu, jcs);
			
			if(jcs.size() > 0) {
				for(JavaClass javaclass: jcs) {
					javaclass_file_mapping.put(javaclass, testfile);
				}
			}			
		}		
		return javaclass_file_mapping;
	}
	
	
	//return mapping between file and classes it directly inherits from (i.e parents)
	public HashMap<File, HashSet<JavaClass>> getFileExtendMapping(List<File> testfiles) {
		HashMap<File, HashSet<JavaClass>> file_extendsclass_mapping = new HashMap<File, HashSet<JavaClass>>();	
		for(File testfile: testfiles) {
			GetCU getcu = new GetCU();
			CompilationUnit cu = getcu.getCu(testfile);
			if(cu == null) {
				continue;
			}
			GetClassExtends getclassextends = new GetClassExtends();
			HashSet<JavaClass> extendsclasses = getclassextends.getextends(cu);
			if(extendsclasses != null) {
				file_extendsclass_mapping.put(testfile, extendsclasses);
			}
			
		}
		return file_extendsclass_mapping;
	}
	
	// return mapping between file and all classes it inherits from (i.e. its ancestors)
	public HashMap<File, HashSet<JavaClass>> getExtendGraph(List<File> testfiles) {
		HashMap<File, HashSet<JavaClass>> file_all_extendsclass = new HashMap<File, HashSet<JavaClass>>();
		// get the mapping between class and file
		HashMap<JavaClass, File> javaclass_file_mapping = new HashMap<JavaClass, File>();
		javaclass_file_mapping = getJavaClassFileMapping(testfiles);
		
		//parse file by file, record the mapping between test file and extendsclasses;
		HashMap<File, HashSet<JavaClass>> file_extendsclass_mapping = new HashMap<File, HashSet<JavaClass>>();
		file_extendsclass_mapping = getFileExtendMapping(testfiles);
				
		//re-analyze file_extends_mapping to get extend-graph
		Set<File> files_extend = file_extendsclass_mapping.keySet();
		for(File file_ex: files_extend) {
			file_all_extendsclass.put(file_ex, file_extendsclass_mapping.get(file_ex));
			HashSet<JavaClass> extendclasses = file_extendsclass_mapping.get(files_extend);
			Stack<JavaClass> stack = new Stack<JavaClass>();
			if(extendclasses == null) {
				continue;
			}
			for(JavaClass javaclass: extendclasses) {			
				stack.push(javaclass);
			}
			while(stack.size() > 0) {
				JavaClass jc = stack.pop();
				file_all_extendsclass.get(file_ex).add(jc);
				if(files_extend.contains(javaclass_file_mapping.get(jc))) {
					stack.addAll(file_extendsclass_mapping.get(javaclass_file_mapping.get(jc)));
				}
			}						
		}	
		return file_all_extendsclass;
	}
	
	
	//return mapping between file and all classes that inherit it(i.e children). 
	public HashMap<File, HashSet<File>> getDirectExtension(List<File> testfiles) {
		HashMap<File, HashSet<File>> javaclass_children_file = new HashMap<File, HashSet<File>>();
		
		// get the mapping between class and file
		HashMap<JavaClass, File> javaclass_file_mapping = new HashMap<JavaClass, File>();
		javaclass_file_mapping = getJavaClassFileMapping(testfiles);
		
		//parse file by file, record the mapping between test file and extendsclasses;
		HashMap<File, HashSet<JavaClass>> file_extendsclass_mapping = new HashMap<File, HashSet<JavaClass>>();
		file_extendsclass_mapping = getFileExtendMapping(testfiles);
		
		//re-analyze to get the relationship between file and its extension(directly)
		for(File file: file_extendsclass_mapping.keySet()) {
			HashSet<JavaClass> javaclasses = file_extendsclass_mapping.get(file);
			if(javaclasses.size() > 0) {
				for(JavaClass javaclass: javaclasses) {
					File javaclass_file = javaclass_file_mapping.get(javaclass);					
					if(javaclass_file != null) {
						if(javaclass_children_file.keySet().contains(javaclass_file)) {
							javaclass_children_file.get(javaclass_file).add(file);	 
						}	
						else {
							HashSet<File> childrenfiles = new HashSet<File>();
							childrenfiles.add(file);
							javaclass_children_file.put(javaclass_file, childrenfiles);
						}
					}				 				
				}
			}		
			
		}
		
		return javaclass_children_file;				
	}
	
	

	
		
	 

	
}
