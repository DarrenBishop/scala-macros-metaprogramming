package com.dabc.rtj.macros

import quoted.*
import scala.util.Try

object TypeQuoteMatching {

  inline def matchType[A]: String =
    ${ matchTypeImpl[A] }

  def matchTypeImpl[A: Type](using Quotes): Expr[String] = {
    val typeOfA = Type.of[A]

    val result = typeOfA match {
      case '[Int] => "the int type"
      case '[List[Int]] => "a list of integers"
      case '[List[a]] => s"a list of ${tpe[a]}" // `a` is a type VARIABLE
      case '[Either[a, b]] => s"either with ${tpe[a]} or ${tpe[b]}"
      case '[a => b] => s"a function from ${tpe[a]} to ${tpe[b]}"
      // can have type restrictions
      // available since Scala 3.5
      case '[type a; (`a`, b, `a`)] => s"a tuple with 3 type members, first and thrid are the same: ${tpe[a]}"
      // can have type variable with bounds
      case '[type a <: AnyVal; Try[`a`]] => s"a try of plain value: ${tpe[a]}"
      case _ => "somthing I don't know about"
    }

    Expr(result)
  }
}
