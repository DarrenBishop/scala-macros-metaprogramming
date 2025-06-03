package com.dabc.rtj.macros

import quoted.{Quotes, Type}


private[macros] def tpe[T: Type](using Quotes): String = Type.show[T]

transparent inline def quotes(using q: Quotes): q.type = q

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
//transparent inline def reflect(using q: Quotes): q.reflect.type = q.reflect
transparent inline def reflects(using Quotes) = quotes.reflect
