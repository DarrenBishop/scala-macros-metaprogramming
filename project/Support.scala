import sbt.*
import sbt.Keys.*
import sbt.plugins.IvyPlugin


object Support extends AutoPlugin {

  override def requires: Plugins = IvyPlugin

  override def trigger: PluginTrigger = allRequirements

  trait Keys {
    lazy val void: SettingKey[Unit] = SettingKey.apply("void")
  }

  trait Versioning {
    type SemVer = VersionNumber
    object SemVer {
      def apply(v: String): SemVer = VersionNumber(v)
      def unapply(v: String): Option[SemVer] = Option(apply(v))
    }
    implicit def toSemVer(v: String): SemVer = SemVer(v)

    object PartialVersion {
      def unapply(v: String): Option[(Long, Long)] = CrossVersion.partialVersion(v)
    }
  }

  trait LocalFunctions {
    def expr[T](key: SettingKey[T])(f: T => Unit) = {
      SettingKey.local[Unit] := f(key.value)
    }
  }

  trait Functions extends Versioning {
    def when[T, U: Manifest](test: SettingKey[T])(predicate: T => Boolean)(values: U*): Def.Initialize[Seq[U]] =
      when(test(predicate))(values*)

    def when[U: Manifest](predicate: Def.Initialize[Boolean])(values: U*): Def.Initialize[Seq[U]] =
      Def.setting[Seq[U]] {
        if (predicate.value) values
        else Nil
      }

    def compilerPluginsIf[T](test: SettingKey[T])(predicate: T => Boolean)(dependencies: ModuleID*): Def.Initialize[Seq[ModuleID]] =
      Def.setting[Seq[ModuleID]] {
        if (predicate(test.value)) dependencies.map(compilerPlugin)
        else Nil
      }

    def compilerPluginIf[T](test: SettingKey[T])(predicate: T => Boolean)(dependency: ModuleID): Def.Initialize[Seq[ModuleID]] =
      compilerPluginsIf(test)(predicate)(dependency)

    def addCompilerPluginIf[T](test: SettingKey[T])(predicate: T => Boolean)(dependency: ModuleID): Setting[Seq[ModuleID]] = {
      libraryDependencies ++= compilerPluginIf(test)(predicate)(dependency).value
    }

    def addCompilerPluginBefore[T](version: SemVer)(dependency: ModuleID): Setting[Seq[ModuleID]] = {
      libraryDependencies ++= compilerPluginIf(scalaVersion)(v => version.matchesSemVer(SemanticSelector(s">$v")))(dependency).value
    }
  }

  object autoImport extends Keys with LocalFunctions with Functions
}
