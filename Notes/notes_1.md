
# CoffeeMaker JUnit Project Setup Notes

## Project Setup Summary

This file documents the experience and resolution steps followed to configure and test the CoffeeMaker project using Java 8 and Gradle in Visual Studio Code.

---

## Initial Setup

1. **Java Version**  
   - Installed Java 1.8.0_191 located at:
     ```
     C:/Program Files/Java/jdk1.8.0_191
     ```

2. **VS Code Configuration**  
   - Configured `settings.json` with the following:
     ```json
     {
         "java.configuration.updateBuildConfiguration": "interactive",
         "java.jdt.ls.java.home": "C:/Program Files/Java/jdk1.8.0_191",
         "java.configuration.runtimes": [
             {
                 "name": "JavaSE-1.8",
                 "path": "C:/Program Files/Java/jdk1.8.0_191",
                 "default": true
             }
         ]
     }
     ```

---

## Issues Encountered

### Package Errors
- Errors like:
  ```
  package edu.ncsu.csc326.coffeemaker.exceptions does not exist
  ```
  were resolved by verifying folder structure and package declarations.

### Gradle Daemon Failure
- Error:
  ```
  Could not create service of type DaemonContext...
  ```
  was resolved by:
  - Deleting the `.gradle` directory.
  - Forcing Java version using `JAVA_HOME`.
  - Setting environment variable and running with `--no-daemon`.

### JUnit Not Found
- Initially:
  ```
  package org.junit does not exist
  ```
  Fixed by ensuring `junit` dependency exists in `build.gradle`.

---

## Final Problems and Resolution

### Language Support for Java™ by Red Hat
- VS Code Extension expects Java 21+ and does not fully support Java 8.
- Error:
  ```
  The Java runtime set by 'java.jdt.ls.java.home' does not meet the minimum required version of '21'
  ```

---

## Conclusion

Despite persistent warnings from the Red Hat Java extension in VS Code, the project compiles and passes all tests using Java 8 via Gradle.

**✅ We will ignore the visual errors in VS Code since they do not impact the actual build or test execution.**
