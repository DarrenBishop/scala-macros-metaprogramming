package com.dabc.rtj.macros

import quoted.{Quotes, Type}


private[macros] def tpe[T: Type](using Quotes): String = Type.show[T]

//export quoted.quotes
//transparent inline def quotes(using q: Quotes): q.type = q

/**
 * [[reflect]] instance of the current [[Quotes]] in scope
 *
 *  Usage:
 *  ```scala
 *  def myExpr[T](using Quotes): Expr[T] = {
 *     import reflect.*
 *     ???
 *  }
 *  ```
 */
transparent inline def reflects(using q: Quotes) = q.reflect
transparent inline def qr(using Quotes) = reflects

transparent inline def reports(using Quotes) = reflects.report
