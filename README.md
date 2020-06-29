[![Github Actions Status for Baralga/baralga](https://github.com/baralga/baralga/workflows/Build/badge.svg)](https://github.com/Baralga/baralga/actions) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=baralga&metric=alert_status)](https://sonarcloud.io/dashboard?id=baralga) [![Known Vulnerabilities](https://snyk.io/test/github/baralga/baralga/badge.svg)](https://snyk.io/test/github/baralga/baralga) 


Baralga Time Tracker
====================
Baralga is a simple time tracking solution for the desktop.

https://baralga.github.io/

Changelog
====================

Baralga Version 1.8.4-SNAPSHOT
---------------------

### Fixes
* #52 Added russion translation (thanks @mrkaban)
* #49 Fixed iCal export.
* #20 state of project restored on export/import
* Fixed data display after import and project deletion.
* Avoid unnecessary exceptions on startup.
* Dependency updates.
* Updated junit from 4 to 5.
* Build with Maven wrapper.

Baralga Version 1.8.3
---------------------

### Fixes
* Fixed compatibility with older Java versions (<10).

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
* Crash during typing of an description.

Baralga Version 1.7.1
---------------------
### Fixes 
* Report name may not contain character.
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
