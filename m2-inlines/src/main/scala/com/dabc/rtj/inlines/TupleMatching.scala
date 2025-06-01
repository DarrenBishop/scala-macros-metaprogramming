package com.dabc.rtj.inlines

import compiletime.summonInline

object TupleMatching {
  trait Show[A] {
    def show(a: A): String
  }

  object Show {
    given Show[String] = identity(_)
    given Show[Int] = _.toString
    given Show[Boolean] = _.toString
  }

  // given any concrete type `T`, we are going to automatically print that kind of value
  inline def showTuple[T <: Tuple](tuple: T): String = {
    inline tuple match {
      case EmptyTuple => ""
      // (1, "a", true) == 1 *: "a" *: true
      case  tup: (h *: t) =>
        val h *: t = tup
        summonInline[Show[h]].show(h) + " " + showTuple(t)
    }
  }

  val aTupleISB = showTuple((1, "a", true)) // compiles, the givens are injected at  COMPILE TIME
  //val thisWontCompile = showTuple(("Scala", List(1), 42)) // does not compile; no Show[List[Int]]

  val aTuple: Tuple = (1, "a", true)
  //val thisWontCompileEither = showTuple(aTuple) // does not compile; type is too general

  def main(args: Array[String]): Unit = {
    println(aTupleISB)
    println(showTuple(("a", true, 1)))
    println(showTuple((true, 1, "a")))
  }
}
