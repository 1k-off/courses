ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.11.12"

lazy val root = (project in file("."))
  .settings(
    name := "BigDataCourse"
  )
libraryDependencies ++= Seq("org.apache.spark" % "spark-core_2.11" % "2.4.7")
libraryDependencies ++= Seq("org.apache.spark" % "spark-sql_2.11" % "2.4.7")
libraryDependencies ++= Seq("org.apache.spark" %% "spark-mllib" % "2.4.7")
