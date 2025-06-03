package com.dabc.rtj.macros
package usage

import scala.util.Try

object TypeNameUsage {
  def main(args: Array[String]): Unit = {
    val typename = TypeName[List[Option[Try[String]]]]
    println(typename.value) //  "List[Option[Try[String]]]"
    println(TypeName[Int].value) //  "List[Option[Try[String]]]"
  }
}
