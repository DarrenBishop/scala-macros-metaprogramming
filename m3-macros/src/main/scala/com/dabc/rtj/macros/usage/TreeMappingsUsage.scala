package com.dabc.rtj.macros
package usage

object TreeMappingsUsage {
  import TreeMappings.*

  val scopedValue = transformCode {
    def multiply(x: String, y: Int) = x * y
    val mol = 42
    val fl = "Scala"

    println(s"The meaning of life is $mol and fav language is $fl")
  }

  val flippedBooleans = flipBooleans {
    val x = true
    val y = false
    val z = x && y
    def funcBool(a: Boolean, b: Boolean) = a && b

    if (z || false) funcBool(x, y)
    else false
  }

  // remove bad patterns /warts in the code
  // List(1,2,3).filter(_ % 2 == 0).size => List(1,2,3).count(_ % 2 == 0)

  val gatheredStatements = demoAccumulator {
    val x = 1 + 3

    println(x) // this should gather the expression x

    val y = {
      println("Hello, I'm writing Scala")
      x * 3
    }

    def logNumbers(a: Int, b: Int) = {
      println(a)
      println(b)
      a + b
    }

    println(logNumbers(x, y))
  }
}
