HAVA.JAR
BETA 3
April 3, 2009

Hava uses the JavaCC parser.
To build it, you must first download javacc.jar from

        https://javacc.dev.java.net

Then perform these steps:  

        java -cp javacc.jar jjtree hava.jjt
        java -cp javacc.jar javacc org/hava/parser/hava.jj

Note that the file org/hava/parser/SimpleNode.java is part of the
Hava source, and should not be overwritten by the parser build.
