package com.dabc.rtj.macros

import quoted.*

object Summoning {

  trait MyTypeClass[A] {
    def message: String
  }

  inline def describeType[A <: Tuple]: String =
    ${ describeTypeImpl[A] }

  def describeTypeFaultyImpl[A <: Tuple: Type](using Quotes): Expr[String] = {
    Type.of[A] match {
      case '[(_, a, _)] =>
        // summon the MyTypeClass[a]
        // we can't access `given MyTypeClass[a]` because `a` is a type VARIABLE
        //'{ summon[MyTypeClass[a]].message } // this does not work
        Expr("cannot summon instance")
      case _ => Expr("some type I do not know about")
    }
  }

  def describeTypeImpl[A <: Tuple: Type](using Quotes): Expr[String] = {
    import quotes.*
    import reflects.*

    Type.of[A] match {
      case '[(_, a, _)] =>
        val maybeTypeClass = Expr.summon[MyTypeClass[a]] // delay of summoning - returns an Option
        // can perform logic based on whether this given value exists
        val typeClass = maybeTypeClass.getOrElse {
          report.errorAndAbort(s"missing type-class for ${tpe[a]}")
        }
        '{ $typeClass.message }
    }
  }
}
