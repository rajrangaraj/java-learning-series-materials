# Java Setup and Basic Concepts

This section covers the fundamental setup required for Java development and introduces basic programming concepts.

## Table of Contents
1. [Java Development Kit (JDK) Setup](#jdk-setup)
2. [IDE Installation](#ide-installation)
3. [Basic Concepts](#basic-concepts)
4. [Examples](#examples)
5. [Exercises](#exercises)

## JDK Setup

### Windows
1. Download JDK from [Oracle's website](https://www.oracle.com/java/technologies/downloads/) or use OpenJDK
2. Run the installer
3. Set JAVA_HOME environment variable:
   - Right-click 'This PC' → Properties → Advanced system settings
   - Click 'Environment Variables'
   - Add JAVA_HOME pointing to your JDK installation
4. Add Java to PATH:
   - Add `%JAVA_HOME%\bin` to Path variable

### macOS
1. Install using Homebrew:
```bash
brew install openjdk
```

### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install openjdk-17-jdk
```

### Verify Installation
Open terminal/command prompt and type:
```bash
java --version
javac --version
```

## IDE Installation

### Recommended IDEs
1. **IntelliJ IDEA**
   - Download Community Edition from [JetBrains website](https://www.jetbrains.com/idea/download/)
   - Follow installation wizard
   - First-time setup guide included

2. **VS Code**
   - Download from [VS Code website](https://code.visualstudio.com/)
   - Install Java Extension Pack

3. **Eclipse**
   - Download from [Eclipse website](https://www.eclipse.org/downloads/)
   - Choose "Eclipse IDE for Java Developers"

## Basic Concepts

### 1. Java Program Structure
- Every Java program must have a class
- The class name must match the file name
- The `main` method is the entry point

### 2. Variables and Data Types
- **Primitive Types**
  - byte, short, int, long
  - float, double
  - char
  - boolean
- **Reference Types**
  - String
  - Arrays
  - Objects

### 3. Operators
- Arithmetic (+, -, *, /, %)
- Assignment (=, +=, -=, etc.)
- Comparison (==, !=, >, <, >=, <=)
- Logical (&&, ||, !)

## Examples

This section includes three fundamental examples:

1. **HelloWorld.java**
   - Basic program structure
   - Print statements
   - Comments

2. **Variables.java**
   - Variable declaration
   - Data types
   - Type conversion

3. **BasicOperators.java**
   - Arithmetic operations
   - Comparison operations
   - Logical operations

## Exercises

### Exercise 1: First Program
Create a program that:
- Prints your name
- Prints your age
- Calculates and prints your birth year

### Exercise 2: Basic Calculator
Create a calculator that:
- Takes two numbers as input
- Performs basic arithmetic
- Prints the results

## Common Issues and Solutions

### 1. "java is not recognized"
- Check if JAVA_HOME is set correctly
- Verify Path variable includes Java bin directory
- Restart terminal/command prompt

### 2. "public class must be in its own file"
- Ensure class name matches file name
- Check file extension is .java
- Verify case sensitivity

## Additional Resources
- [Oracle Java Documentation](https://docs.oracle.com/javase/tutorial/)
- [W3Schools Java Tutorial](https://www.w3schools.com/java/)
- [Java Code Conventions](https://www.oracle.com/java/technologies/javase/codeconventions-contents.html)

## Next Steps
After completing this section, proceed to:
1. Complete the exercises
2. Review the example code
3. Move on to Control Structures section

