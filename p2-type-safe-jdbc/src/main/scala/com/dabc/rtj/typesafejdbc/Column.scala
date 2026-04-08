package com.dabc.rtj
package typesafejdbc

case class Column(
  index: Int,
  name: String,
  jdbcType: JDBC.Type,
  jdbcNullability: JDBC.Nullability
)

object Column {
  given Show[Column] = { _.fmt(1, 1) }

  extension (c: Column) {
    def fmt(n: Int, t: Int, prefix: String = "", suffix: String = "\n"): String =
      s"${prefix}column: (%d) %-${n}s: %-${t}s [%s]$suffix".format(c.index, c.name, c.jdbcType, c.jdbcNullability)
  }

  import cats.{Functor, Foldable}
  extension [F[_]: {Functor, Foldable}](columns: F[Column]) {

    def fmtF(prefix: String = "", suffix: String = "\n"): String = {
      import cats.syntax.all.*
      val nameWidth = columns.map(_.name.length).maximumOption.head
      val typeWidth = columns.map(_.jdbcType.toString.length).maximumOption.head
      columns
        .foldLeft(StringBuilder()) { (sb, c) => sb.append(c.fmt(nameWidth, typeWidth, prefix, suffix)) }
        .mkString
    }
  }
}
