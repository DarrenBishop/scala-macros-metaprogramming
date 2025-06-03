import sbt.*
import sbt.plugins.JvmPlugin

object Depending extends AutoPlugin {

  override def requires: Plugins = JvmPlugin

  override def trigger: PluginTrigger = allRequirements

  trait Functions {
    def provide(deps: Seq[ModuleID]): Seq[ModuleID] = deps //map { _ % Provided }
  }

  trait AutoImport extends Functions with DependenciesBase

  object autoImport extends AutoImport
}
