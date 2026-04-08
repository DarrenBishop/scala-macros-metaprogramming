package com.dabc.rtj
package typesafejdbc

import cats.syntax.show.*

case class Row(data: Map[String, Any])

object Row {

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
