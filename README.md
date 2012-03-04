Baralga Time Tracker
====================
Baralga is a simple time tracking solution for the desktop.


Known Bugs
---------------------
* Active missing in import
* DayFilters in several languages


Further Development
---------------------
>   task installer(dependsOn: prepareInstaller) {
>       ant.taskdef(name: 'izpack', classname: 'com.izforge.izpack.ant.IzPackTask', classpath: configurations.izpack.asPath)
>       ant.izpack(input: "[installation XML file]", output:  "${jar.baseName}-${jar.version}-Installer.jar", basedir: "${buildDir}/installer")
>   }


Baralga Version 1.7.2	
---------------------
### Fixes 
* Crash during typing of an description (Issue #100 http://baralga.origo.ethz.ch/issues/100).


Baralga Version 1.7.1
---------------------
### Fixes 
* Report name may not contain character (Issue #99 http://baralga.origo.ethz.ch/issues/99).
* Support for Java 7.

### Misc
* Removed some libraries which are not really necessary.


Baralga Version 1.7
---------------------
### Functionality
* Added data export to xml.
* Added data import from xml.
* Added data export to iCal.
* Restored periodical data backup as xml file.

### Misc
* Improved UI with quick filters.
