#Test Smell Check
**Set UP**  
1) Input the absolute path of the checking project to variable *projectdirpath* in *src/main/java/main/app.java* . 

2) Build the project with Maven by running command:  
```
mvn clean &&  mvn compile
```

3) Run project with Maven:

```
mvn exec:java -Dexec.mainClass="app.main"  
```
**Output**  
The identified test smells are stored in: *output/testsmell.csv*.  
The output file contains information about:  
TestSmellCategory |	TestSmellName	| TestFileWithSmell |	FilesRelatedToTestSmells   


