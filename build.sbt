ThisBuild / version := "0.1.0"

//ThisBuild / scalaVersion := "3.8.3"
ThisBuild / scalaVersion := "3.7.4"

ThisBuild / fork := true

ThisBuild / libraryDependencies ++= Seq(
  Typelevel.cats,
  Typelevel.catsEffect,
  Typelevel.alleycats,
  Typelevel.mouse,
  Typelevel.refined,

  Testing.scalatest
)

lazy val `scala-macros-metaprogramming` = (project in file("."))
  .aggregate(
    common,
    `m1-warmup`,
    `m2-inlines`,
    `m3-macros`,
    `p1-wartimizer`,
    `p2-type-safe-jdbc`
  )

lazy val common = project

lazy val `m1-warmup` = project
  .dependsOn(common)

lazy val `m2-inlines` = project
  .dependsOn(common)

lazy val `m3-macros` = project
  .dependsOn(common)

lazy val `p1-wartimizer` = project
  .dependsOn(common)

lazy val `p2-type-safe-jdbc` = project
  .dependsOn(common)
  .settings(
    libraryDependencies += Data.postgres
  )
