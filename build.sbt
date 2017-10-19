enablePlugins(org.nlogo.build.NetLogoExtension)

scalaVersion := "2.12.2"

scalaSource in Compile := baseDirectory.value / "src"

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Xfatal-warnings", "-encoding", "us-ascii")

name := "custom-logging"

netLogoVersion := "6.0.2"

netLogoClassManager := "org.nlogo.extensions.logging.CustomLoggingExtension"

