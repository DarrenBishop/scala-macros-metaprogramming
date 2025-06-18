package com.dabc.rtj.macros

import quoted.*

object ShowingTrees {

  inline def debugExpr[A](inline value: A)=
    ${ debugExprImpl('value)}

  private def debugExprImpl[A: Type](valueExpr: Expr[A])(using Quotes): Expr[Unit] = {
    import quotes.*
    import reflect.*

    // typed: Expr[A], Type[A]
    // untyped: Term, TypeRepr

    val term = valueExpr.asTerm
    val typeRepr = TypeRepr.of[A]

    // prints will happen at compile-time, before the expr is injected into the code
    println("============================ start debug ============================")
    // with fully qualified class names
    println(term.show)
    // short types
    println(term.show(using Printer.TreeShortCode))
    // print the tree
    println(term.show(using Printer.TreeStructure))


    // print type reprs
    println(typeRepr.show)
    println(typeRepr.show(using Printer.TypeReprShortCode))
    println(typeRepr.show(using Printer.TypeReprStructure))
    println("============================= end debug =============================")

    '{ () }
  }
}
