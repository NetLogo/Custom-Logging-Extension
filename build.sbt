scalaVersion := "2.9.2"

scalaSource in Compile <<= baseDirectory(_ / "src")

scalacOptions ++= Seq("-deprecation", "-unchecked", "-Xfatal-warnings",
                      "-encoding", "us-ascii")

libraryDependencies +=
  "org.nlogo" % "NetLogo" % "5.0.x" from
    "http://ccl.northwestern.edu/devel/NetLogo-signedjars-82673cd.jar"

artifactName := { (_, _, _) => "custom-logging.jar" }

packageOptions := Seq(
  Package.ManifestAttributes(
    ("Extension-Name", "custom-logging"),
    ("Class-Manager", "org.nlogo.extensions.CustomLoggingExtension"),
    ("NetLogo-Extension-API-Version", "5.0")))

packageBin in Compile <<= (packageBin in Compile, baseDirectory, streams) map {
  (jar, base, s) =>
    IO.copyFile(jar, base / "custom-logging.jar")
    Process("pack200 --modification-time=latest --effort=9 --strip-debug " +
            "--no-keep-file-order --unknown-attribute=strip " +
            "custom-logging.jar.pack.gz custom-logging.jar").!!
    if(Process("git diff --quiet --exit-code HEAD").! == 0) {
      Process("git archive -o custom-logging.zip --prefix=custom-logging/ HEAD").!!
      IO.createDirectory(base / "custom-logging")
      IO.copyFile(base / "custom-logging.jar", base / "custom-logging" / "custom-logging.jar")
      IO.copyFile(base / "custom-logging.jar.pack.gz", base / "custom-logging" / "custom-logging.jar.pack.gz")
      Process("zip custom-logging.zip custom-logging/custom-logging.jar custom-logging/custom-logging.jar.pack.gz").!!
      IO.delete(base / "custom-logging")
    }
    else {
      s.log.warn("working tree not clean; no zip archive made")
      IO.delete(base / "custom-logging.zip")
    }
    jar
  }

cleanFiles <++= baseDirectory { base =>
  Seq(base / "custom-logging.jar",
      base / "custom-logging.jar.pack.gz",
      base / "custom-logging.zip") }

