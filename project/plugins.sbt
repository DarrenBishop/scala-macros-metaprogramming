externalResolvers += Resolver.sbtPluginRepo("releases")

addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat" % "0.4.4")
//addSbtPlugin("org.typelevel" % "sbt-tpolecat" % "0.5.2")
addSbtPlugin("com.github.sbt"    % "sbt-git"              % "2.0.1")
addSbtPlugin("org.scalameta"     % "sbt-mdoc"             % "2.3.7" )
addSbtPlugin("com.eed3si9n"      % "sbt-buildinfo"        % "0.9.0")
addSbtPlugin("com.typesafe.sbt"  % "sbt-native-packager"  % "1.8.0")
addSbtPlugin("com.lightbend.sbt" % "sbt-javaagent"        % "0.1.5")
addSbtPlugin("com.mintbeans"     % "sbt-ecr"              % "0.15.0")
addSbtPlugin("org.scalameta"     % "sbt-scalafmt"         % "2.3.4")
addSbtPlugin("io.kamon"          % "sbt-kanela-runner"    % "2.0.6")
addSbtPlugin("com.github.sbt"    % "sbt-jacoco"           % "3.1.0")
addSbtPlugin("com.github.sbt"    % "sbt-release"          % "1.1.0")
addSbtPlugin("com.eed3si9n"      % "sbt-assembly"         % "0.14.10")

// taken from https://github.com/scala/bug/issues/12632
libraryDependencySchemes ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always
)
