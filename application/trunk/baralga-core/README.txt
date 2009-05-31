== Known Bugs ==
 * Only one activity can be deleted at a time.


== Further Development ==
 * Support for pluggable exporters.


== Baralga Version 1.4.1 ==
Fixes
 * Editing activities after deleting all projects (Issue #30 http://baralga.origo.ethz.ch/node/118)
 * Exit JVM on errors during startup.
 * Default sort order is preserved (Issue #0 http://baralga.origo.ethz.ch/node/120).
 
Functionality
 * Improved displayed texts for undo and redo actions.
 * Improved french translation.


== Baralga Version 1.4 ==
Fixes
 * Export locations remembered correctly.
 * Removed possible problems in concurrent data manipulation.
 * Smarter placement of the 'Start activity?' dialog - especially for those who have their taskbar on the left side of the screen.
 * Error dialog for fatal errors during application startup.
 * Handle errors in settings import gracefully (Issue #22 http://baralga.origo.ethz.ch/node/102)
 * Log files are stored in user specific directory to ensure logging works in multiuser environments.
 * Corrected filter update after changing the day of an activity.
 * Fixed filter list behaviour for outdated filter criteria.
 * Right click on a record in all activities table gives an edit option (Issue #0 http://baralga.origo.ethz.ch/node/106).

Functionality
 * Display start time and running duration of activity.
 * Start time of running activity editable (Issue #11 http://baralga.origo.ethz.ch/node/60).
 * Estonian language support thanks to Kristjan K.
 * Size and location of main window can be remembered and restored (Issue #23 http://baralga.origo.ethz.ch/node/105).
 * Report for hours by month.


== Baralga Version 1.3.5 ==
Fixes
 * Activity editing bugs (Issue #19 http://baralga.origo.ethz.ch/node/94)
 * Midnight bug (Issue #17 http://baralga.origo.ethz.ch/node/87).
 * Start activity when selection changes in systray (Issue #16 http://baralga.origo.ethz.ch/node/85)
 * Remember location of last data export.
 
Functionality
 * Special version for use as portable application.
 * Export to Comma Separated Value (CSV) format.

== Baralga Version 1.3.4 ==
Functionality
 * French language support (thanks to Adrien).


== Baralga Version 1.3.3 ==
Fixes
 * Projects in tray icon sorted alphabetically.
 * Hours by day report fixed.
 * Linux installation now installs an executable jar.


== Baralga Version 1.3.2 ==
Functionality
 * Project title is editable.
 * All activities table is sortable.

Fixes
 * List of projects sorted alphabetically.
 * Project filter now restored correctly.
 * Save data after project or activity has changed.
 

== Baralga Version 1.3.1 ==
Functionality
 * Data backup files can be exported and imported.
 * Filter for week of the year.
 * Smart filters for the current week of the year, current year and the current month.
 * Sorting in tables improved.

Misc
 * Libraries upgraded.
 * Installer without JRE included.
 
Fixes
 * Added title to about dialog.
 * Adding or removing project does not affect the filter by projects.
 * Enabled editing of year in activity.


== Baralga Version 1.3 ==
User Interface
 * Enhanced tooltips.
 * Improved sorting by hours in different tables.
 * Reports belong to categories like General, Time or Project.
 * Smart parsing of time, e.g. 12 -> 12:00 or 12,5 -> 12:30.
 * Combo boxes for filtering by project, month and year are sorted.
 * Reports grouped in categories general, time and project.
 * Look and feel of tray icon improved.

Functionality
 * New report for working hours by project.
 * New chart for working hour distribution by project.
 
Fixes
 * Activities can go on until after midnight. That results in two activity
   entries.


== Baralga Version 1.2.3 ==
 * On exit running activities are stopped. 

User Interface
 * Tooltip with sum of selected activities in all activities table.
 * Icons in tabs of activity reports.
 * URLs in about dialog can be opened in the browser.

Fixes
 * Sorting of accumulated activities by date corrected.


== Baralga Version 1.2.2 ==
 * Improved the editing of dates in the table of all activities.


== Baralga Version 1.2.1 ==
 * Adding activities can be undone.
 * Special characters are handled correctly in Excel Export (Issue #8).
 * Sorting by date fixed in tables for activities by date and all activities.
 * Installer for linux.
 * Baralga works also on platforms which do not support a tray icon (e.g. linux).
 * Dialogs close on pressing Escape.
 * Icon added for deleting activities from all activities list.


== Baralga Version 1.2 ==
Performance and Security
 * Log file for error handling.
 * Create backups of data file.

User Interface
 * Calculate hours by day.
