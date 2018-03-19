name := "HTMLRooms"

version := "0.1"

scalaVersion := "2.12.4"

enablePlugins(ScalaJSPlugin)

// This is an application with a main method
scalaJSUseMainModuleInitializer := true

libraryDependencies ++= Seq(
  "org.scala-js" %%% "scalajs-dom" % "0.9.5"
)

watchSources ++= {
  val htmlFiles = (file("src/main/html") ** "*.*").get
  htmlFiles.map(WatchSource(_))
}

val packageFast = TaskKey[Unit]("packageFast", "Package static HTML site")

packageFast := {

  val log = streams.value.log

  val jsFile = (fastOptJS in Compile).value.data
  val jsFileName = jsFile.getName

  val htmlFileSrc = sourceDirectory.value / "main" / "html" / "index.html"

  val htmlFileDst = target.value / "site" / "index.html"

  log.info(s"Copying $htmlFileSrc to $htmlFileDst")

  IO.copyFile(htmlFileSrc, htmlFileDst, CopyOptions(true, false, false))

  val jsFileDst = target.value / "site" / "js" / jsFileName

  log.info(s"Copying $jsFile to $jsFileDst")

  IO.copyFile(jsFile, jsFileDst, CopyOptions(true, false, false))
}