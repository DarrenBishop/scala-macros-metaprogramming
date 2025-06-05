package com.dabc.rtj.macros
package usage

object ExprListsUsage {
  import ExprLists.*

  val varargsDescriptor = processVarargs(1 * 2 * 3, 3 + 45, 99)

  val listOfExpressions = returnExprs()
}
