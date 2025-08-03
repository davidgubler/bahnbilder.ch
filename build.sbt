name := "bahnbilder"
organization := "ch.bahnbilder"
version := "1.0-SNAPSHOT"
maintainer := "david@bahnbilder.ch"

// Don't build documentation to (slightly) increase build speed
Compile/doc/sources := Seq.empty
Compile/packageDoc/publishArtifact := false

lazy val root = (project in file(".")).enablePlugins(PlayJava).enablePlugins(SbtWeb)

scalaVersion := "2.13.15"

libraryDependencies ++= Seq(
  guice,
  "org.mongodb" % "mongodb-driver-sync" % "4.11.2",
  "dev.morphia.morphia" % "morphia-core" % "2.4.14",
  "commons-codec" % "commons-codec" % "1.15",
  "com.drewnoakes" % "metadata-extractor" % "2.19.0",
  "org.mariadb.jdbc" % "mariadb-java-client" % "3.5.1",
  "com.twelvemonkeys.imageio" % "imageio-jpeg" % "3.12.0",
  "com.typesafe.play" %% "play-mailer" % "9.1.0",
  "com.typesafe.play" %% "play-mailer-guice" % "9.1.0"
)

gzip / includeFilter := "*.css" || "*.js"
pipelineStages := Seq(digest, gzip)
