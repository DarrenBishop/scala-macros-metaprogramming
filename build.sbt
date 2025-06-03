ThisBuild / version := "0.1.0"

ThisBuild / scalaVersion := "3.7.0"

ThisBuild / fork := true

ThisBuild / libraryDependencies ++= Seq(
  Typelevel.cats,
  Typelevel.catsEffect,
  Typelevel.refined,
)

lazy val `scala-macros-metaprogramming` = (project in file("."))
  .aggregate(
    common,
    `m1-warmup`,
    `m2-inlines`,
    `m3-macros`
  )

lazy val common = project

lazy val `m1-warmup` = project
  .dependsOn(common)

lazy val `m2-inlines` = project
  .dependsOn(common)

lazy val `m3-macros` = project
  .dependsOn(common)
