// The Play plugin
addSbtPlugin("org.playframework" % "sbt-plugin" % "3.0.5")

// Defines scaffolding (found under .g8 folder)
// http://www.foundweekends.org/giter8/scaffolding.html
// sbt "g8Scaffold form"
//addSbtPlugin("org.foundweekends.giter8" % "sbt-giter8-scaffold" % "0.16.2")
addDependencyTreePlugin

// Asset hashing and compressing
addSbtPlugin("com.github.sbt" % "sbt-digest" % "2.1.0")
addSbtPlugin("com.github.sbt" % "sbt-gzip" % "2.0.0")
