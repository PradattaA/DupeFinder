![](/resources/image/banner.png)

DupeFinder
==========
This a Duplicate File Finder Application. This application searches all drives and directories from the system for duplicate files. User need to select a file and then search. After a complete scan this application will find all duplicated copies of the given file. User can scan entire system or specify limited drives for the scanning process.

## Requirements
- Windows or Linux
- Maven (for building)

## Building DupeFinder
DupeFinder is a standard Maven project. Simply run the following command from the project root directory:

    mvn clean install
On the first build, Maven will download all the dependencies from the internet and cache them in the local repository (~/.m2/repository), which can take a considerable amount of time. Subsequent builds will be faster.

## Running DupeFinder in your IDE

### Overview

After building DupeFinder for the first time, you can load the project into your IDE and run the application. I recommend using [**IntelliJ IDEA**](www.jetbrains.com/idea/), because the GUI is designed using INteliJ GUI BUilder Plugin, which may not work in anyother IDE. As DupeFinder is a standard Maven project, you can import it into your IDE using the root pom.xml file. In **IntelliJ**, choose Open Project from the Quick Start box or choose Open from the File menu and select the root **pom.xml** file.

After opening the project in IntelliJ, double check that the Java SDK is properly configured properly for the project:

Open the File menu and select Project Structure
In the SDKs section, ensure that a 1.7 JDK is selected (create one if none exist)
In the Project section, ensure the Project language level is set to 7.0 as DupeFinder makes use of several Java 7 language features
DupeFinder comes with sample configuration that should work out-of-the-box for development.

### Program Configuration

**Use the following options to create a run configuration:**

- **Main Class:** 			`com.neelhridoy.dupfinder.gui.DupeFinderGui`
- **Working directory:** 	`$MODULE_DIR$`
- **Resource directory:** 	`$MODULE_DIR$/resources/`
- **Source directory:** 	`$MODULE_DIR$/src/`

The working directory should be the DupeFinder subdirectory. In **IntelliJ**, using `$MODULE_DIR$` accomplishes this automatically.

### Library Dependency


![](/resources/image/dependency_diagram.png)


## Screen Shots


![](/resources/image/Screenshot.png)

![](/resources/image/Screenshot1.png)


