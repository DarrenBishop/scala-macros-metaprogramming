package com.dabc.rtj.wartimizer

import scala.quoted.Quotes

/**
 * Wartimization that does not compile any expressions of type
 * ```
 * string + something
 * ```
 * where something is not a `String`
  */
object StringPlusAny extends Wartimization {

  def treeMap(using q: Quotes): q.reflect.TreeMap =  {
    import q.reflect.*

    new TreeMap {
      override def transformTerm(tree: Term)(owner: Symbol): Term = {
        // this term must be an `Expr`
        if (!tree.isExpr)
          super.transformTerm(tree)(owner)
        else tree.asExpr match {
          // this term must be a `String + something`, else
          case '{ ($lhs: String) + ($rhs: t) } if !(TypeRepr.of[t] <:< TypeRepr.of[String]) =>
            val tpe = TypeRepr.of[t]
            val tpeName = tpe.typeSymbol.name
            report.errorAndAbort(s"Adding String to anything other than a String e.g. $tpeName is forbidden", tree.pos)
          case _ =>
            super.transformTerm(tree)(owner)
        }
      }
    }
  }
}
