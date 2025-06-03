package com.dabc.rtj.macros
package usage

import QuoteMatching.*

object QuoteMatchingUsage {
  val a = pmOptions(Some(42))
  val b = pmOptions(None)
  val c = pmOptions(Option(42)) // Option(42) != Some(42) as an EXPRESSION
  val d = pmOptions(new Some(42)) // new Some(42) != Some(42) as an EXPRESSION
  val e = pmOptions(Some(40 + 2)) // teh expression Some(40 + 2) != Some(42)

  val f = pmGeneric(Some(42))

  val g = pmAny(Some("Scala"))
  val h = pmAny(Some(42))
  val i = pmAny(Some(List(1,2,3)))

  val j = pmErasureAvoidance(List(1,2,3))
  val k= pmErasureAvoidance(List("aaa","bbb","ccc"))
  val l = pmErasureAvoidance(List(true, false, true))
  val m = pmErasureAvoidance(List('a', 'b', 'c'))

  val o = pmListExpression(List('a', 'b', 'c'))
  val p = pmListExpression(List(1,2,3).map(_.toString).map(_.length))
  val q = pmListExpression(List("1","2","3").map(_.toString).map(_.length.toString))
  val r = pmListExpression(List('a', 'b', 'c').map(_.toString).map(_.length.toChar))
  val s = pmListExpression(List(true, false, true).map(_.toString).map(_.length > 0))
}
