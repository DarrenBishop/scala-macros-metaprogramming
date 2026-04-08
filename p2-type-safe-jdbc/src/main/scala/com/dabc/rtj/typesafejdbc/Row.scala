package com.dabc.rtj
package typesafejdbc

import cats.{Foldable, Functor, Show}
import cats.syntax.all.*

case class Row(data: Map[String, Any])

object Row {

  import scala.compiletime.summonFrom

  inline given [T] => Show[T] = summonFrom {
    case ev: Show[T] => ev
    case _ => Show.fromToString
  }

  given [E: Show] => Show[Array[E]] = _.map(_.show).mkString(", ")

  given [E: Show, C[_] : {Functor, Foldable}] => Show[C[E]] = _.map(_.show).intercalate(", ")

  given Show[Row] = { row =>
    StringBuilder().peek { sb =>
        sb.append("Printing Rows:\n")
        row.data.foreach {
          case (k, vs: Array[e]) => sb.append(show"\t$k: $vs\n")
          case (k, vs: List[e]) => sb.append(show"\t$k: $vs\n")
          case (k, v) => sb.append(show"\t$k: $v\n")
        }
      }
      .mkString
  }
}
