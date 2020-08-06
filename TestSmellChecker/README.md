# Test Smell Checker   


**Introduction**

Test smell checker aims to help developers improving test quality. The tool supports identify tests that containing any of the three types of test smells: 

* Test Smell 1: Duplicate test runs caused by inheritance.
* Test Smell 2: Scattered test fixtures caused by inheritance. 
* Test Smell 3: Using test case inheritance to test source code polymorphism.

-----
**Usage**

1) Input the absolute path of the checking project to variable *projectdirpath* in *src/main/java/main/app.java* . 

2) Build the project with Maven by running command:  

```
mvn clean &&  mvn compile
```

3) Run project with Maven:

```
mvn exec:java -Dexec.mainClass="app.main"  
```
-----
**Output**  

The identified test smells are stored in  `output/testsmell.csv`.  

The output file contains information about:  
* TestSmellCategory 
* TestSmellName	
* TestFileWithSmell 
* FilesRelatedToTestSmells   


