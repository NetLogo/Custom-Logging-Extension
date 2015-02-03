scalaVersion := "2.9.3"

scalaSource in Compile <<= baseDirectory(_ / "src")

scalacOptions ++= Seq("-deprecation", "-unchecked", "-Xfatal-warnings", "-encoding", "us-ascii")

libraryDependencies += "org.nlogo" % "NetLogo" % "5.x-SNAPSHOT" from "http://ccl.northwestern.edu/devel/NetLogo-dfa6f6e.jar"

name := "custom-logging"

NetLogoExtension.settings

NetLogoExtension.classManager := "org.nlogo.extensions.logging.CustomLoggingExtension"

