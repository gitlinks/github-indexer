name := "github-indexer"
version := "1.0"
scalaVersion := "2.11.0"
libraryDependencies += "com.typesafe.akka" % "akka-actor_2.11" % "2.4.0"
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.3.4"
libraryDependencies += "org.scalaj" %% "scalaj-http" % "1.1.5"
libraryDependencies += "com.sendgrid" % "sendgrid-java" % "2.2.2"
libraryDependencies += "com.typesafe.akka" % "akka-slf4j_2.11" % "2.3.14"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.3"
libraryDependencies += "org.apache.derby" % "derby" % "10.1.1.0"
libraryDependencies += "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test"

mainClass in Compile := Some("Pooling")
scalaSource in Compile := baseDirectory.value / "src"
enablePlugins(JavaAppPackaging)

resolvers ++= Seq(
  "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/",
  "Atlassian Releases" at "https://maven.atlassian.com/public/",
  "Sbt" at "https://dl.bintray.com/sbt/sbt-plugin-releases/")