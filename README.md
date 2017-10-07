# hepek-classycle
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/ba.sake/hepek-classycle/badge.svg)](https://maven-badges.herokuapp.com/maven-central/ba.sake/hepek-classycle)  
*Classycle, ressurected*

This is a fork of popular library called [Classycle](http://classycle.sourceforge.net/index.html).

## What's different?  
- added Java 7 and Java 9 Constant pool  [tags](https://docs.oracle.com/javase/specs/jvms/se9/html/jvms-4.html#jvms-4.4).  
Last official Classycle version (1.4.2) didn't support them.  
You would recognize it by running into an issue like [this one](https://github.com/sake92/sbt-hepek/issues/2).
- refactored to generics, lambdas etc
- formatted code
- removed Ant tasks, you can implement your own if you want... :stuck_out_tongue_winking_eye:

# Usage

## Programmatically
`libraryDependencies ++= Seq("ba.sake" % "hepek-classycle" % "0.0.1")`

## Console
See official [user guide](http://classycle.sourceforge.net/userGuide.html).

# TODO

- improve [MethodHandleConstant](https://github.com/sake92/hepek-classycle/blob/master/src/main/java/classycle/classfile/MethodHandleConstant.java) and InvokeDynamicConstant. Anyone?
- final-ize classes ???
- remove Vector class ???
