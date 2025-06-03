package com.dabc.rtj.macros

import scala.quoted.*

case class TypeName[A] private (value: String)

/*
  val typename = TypeName[List[Option[Try[String]]]]
  println(typename.value) //  "List[Option[Try[String]]]"
*/
object TypeName {

  def apply[T](using ev: TypeName[T]): TypeName[T] = ev

  // compiler will synthesize new givens on the fly, upon request
  // 1 - macro expansion
  inline given[T]: TypeName[T] = ${ derive[T] }

  // 2 - macro implementation
  /*
      Rules for building Exprs
      - quote `{ ... }: has restrictions on variables & level consistency
      - apply method Expr( ... ): is only available for standard types  e.g. String, Int, etc
   */
  private def derive[T: Type](using Quotes): Expr[TypeName[T]] = '{ TypeName( ${ Expr(Type.show[T]) } ) }
}
