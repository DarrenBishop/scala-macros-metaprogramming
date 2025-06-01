import sbt.librarymanagement.SemanticSelector
import simulacrum.{op, typeclass}

@typeclass
trait VersionSelector[V] {
  private def clean(selector: String): String = selector.replaceAll("^[<>=]", "")
  @op("??", true) def matches(v: V, selector: String): Boolean
  @op("<", true) def lessThan(v: V, selector: String): Boolean = matches(v, "<" + clean(selector))
  @op("<=", true) def lessThanOrEqual(v: V, selector: String): Boolean = matches(v, "<=" + clean(selector))
  @op(">", true) def greaterThan(v: V, selector: String): Boolean = matches(v, ">" + clean(selector))
  @op(">=", true) def greaterThanOrEqual(v: V, selector: String): Boolean = matches(v, ">=" + clean(selector))
  @op("===", true) def equals(v: V, selector: String): Boolean = matches(v, "=" + clean(selector))
}

object VersionSelector extends Support.Versioning {

  type Syntax = ToVersionSelectorOps

  implicit val semVerVersionSelector: VersionSelector[SemVer] =
    (sv: SemVer, selector: String) => sv.matchesSemVer(SemanticSelector(selector))
}
