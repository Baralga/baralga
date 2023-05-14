[![Github Actions Status for Baralga/baralga](https://github.com/baralga/baralga/workflows/Build/badge.svg)](https://github.com/Baralga/baralga/actions) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=baralga&metric=alert_status)](https://sonarcloud.io/dashboard?id=baralga) [![Known Vulnerabilities](https://snyk.io/test/github/baralga/baralga/badge.svg)](https://snyk.io/test/github/baralga/baralga) [![Project Dashboard](https://sourcespy.com/shield.svg)](https://sourcespy.com/github/baralgabaralga/)  


Baralga Time Tracker
====================
Simple and lightweight time tracking. 

![Baralga Main Screen](./documents/main-screen.png)

Record the time spent on a project or manually enter and edit activities.

### Features
* plain time tracking no fuzz
* switch projects with tray icon
* record your activities
* report as Excel, CSV and iCalendar
* manually add and edit activities
* enter data fast with shortcuts
* suitable for single users and teams

### Editions
Use [Baralga](https://baralga.github.io/) just for yourself or for your whole team.

#### Single User Mode
As a single user use our great [Desktop application](https://github.com/Baralga/baralga/releases).

#### Multi User Mode
As a team use our great [Desktop application](https://github.com/Baralga/baralga/releases) along with our [team server](https://github.com/Baralga/baralga-app).

# User Guide

### Single User Mode
Single user mode is enabled by default so no extra settings required.

## Multi User Mode
In multi-user mode all data is stored on the Baralga server backend. Multi-user mode is enabled using the Java system property
`-DuserMode=multiuser` or the setting `userMode=multiuser` in the `application.properties`
stored in the directory `$USER_HOME\.baralga\application.properties`.

### Settings in Application Properties

```properties
# Mandatory for multiuser mode
userMode=multiuser

# Default value http://localhost:8080
backendURL=http://localhost:8080

# Default value is current system user
user=user

# Default value us3r
password=us3r
```

# Changelog

see [Changelog](CHANGELOG.md)
