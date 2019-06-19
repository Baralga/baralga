[![Codeship Status for Baralga/baralga](https://app.codeship.com/projects/4ed40830-4298-0137-05fa-722e2affcf3a/status?branch=master)](https://app.codeship.com/projects/336356)

Baralga Time Tracker
====================
Baralga is a simple time tracking solution for the desktop.

https://baralga.github.io/

Changelog
====================

Baralga Version 1.8.3-SNAPSHOT
---------------------

Baralga Version 1.8.2
---------------------
### Features
* Report for hours by quarter and filter by quarter.

### Fixes
* Avoid NullPointerException after data import on certain conditions.

### Misc
* Updated opencsv.
* Updated Apache POI.
* Updated commons-lang.
* Updated ical4j.

Baralga Version 1.8.1
---------------------
### Misc
* Updated opencsv.
* Updated Apache POI.
* Updated IZPack Installer.

Baralga Version 1.8.0
---------------------
### Fixes
* Removed platform specific inactivity reminder.
* Run Windows and Mac Installers as Admin to make sure installation in default path is permitted.
* Avoid error log when getting the initial start date.

### Misc
* Updated IZPack Installer to version 5.0.0-rc4.
* Package application as single jar with libraries included.

Baralga Version 1.7.4
---------------------
### Misc
* Updated several libraries.

Baralga Version 1.7.3
---------------------
### Misc 
* Updated website and issues to Github links.
* Updated used libraries.

### Fixes
* Exception occurred when old application directory was found.

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
