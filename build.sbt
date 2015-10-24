name := "github-indexer"
version := "1.0"
scalaVersion := "2.11.0"
libraryDependencies += "com.typesafe.akka" % "akka-actor_2.11" % "2.4.0"
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.3.4"
libraryDependencies +=  "org.scalaj" %% "scalaj-http" % "1.1.5"
resolvers ++= Seq(
  "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases",
  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/",
  "Atlassian Releases" at "https://maven.atlassian.com/public/"
)