package com.dabc.rtj.macros
package usage

object ShowingTreesUsage {
  import ShowingTrees.*

  debugExpr(List(1,2,3).map(_.toString))
  debugExpr[Either[List[String], Int]](Right(3))
  val x = 42
  debugExpr(s"Scala is cool, meaning of life is $x")
}
