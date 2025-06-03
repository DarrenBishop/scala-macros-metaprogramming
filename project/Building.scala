import io.github.davidgregory084 as tpcat
import sbt.Keys.*
import sbt.plugins.JvmPlugin
import sbt.{Def, *}

import scala.Ordering.Implicits.*
import scala.language.implicitConversions

object Building extends AutoPlugin {

  override def requires: Plugins = JvmPlugin && tpcat.TpolecatPlugin

  override def trigger: PluginTrigger = allRequirements

  import tpcat.TpolecatPlugin.autoImport.*

  trait ScalaVersions {
    type ScalaVersion = tpcat.ScalaVersion
    val ScalaVersion: tpcat.ScalaVersion.type = tpcat.ScalaVersion

    val V2_11_0: ScalaVersion = ScalaVersion(2, 11, 0)
    val V2_11_11: ScalaVersion = ScalaVersion(2, 11, 11)
    val V2_12_0: ScalaVersion = ScalaVersion(2, 12, 0)
    val V2_12_2: ScalaVersion = ScalaVersion(2, 12, 2)
    val V2_12_5: ScalaVersion = ScalaVersion(2, 12, 5)
    val V2_13_0: ScalaVersion = ScalaVersion(2, 13, 0)
    val V2_13_2: ScalaVersion = ScalaVersion(2, 13, 2)
    val V2_13_3: ScalaVersion = ScalaVersion(2, 13, 3)
    val V2_13_4: ScalaVersion = ScalaVersion(2, 13, 4)
    val V2_13_5: ScalaVersion = ScalaVersion(2, 13, 5)
    val V2_13_6: ScalaVersion = ScalaVersion(2, 13, 6)
    val V2_13_9: ScalaVersion = ScalaVersion(2, 13, 9)
    val V3_0_0: ScalaVersion = ScalaVersion(3, 0, 0)
    val V3_1_0: ScalaVersion = ScalaVersion(3, 1, 0)
    val V3_5_0: ScalaVersion = ScalaVersion(3, 5, 0)
    val V3_5_1: ScalaVersion = ScalaVersion(3, 5, 1)
    val V3_5_2: ScalaVersion = ScalaVersion(3, 5, 2)
    val V3_6_2: ScalaVersion = ScalaVersion(3, 6, 2)
    val V3_6_3: ScalaVersion = ScalaVersion(3, 6, 3)
    val V3_6_4: ScalaVersion = ScalaVersion(3, 6, 4)
    val V3_7_0: ScalaVersion = ScalaVersion(3, 7, 0)
  }

  object ScalaVersions extends ScalaVersions

  trait AutoImport extends ScalaVersions with VersionSelector.Syntax {

    case object ItProject {
      def unapply(name: String): Boolean = name.endsWith("-it")

      lazy val LogbackFile = "logback-it.xml"
    }

    case object NftProject {
      def unapply(name: String): Boolean = name.endsWith("-nft")

      lazy val LogbackFile = "logback-nft.xml"
    }

    lazy val logbackFile =
      settingKey[String]("Name of the logback configuration file")

    lazy val logbackProperty =
      settingKey[String]("JVM system property overriding the location of the logback configuration file")

    lazy val commonSettings: Seq[Setting[?]] = Seq(
      tpolecatScalacOptions ++= Set(
        ScalacOptions.advancedOption("max-inlines:60")
      ),
      run / fork := true
    )

    lazy val testSettings: Seq[Setting[?]] = inConfig(Test)(
      Seq(
        parallelExecution := false,
        fork := true,
        javaOptions += "-Xmx1g",
        testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oDFGK")
      )
    ) ++ commonSettings

    lazy val compileSettings: Seq[Setting[?]] = Seq(
      tpolecatScalacOptions ++= Set(
        ScalacOptions.other("-explain-cyclic"),
        ScalacOptions.other("-explain"),
        ScalacOptions.privateOption("debug-cyclic"),
        ScalacOptions.other("-no-indent"),
        ScalacOptions.other("-old-syntax"),
        ScalacOptions.warnOption("unused:all"),
        ScalacOptions.warnOption("nonunit-statement")
      )
    ) ++ commonSettings

    lazy val defaultSettings: Seq[Setting[?]] = testSettings ++ compileSettings
  }

  object autoImport extends AutoImport

  import autoImport.*

  object noAutoImport extends DependenciesBase

  import noAutoImport.*

  private def ++ [T](setting: Setting[T]): Setting[T] = setting //Def.derive(setting, default = true)

  def isRootProject = Def.setting {  (ThisBuild / baseDirectory).value == thisProject.value.base }

  override def projectSettings: Seq[Def.Setting[?]] = Compiler.macroParadise_? ++ Compiler.kindProjector_? ++ Seq(
    publish / skip := { isRootProject.value || (publish / skip).value },
    ++ {
      tpolecatScalacOptions := {
        tpolecatScalacOptions.value - ScalacOptions.privateKindProjector ++ Set(
          ScalacOptions.privateOption("kind-projector", _.isBetween(V3_0_0, V3_5_0)),
          ScalacOptions.advancedOption("kind-projector", _ >= V3_5_0),
          ScalacOptions.advancedOption("print:postInlining"),
          ScalacOptions.advancedOption("max-inlines:100000"),
          ScalacOptions.explain,
          ScalacOptions.explainTypes
        )
      }
    },
    ++ {
      tpolecatExcludeOptions ++= Set(
        ScalacOptions.warnUnusedParams,
        ScalacOptions.warnUnusedImplicits,
        ScalacOptions.warnUnusedExplicits,
        ScalacOptions.warnUnusedLocals
      )
    }
  )
}
