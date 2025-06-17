package com.dabc.rtj.macros

import  quoted.*

object QuoteMatching {

  inline def pmOptions(inline opt: Option[Int]) =
    ${ pmOptionsImpl('opt) }

  private def pmOptionsImpl(opt: Expr[Option[Int]])(using Quotes): Expr[String] = {
    val result = opt match {
      case '{ Some(42) } => "got the meaning of life"
      case '{ Some($x) } =>
        // x is of type Expr[?]
        s"got a variable: ${x.show}"
      case _ => "got something else"
    }

    Expr(result)
  }

  // we can pattern match on generic types
  inline def pmGeneric[A](inline oa: Option[A]) =
    ${ pmGenericImpl[A]('oa) }

  private def pmGenericImpl[A: Type](optA: Expr[Option[A]])(using Quotes): Expr[String] = {
    val result = optA match {
      case '{ Some($x) } => s"got a variable: ${x.show} of type ${tpe[A]}"
      case _ => "got something else"
    }

    Expr(result)
  }

  /* the following type ascriptions uses rumtime reflection
  val x = Some(42)
  x match {
    case Some(v: String) => ""
    case Some(v: Int) => "..."
    case ...
  }
   */

  inline def pmAny(inline opt: Option[Any]) =
    ${ pmAnyImpl('opt) }

  private def pmAnyImpl(opt: Expr[Option[Any]])(using Quotes): Expr[String] = {
    val result = opt match {
      case '{ Some($x: String) } => s"got a string: ${x.show}"
      case '{ Some($x: Int) } => s"got an int: ${x.show}"
      case '{ Some($x) } => s"got a variable: ${x.show}"
      case _ => "got something else"
    }

    Expr(result)
  }

  inline def pmErasureAvoidance(inline list: List[Any]) =
    ${ pmErasureAvoidanceImpl('list) }

  private def pmErasureAvoidanceImpl(list: Expr[List[Any]])(using Quotes): Expr[String] = {
    val result = list match {
      case '{ $_ : List[Int] } => s"got a list of ints"
      case '{ $_ : List[String] } => s"got a list of strings"
      case '{ $_ : List[t] } => s"a list of elements of type ${ tpe[t] }"
      case '{ List($_) } => s"got a list "
      case _ => "got something else"
    }

    Expr(result)
  }

  // List(1,2,3).map(_.toString).map(_.length)

  inline def pmListExpression(inline list: List[Any]) =
    ${ pmListExpressionImpl('list) }

  private def pmListExpressionImpl(list: Expr[List[Any]])(using Quotes): Expr[String] = {
    val result = list match {
      case '{
        // can declare abstract type variables
        type t1 <: AnyVal // this is a TYPE VARIABLE
        // ...not an abstract type member in an interface/trait
        // ...and can have upper and lower bounds e.g. the `<: AnyVal`
        ($_ : List[`t1`]).map[t2]($f).map[`t1`]($g)
        //             ^^^^                                ^^^^ match EXACTLY that type value
        //             ^^^^ compiler BINDS the type `t1` with whatever the match returned
      } => s"got a chain of list maps between ${tpe[t1]} and ${tpe[t2]}"
      case _ => "got something else"
    }

    Expr(result)
  }
}
