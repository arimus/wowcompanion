WoWCompanion (TM) v0.1.12
Copyright (C) 2004-2005, David Castro
Contact:  David Castro <arimus@users.sourceforge.net>

WoW Companion is a Java client/server used to centrally store character, item
and other data for World of Warcraft. Data is made available via the web in XML
and other formats. Frontends for displaying character profiles also included.

Please see LICENSE in this directory for WoWCompanion licensing information.
See LICENSE_log4j, LICENSE_oro, LICENSE_logging, LICENSE_httpclient,
LICENSE_onejar respectively for Log4j, ORO, Commons Logging, Commons HttpClient
and One-Jar licensing information.  Log4j, ORO, Commons Logging, Commons
HttpClient and One-Jar are bundled with WoWCompanion for convenience.

** NOTE **
This application absolutely will change until there is a stable release!
Relying on it to not change is probably NOT a safe bet.  It is an initial
release, given as a (hopefully) better than nothing option.  The plan is for
this appplication to become much cleaner, more robust, and featureful over
time.  The more you show interest in this application/nudge me, the more
likely that will be the case.  Comments and feedback greatly welcome.

** NOTICE **
The preferred mechanism for keeping WoW Companion up-to-date is using the 
Java Web Start version located at:

  http://apps.wowcompanion.net/wowcompanion/files/launch.jnlp
  
If you have installed a Sun Java 2 JRE 1.4.1 or greater, then Java Web Start 
is already installed.  Other JDKs may or may not have Java Web Start bundled.
If your JRE does not provide Java Web Start functionality, you can install
OpenJNLP (http://openjnlp.nanode.org/).

The releases on SourceForge may lag a bit behind the Java Web Start version
if the developers of WoW Companion are pinched for time.

Requirements to run:
  Java 2 JRE 1.4+
  WoW Companion Server 0.1.12
  World of Warcraft =)

Requirements to build:
  Java 2 SDK 1.4+
  Apache Ant 1.4+
  Jakarta ORO 2.0.x (bundled)
  Log4j 1.2.x (bundled)
  Commons Logging 1.0.4 (bundled)
  Commons HttpClient 2.0.2 (bundled)
  One-Jar 0.95 (bundled)
  JSmooth 0.9.7 (if you to build an .exe)
  World of Warcraft =)

Building:
  To get a jar suitable for use with Java Web Start

	  type 'ant'

	  should have your jar file in ./dist

  To get a jar suitable for standalone distribution

	  type 'ant onejar'

	  should have a jar bundles with supporting libs in ./dist

Testing:
  Log4j setting can be modified in properties/log4j.properties

  Currently there is no formal unit testing.

Notes:
  The WoWCompanion UI was built with the assistance of the Swing Designer
  Eclipse plugin.  Very nice plugin that has some rough edges here and there
  but all-in-all the best tool I have found for building Java UIs.  It made
  my life muuuuuuch easier =)

  http://www.instantiations.com/

Contributions:
  Thanks to the Council of Thoyr (WoW guild on Lightbringer, http://thoyr.net/)
  for the alpha/beta testing that greatly helped iron out many of the early
  kinks.

  To contribute code or other help, send an email to arimus@users.sourceforge.net
  or submit patches/bug reports/etc on the WoWCompanion project page:

      http://sf.net/projects/wowc/

Developers:
  David Castro <arimus@users.sourceforge.net>

Problems/questions/suggestions:
  David Castro <arimus@users.sourceforge.net>

