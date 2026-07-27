ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.8.3"

lazy val root = (project in file("."))
  .settings(
    name := "scala3-pekko-project-bank-core",
    idePackagePrefix := Some("com.techmatrix18")
  )
