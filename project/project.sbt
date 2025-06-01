import Dependencies.*

//scalacOptions ++= { CrossVersion.partialVersion(scalaVersion.value) match {
//  case Some((3, _))           => Seq("-source:3.2", "-java-output-version:8", "-explain")
//  case Some((2, 12))          => Seq("-Xsource:2.12", "-target:8", "-explaintypes", "-Ypartial-unification", "-Ywarn-macros:after")
//  case Some((2, n)) if n < 12 => Seq("-Xsource:2.12", "-target:8", "-explaintypes", "-Ypartial-unification")
//  case Some((2, _))           => Seq("-Xsource:2.13", "-target:8", "-explaintypes", "-Ymacro-annotations")
//  case _                      => Nil
//}}

libraryDependencies ++= Seq(
  Typelevel.cats,
  Typelevel.refined,
  Typelevel.simulacrum
)

Compiler.macroParadise_?
Compiler.kindProjector_?
