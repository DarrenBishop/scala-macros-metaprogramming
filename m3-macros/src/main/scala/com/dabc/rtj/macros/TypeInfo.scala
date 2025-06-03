package com.dabc.rtj.macros

import quoted.*

object TypeInfo {

  inline def myLittleMacro(x: Int): Int =
    ${ myLittleMacroImpl('x) }

  def myLittleMacroImpl(xExpr: Expr[Int])(using Quotes): Expr[Int] = {
    import quotes.*
    // type instance, synthesized by the compiler in this scope
    // only available inside a macro implementation
    val intType: Type[Int] = Type.of[Int] // instance describing a type
    // allows the macro implementation to make decision

    // this type info is available BEFORE type erasure
    val listIntTypeDescription: String = Type.show[List[Int]]

    Expr(42) // not important
  }
}
