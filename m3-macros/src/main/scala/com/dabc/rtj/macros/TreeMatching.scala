package com.dabc.rtj.macros

import rtj.all.*
import quoted.*

object TreeMatching {

  inline def demoTreeMatching[A](inline value: A)=
    ${ demoTreeMatchingImpl('value)}

  private def demoTreeMatchingImpl[A: Type](valueExpr: Expr[A])(using Quotes): Expr[Unit] = {
    import quotes.*
    import reflect.*

    val term = valueExpr.asTerm

    div()
    println(term.show(using Printer.TreeStructure))
    div()

    term match {
      case Inlined(_, _, Apply(Ident(funcName), args)) =>
        println(s"Function call with $funcName")
        args.foreach(a => println(a.show))
      case _ =>
    }

    '{ () }
  }
}
