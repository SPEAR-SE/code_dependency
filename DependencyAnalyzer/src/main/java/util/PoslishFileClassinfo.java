package util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import com.github.javaparser.ast.expr.Name;

import its.datastructure.FileClassinfo;
import its.datastructure.JavaClass;

/*
 * import packagename.classname.*
 * import packagename.*
 * Class B{
 *     classA.method(); // classA is in classname
 * }
 * 
 *  in the above example, class A should been involved in call graph.
 */
public class PoslishFileClassinfo {
	public List<FileClassinfo> getImportAllfiles(List<FileClassinfo>allfileinfo){
		List<FileClassinfo> FileContainImportall = new ArrayList<FileClassinfo>();
		for(FileClassinfo fci: allfileinfo) {
			if(fci.getcontainimportall() == true) {
				FileContainImportall.add(fci);
			}
		}
		return FileContainImportall;		
	}
	
	
	/*  ATest.java is a test for A.java, but A.java normally don't need to explicitly import in ATest.java.  So need to add A.java into the test dependency*/
	public void polistTest(List<FileClassinfo>allfileinfo,List<File> projectfiles) {		
		for(FileClassinfo fileclassinfo: allfileinfo) {			 
			if(fileclassinfo.getfilename().getName().contains("Test")) {
				System.out.println("polistTest: "+ fileclassinfo.getfilename());
				HashSet<String> dependency = new HashSet<String>();
				dependency = fileclassinfo.getDependenciesName();	
				Map<JavaClass,HashSet<JavaClass>>interfacee = fileclassinfo.getInterfaces();
				JavaClass rootjavaclass = fileclassinfo.getrootjavaclass();
				if(rootjavaclass==null) {
					continue;
				}
				HashSet<JavaClass> calledjavaclass = new HashSet<JavaClass>();
				calledjavaclass = fileclassinfo.getcalledjavaclass();
				String filename = fileclassinfo.getfilename().getName();
				String possiblefilename = filename.replace("Test", "");
				//System.out.println("Filename "+ fileclassinfo.getfilename().getName()+" contains possible filename "+possiblefilename);
				for(File pfile: projectfiles) {	
					if(pfile.getName().equals(possiblefilename)) {				
						String namewithoutsuffix = possiblefilename.substring(0, possiblefilename.lastIndexOf("."));
						dependency.add(namewithoutsuffix);
						List<FileClassinfo>tmpfileinfo = new ArrayList<FileClassinfo>(allfileinfo);						
						for(FileClassinfo fci: tmpfileinfo) {
							if(fci.getfilename().equals(pfile)) {
								JavaClass newroot = fci.getrootjavaclass();
								if(calledjavaclass==null) {
									calledjavaclass=new HashSet<JavaClass>();
								}
								if(newroot != null) {
								calledjavaclass.add(newroot);
								Set<JavaClass> keys = interfacee.keySet();
								 Boolean haskey = false;
								 if(keys !=null && keys.size()>0) {
								   for(JavaClass key: keys) {
									 if(key.equals(rootjavaclass)) {
										 interfacee.get(rootjavaclass).add(newroot);
										 haskey = true;
										 break;
									  }
								    }
								 }
								 if(haskey==false) {
									 interfacee.put(rootjavaclass, new HashSet<JavaClass>(Arrays.asList(newroot)));
								 }	 
								}
								break;
							}
						}
						 
						break;
					}
				}
			 
				fileclassinfo.setDependenciesName(dependency);
				fileclassinfo.setcalledjavaclass(calledjavaclass);		 
				fileclassinfo.setInterfaceMapForUpdate(interfacee);
				Map<JavaClass,HashSet<JavaClass>>interfaces_include_extends = new HashMap<JavaClass,HashSet<JavaClass>>(interfacee);			 
				fileclassinfo.setInterfaces_includes_extends(interfaces_include_extends);				
			}
		}
	}
	
	
	public void polishFileClassInfo_allimport(List<FileClassinfo>allfileinfo) {
		List<FileClassinfo> ImportallFiles = getImportAllfiles(allfileinfo);
		if(ImportallFiles.size()>0) {
			for(FileClassinfo ImportallFil: ImportallFiles) {
				HashSet<JavaClass> mocks = ImportallFil.getmockclass();		
				File FileContainImportall = ImportallFil.getfilename();			 
				System.out.println("polishing importall file: " + FileContainImportall);
				JavaClass rootjavaclass = new JavaClass("","");
				if(ImportallFil.getSuperClasses() !=null) {
					rootjavaclass = ImportallFil.getSuperClasses().get(FileContainImportall);
				}
				if(rootjavaclass.toString().isEmpty()) {
					continue;
				}
				
				//  find all the JavaClass in interfacee that packagename=""			
				Map<JavaClass,HashSet<JavaClass>> interfacee =ImportallFil.getInterfaces();				 
				HashSet<JavaClass> CalledJavaClass = new HashSet<JavaClass>();
				HashSet<JavaClass> JavaClassWithNoPackagename = new HashSet<JavaClass>();
				if(interfacee!=null) {
				if(interfacee.size() > 0) {
				   CalledJavaClass = interfacee.get(rootjavaclass);				 
				   if(CalledJavaClass.size() > 0) {
					   for(JavaClass jcnop: CalledJavaClass) {
					    	if(jcnop.getPackageName().equals("")) {
					    		boolean ismock = false;
					    		if(mocks != null) {
					    			for(JavaClass tmp: mocks) {
						    			if(tmp.getClassName().equals(jcnop.getClassName())) {
						    				ismock = true;
						    				break;
						    			}
						    		}
					    		}
					    		
					    		if(ismock == false) {
					    			JavaClassWithNoPackagename.add(jcnop);
					    		}
							   
						   }
					   }
				   }
				}
				}
				//System.out.println("File has "+ JavaClassWithNoPackagename.size() + " no packageclass :" + JavaClassWithNoPackagename );
				
				if(JavaClassWithNoPackagename.size()==0) {
					continue;
				}
				
				//get the names in the import* expression
				List<Name> importallnames = ImportallFil.getimportallname();				 
				if(importallnames.isEmpty() == false) {
					//if the name is a classnameA(not packagename), find all the class declared in  classA.
					for(Name na:importallnames) {
						String nameiden= "";
						String namequa = "";	
						if(!na.getIdentifier().isEmpty()) {
							nameiden = na.getIdentifier();
						}
					    						 
						if(na.getQualifier().isPresent()) {
							namequa = na.getQualifier().get().asString();
						}
						 
						 for(FileClassinfo foundfci: allfileinfo) {			
							String filename = foundfci.getfilename().getName().substring(0, foundfci.getfilename().getName().lastIndexOf("."));							  
							if(filename.equals(nameiden)) {
								
								//update dependname
								HashSet<String> dependname = ImportallFil.getDependenciesName();
								dependname.add(nameiden);
								ImportallFil.setDependenciesName(dependname);
								 
								 //find all the classes declared in foundfci
								Boolean found = false;
								HashSet<JavaClass> declaredclasses = new HashSet<JavaClass>();
								declaredclasses = foundfci.getClassDeclaredInFile();
								  
								 if(declaredclasses.size()>0 && JavaClassWithNoPackagename.size()>0) {
									 for(JavaClass jcnp: JavaClassWithNoPackagename) {
									   for(JavaClass declaredclass: declaredclasses) {
											 				 
											 if(declaredclass.getClassName().equals(jcnp.getClassName())) {
												 found = true;												 
												 JavaClass newjavaclass = new JavaClass(declaredclass.getPackageName(),jcnp.getClassName());											 
												// System.out.println("Found! "+ jcnp + " in file " + filename+ " ,Renew to " + newjavaclass);
												 
												 //update interfacee
												 Set<JavaClass>keys = interfacee.keySet();
												 if(keys.size()>0) {
													 for(JavaClass key: keys) {
														 if(interfacee.get(key).contains(jcnp)) {
															 for(JavaClass value:interfacee.get(key)) {
																 if(value.equals(jcnp)) {
																	 interfacee.get(key).remove(jcnp);
																	 interfacee.get(key).add(newjavaclass);
																	 
																	 break;
																 }
															 }
														 }
													 } 
												 }
												 
												 
												/* //update parent
												 Set<JavaClass>parentkeys = parents.keySet();
												 for(JavaClass key: parentkeys) {
													 if(parents.get(key).contains(jcnp)) {
														 for(JavaClass value:parents.get(key)) {
															 if(value.equals(jcnp)) {
																 parents.get(key).remove(jcnp);
																 parents.get(key).add(newjavaclass);
																 break;
															 }
														 }
													 }
												 }*/
												 break;
											 }
										 }					
									 } 
								 }				 										 
								 if(found.equals(false)) { //eg. import Constant.*;
									 JavaClass newjavaclass = new JavaClass(namequa,nameiden);
									 Set<JavaClass> keys = interfacee.keySet();
									 Boolean haskey = false;
									 if(keys.size()>0) {
									   for(JavaClass key: keys) {
										 if(key.equals(rootjavaclass)) {
											 interfacee.get(rootjavaclass).add(newjavaclass);
											 haskey = true;
											 break;
										  }
									    }
									 }
									 if(haskey==false) {
										 interfacee.put(rootjavaclass, new HashSet<JavaClass>(Arrays.asList(newjavaclass)));
									 }			 
								 }
								 ImportallFil.setInterfaceMapForUpdate(interfacee);	 
							 }							 
						 }
						 
								 
					}
				}				
			}
		}	
	}
	
	
	
	public void polishFileClassInfo_extends(List<FileClassinfo>allfileinfo) {
		
		for(FileClassinfo fci: allfileinfo) {			
			if(fci.getExtendsclasses().isEmpty()) {
				continue;
			}
			System.out.println("polishing extends: "+ fci.getfilename() );
			HashSet<JavaClass> extendjavaclasses = fci.getExtendsclasses();
			HashSet<JavaClass> alldirectIndirectextendsclasses = new HashSet<JavaClass>();
			HashSet<String> dependency = new HashSet<String>();
			if(fci.getDependenciesName() != null) {
				dependency.addAll(fci.getDependenciesName());
			}
			HashSet<File> allextendfiles = new HashSet<File>();
			
			Map<JavaClass, HashSet<JavaClass>> interfaces_include_extends = new HashMap<JavaClass, HashSet<JavaClass>>();
			//Map<JavaClass, HashSet<JavaClass>> interfacee = fci.getInterfaces();
			//interfaces_include_extends.putAll(interfacee);
			
			HashSet<JavaClass> calledjavaclass = new HashSet<JavaClass>();
			if(fci.getcalledjavaclass() != null) {
				calledjavaclass.addAll(fci.getcalledjavaclass());
			}
			HashSet<JavaClass>classinfile_include_extends = fci.getclassDeclaredInFile_includeExtends();
			
			if(extendjavaclasses!= null) {
				if(extendjavaclasses.size() > 0) {  //this file has extends
					
					Stack<JavaClass> tmpextendjavaclasses = new Stack<JavaClass>();
					tmpextendjavaclasses.addAll(extendjavaclasses);
					//  update dependency
					while(tmpextendjavaclasses.isEmpty()==false) {
						JavaClass extendclass = tmpextendjavaclasses.pop();
			
						for(FileClassinfo loopfileinfo: allfileinfo) {
							String filename = loopfileinfo.getfilename().getName().substring(0, loopfileinfo.getfilename().getName().lastIndexOf("."));	
							if(filename.equals(extendclass.getClassName())) { // found in loopfileinfo								 
								if(loopfileinfo.getPackage().equals(extendclass.getPackageName())) {									
									dependency.add(filename);
									alldirectIndirectextendsclasses.add(extendclass);
									calledjavaclass.add(extendclass);
									allextendfiles.add(loopfileinfo.getfilename());
																		
									if(loopfileinfo.getExtendsclasses() !=null) {
										tmpextendjavaclasses.addAll(loopfileinfo.getExtendsclasses());
									}
									break;
								}																										
							}							 
						}
						
					}
	
					
					for (JavaClass extendedClass : alldirectIndirectextendsclasses){
						for (FileClassinfo fileInfo: allfileinfo) {
						// if one of its parent is a test, then it is a test too
						// e.g., A contains @Test, and B extends A, then B will also execute the method that contains @Test
						HashSet<JavaClass> decla = fileInfo.getClassDeclaredInFile();
						for(JavaClass javaclassdecla: decla) {
							if (javaclassdecla.equals(extendedClass)) {
								if(fileInfo.getistest()) {
									fci.setistest(true);									
								}
								if(fileInfo.getcalledjavaclass() != null) {
									calledjavaclass.addAll(fileInfo.getcalledjavaclass());
								}
								if (fileInfo.getDependenciesName() != null) {
									dependency.addAll(fileInfo.getDependenciesName());
								}
								if (fileInfo.getClassDeclaredInFile() != null) {
									classinfile_include_extends.addAll(fileInfo.getClassDeclaredInFile());
								}
								
								
								//update interfaces_include_extends, only add fileInfo.getcalledjavaclass() to rootjava map
								// add fci interface with fileinfo interface
								//Map<JavaClass,HashSet<JavaClass>> fciinterface = fci.getInterfaces();
								 
								Set<JavaClass> keysets = interfaces_include_extends.keySet();
								Boolean haskey = false;
								 if(keysets.size()>0) {
								   for(JavaClass key: keysets) {
									 if(key.equals(fci.getrootjavaclass())) {
										 if(fileInfo.getcalledjavaclass() != null && interfaces_include_extends.get(key)!=null) {														
										     interfaces_include_extends.get(fci.getrootjavaclass()).addAll(fileInfo.getcalledjavaclass());
										 }
										 haskey = true;
									     break; 
									 }
									}
									
								}
								 if(haskey==false) {
									interfaces_include_extends.put(fci.getrootjavaclass(),fileInfo.getcalledjavaclass());
								}
																 								
							}
						}							
					}
				}
					
				/*	for(File extendedfile: allextendfiles) {
						for (FileClassinfo fileInfo: allfileinfo) {
							if(fileInfo.getfilename().equals(extendedfile)) {
								if(fileInfo.getistest()) {
									fci.setistest(true);
								}
								calledjavaclass.addAll(fileInfo.getcalledjavaclass());
								classinfile_include_extends.addAll(fileInfo.getClassDeclaredInFile());								
							}
						}
					}*/
				
					//update the number of istestNumber
					// if A extends B, A have all the @test in B
					// e.g., A contains @Test, and B extends A, then B will also execute the method that contains @Test
					for (JavaClass extendedparentClass : extendjavaclasses){
						for (FileClassinfo fileInfo: allfileinfo) {						
						HashSet<JavaClass> decla = fileInfo.getClassDeclaredInFile();
						for(JavaClass javaclassdecla: decla) {
							if (javaclassdecla.equals(extendedparentClass)) {
								if(fileInfo.getistest()) {
									fci.setistestNumber_extend(fci.getRealtestNumber_extend() + fileInfo.getRealtestNumber());
									fci.setbeforeClassNumber_extend(fci.getbeforeClassNumber_extend() + fileInfo.getbeforeClassNumber());
									fci.setafterClassNumber_extend(fci.getafterClassNumber_extend() + fileInfo.getafterClassNumber());
									fci.setafterNumber_extend(fci.getafterNumber_extend()+fileInfo.getafterNumber());
									fci.setbeforeNumber_extend(fci.getbeforeNumber_extend() + fileInfo.getbeforeNumber());
								}
								
							}
						}							
					}
				}
					
					//System.out.println(alldirectIndirectextendsclasses);
				}
			}
			//update all direct-indirect depend extend class if the file contain extend class
			fci.setAllDirectandIndirectExtendsclasses(alldirectIndirectextendsclasses);
			fci.setDependenciesName(dependency);
			fci.setAllextendsfile(allextendfiles);
			//System.out.println("allextendfiles: "+ allextendfiles);
			fci.setcalledjavaclass(calledjavaclass);
			fci.setclassDeclaredInFile_includeExtends(classinfile_include_extends);
			fci.setInterfaces_includes_extends(interfaces_include_extends);
			
		}
		
	}
	
	
	public void givepackagename(List<FileClassinfo>allfileinfo,List<File> projectfiles) {
		for(FileClassinfo fc: allfileinfo) {
			System.out.println("Setpackagename: "+ fc.getfilename());
			JavaClass rootjavaclass = fc.getrootjavaclass();
			Map<JavaClass,HashSet<JavaClass>> interfacee = fc.getInterfaces();
			Map<JavaClass, HashSet<JavaClass>> interfaces_include_extends = fc.getInterfaces_includes_extends();
			Set<JavaClass> keys = interfaces_include_extends.keySet();
			//HashSet<JavaClass> CalledJavaClass = fc.getcalledjavaclass();
			//HashSet<JavaClass> JavaClassWithNoPackagename = new HashSet<JavaClass>();
			if(keys!=null && keys.size() > 0) {			  			 
				for(JavaClass key: keys) {
				 
					HashSet<JavaClass> CalledJavaClass  = null;
					if(interfaces_include_extends.get(key)!=null) {
						CalledJavaClass = new HashSet<JavaClass> (interfaces_include_extends.get(key));
					}
										
					if(CalledJavaClass!=null && CalledJavaClass.size()>0) {
						Iterator<JavaClass> it = CalledJavaClass.iterator();
						while(it.hasNext()) {						 
							JavaClass calledjava = it.next();
							if(calledjava.getPackageName().equals("")) {
								/*
								 * find a class with no packagename. 
								 * if this class equals to a javafile, then give it a packagename.
								 */
								for(File file: projectfiles) {
									if(file.getName().substring(0, file.getName().lastIndexOf(".")).equals(calledjava.toString())){
										JavaClass newjavaclasswithpackage = new JavaClass(fc.getPackage(),calledjava.getClassName());							    		
							    		interfacee.get(key).add(newjavaclasswithpackage);							    	    
							    		interfaces_include_extends.get(key).add(newjavaclasswithpackage); 
							    		fc.getDependenciesName().add(calledjava.toString());
							    		interfacee.get(key).remove(calledjava);
							    		interfaces_include_extends.get(key).remove(calledjava);
									}
								}
					    		 
					    	}
						}
					}		    	
				}
			}
			fc.setInterfaceMapForUpdate(interfacee);
			fc.setInterfaces_includes_extends(interfaces_include_extends);
			fc.setcalledjavaclass(interfaces_include_extends.get(rootjavaclass));
		}
		
	}

}
