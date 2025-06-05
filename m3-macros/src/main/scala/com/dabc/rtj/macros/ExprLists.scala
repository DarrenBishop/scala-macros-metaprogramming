package com.dabc.rtj.macros

import quoted.*

object ExprLists {
  inline def processVarargs(inline values: Int*) =
    ${ processVarargsImpl('values) }

  private def processVarargsImpl(values: Expr[Seq[Int]])(using Quotes): Expr[String] =
    values match {
      case Varargs(elems) => // Seq[Expr[Int[]
        val nums = elems.map(_.show)
        val numExprs = Expr(nums.toString) // show the list of arg expressions
        '{ "I just received: " + $numExprs }
      case _ => Expr("got something else")
    }

  inline def returnExprs() =
    ${ returnExprsImpl }

  private def returnExprsImpl(using Quotes): Expr[List[String]] = {
    val exprs: List[Expr[String]] = List(
      '{ "Scala" * 2 },
      '{ "macros".toUpperCase() }
    )

    // can turn a List[Expr[String]] => Expr[List[String]]
    Expr.ofList(exprs) // the analogue of `Varags(exprs)`
  }
}
