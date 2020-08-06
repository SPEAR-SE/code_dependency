# Test Dependency analyzer 


## Introduction

The tool is designed to help analyze dependencies between tests and source code and dependencies between tests in the paper *Revisiting Test Impact Analysis in Continuous Testing From the Perspective of Code Dependencies*. It checks both direct depends and in-direct depends on tests that using Junit (version 4 and above）and TestNG framework. 

-----
## Usage

**Prerequisites**

* JavaParser(3.13.3). The tool reply on javaparser to analyze code structure.    
* Maven(2.0.2). The tool is built and compiled with Maven.     
* Python(version 3)

**Analyze test dependencies**

1) Edit `src/main/java/main/app.java`, input the absolute path of the test project to variable *dir* in *line 20* . e.g., `String dir = "/Users/projects/project_name"`

2) Build the project with Maven by running command:  

```
mvn clean &&  mvn compile
```

3) Run project with Maven:

```
mvn exec:java -Dexec.mainClass="app.main"  
```
-----
**Analyze result**  
The code dependencies analyze results stored in `output/Summary.csv`.   
* Column `TestFileName` repesents the name of test classes.  
* Column `RealTest` represents whether it contains @Test in the file. A file is identified as a test if it contains @Test in it.      
* Column `Sourcecode_DirectDepend` is the number of direct dependencies between tests and codes.  
* Column `RealTest_DirectDependTest` is the number of direct dependencies between a test and other tests.   
* Column `TotalDirectIndirecDepend` is the number of all the indirect dependency of a test.  
  


