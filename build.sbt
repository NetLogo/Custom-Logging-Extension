scalaVersion := "2.9.2"

scalaSource in Compile <<= baseDirectory(_ / "src")

scalacOptions ++= Seq("-deprecation", "-unchecked", "-Xfatal-warnings",
                      "-encoding", "us-ascii")

libraryDependencies +=
  "org.nlogo" % "NetLogo" % "5.0.x" from
    "http://ccl.northwestern.edu/devel/NetLogo-signedjars-82673cd.jar"

name := "custom-logging"

NetLogoExtension.settings

NetLogoExtension.classManager := "org.nlogo.extensions.logging.CustomLoggingExtension"

