# Code Dependencies in Continuous Testing  
The project includes codes to replicate the paper of *Revisiting Test Impact Analysis in Continuous Testing From the Perspective of Code Dependencies*.
## DependencyAnalyzer
It helps analyze dependencies between tests and source code and dependencies between tests.

## TestSmellChecker
A prototype tool to help developers improving test quality. The tool supports identify tests that containing any of the three types of test smells:

* Test Smell 1: Duplicate test runs caused by inheritance.
* Test Smell 2: Scattered test fixtures caused by inheritance.
* Test Smell 3: Using test case inheritance to test source code polymorphism.