ThisBuild / version := "0.1.0"

ThisBuild / scalaVersion := "3.7.0"

//ThisBuild / scalacOptions ++= Seq(
//  //"-encoding", "utf-8", // Specify character encoding used by source files (2 parameters, 1 line)
//  "-feature", // Emit warning and location for usages of features that should be imported explicitly
//  "-deprecation", // Emit warning and location for usages of deprecated APIs
//  "-unchecked", // Enable additional warnings where generated code depends on assumptions
//  //"-explain", // Explain type errors in more detail.
//  //"-explaintypes", // Explain type errors in more detail.
//  "-language:experimental.macros", // Allow macro definition (besides implementation and application)
//  "-language:existentials", // Existential types (besides wildcard types) can be written and inferred
//  "-language:higherKinds", // Allow higher-kinded types
//  "-language:implicitConversions" // Allow definition of implicit functions called views
//)

ThisBuild / fork := true

Compiler.macroParadise_?
Compiler.kindProjector_?

ThisBuild / libraryDependencies ++= Seq(

  Typelevel.cats,
  Typelevel.catsEffect,
  Typelevel.refined,
)

//lazy val scalacOptionsTask = Def.task {
//  CrossVersion.partialVersion(scalaVersion.value) match {
//    case Some((3, 2)) => Seq("-source:3.2", "-java-output-version:8", "-explain")
//    case Some((3, _)) => Seq("-java-output-version:8", "-explain")
//    case Some((2, 12)) => Seq("-Xsource:2.12", "-target:8", "-explaintypes", "-Ypartial-unification", "-Ywarn-macros:after")
//    case Some((2, n)) if n < 12 => Seq("-Xsource:2.12", "-target:8", "-explaintypes", "-Ypartial-unification")
//    case Some((2, _)) => Seq("-Xsource:2.13", "-target:8", "-explaintypes", "-Ymacro-annotations")
//    case _ => Nil
//  }
//}

lazy val `scala-macros-metaprogramming` = (project in file("."))
  .settings(
    publish / skip := true
  )
  .aggregate(
    common,
    `m1-warmup`,
    `m2-inlines`,
    `m3-macros`
  )

lazy val common = project
  .settings(
    tpolecatExcludeOptions ++= Set(
      ScalacOptions.warnUnusedParams,
      ScalacOptions.warnUnusedImplicits
    ),
    tpolecatScalacOptions ++= Set(
      ScalacOptions.warnUnusedExplicits
    )
  )

lazy val `m1-warmup` = project
  .dependsOn(common)

lazy val `m2-inlines` = project
  .dependsOn(common)
  .settings(
    tpolecatScalacOptions ++= Set(
      ScalacOptions.advancedOption("print:postInlining"),
      ScalacOptions.advancedOption("max-inlines:100000")
    )
  )

lazy val `m3-macros` = project
  .dependsOn(common)
  .settings(
    tpolecatScalacOptions ++= Set(
      ScalacOptions.advancedOption("print:postInlining"),
      ScalacOptions.advancedOption("max-inlines:100000")
    )
  )
