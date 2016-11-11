name := """awi"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala, PlayEbean)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaJpa,
  ws,
  "org.postgresql" % "postgresql" % "9.3-1102-jdbc4",
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)
