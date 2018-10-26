Parser4Java
===========

Configuration Tab: 
==================
1. System Name: Name of the system (new or update).

2. Repo Systems: Directory where the generated files will be stored for the selected system.

3. Source: Txt file with the source path of the system. You could include more than one source path (one for each line of the txt file).

4. Jar Libraries: Txt file with the jar library path used by the system. 
You could include more than one jar library path (one for each line).
 ---------------------
5. Xml File: Xml File generated by visualvm application for runtime analysis.

Analysis Tab: 
=============
1. System Name: System to be analysed.

2. Type of Representation (Static Analysis): Package calls or method calls.

3. Type of Operation (Static Analysis): Show comparisons or variables or nothing for each class represented in the graph.

4. Self-Node call (Static Analysis): Show calls of the same node.

Compile:
========
mv clean install  

Package:
========

javafxpackager -createjar -appclass br.parser.ui.Parser4Java -srcdir . -outdir out -outfile  Parser4Java.jar -v

-createjar creates a JavaFX JAR executable application.
-appclass br.parser.ui.Parser4Java specifies the fully qualified name of the class containing the main() method.
-srcdir . sets the top-level location of the parent directory holding the compiled classes (current directory).
-outdir out sets the destination where the packaged jar file will be created.
-outfile Parser4Java.jar specifies the name of the executable jar file.
-v allows verbose displays logging information when executing javafxpackager.
