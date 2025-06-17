package com.dabc.rtj.macros
package usage

object ReflectionBasicsUsage {
  import ReflectionBasics.*

  case class SimpleWrapper(x: Int) {
    def magicMethod(y: Int) =
      s"This simple wrapper calss a magic method with result ${x + y}"
  }

  val meaningOfLife = 42
  val descriptor = callMethodDynamically(SimpleWrapper(10), meaningOfLife, "magicMethod")
  // && is transformed (at compile-time) to SimpleWrapper(10). magicMethod(meaningOfLife)

  // compile-time error - no method found
  //val descriptorFail = callMethodDynamically(SimpleWrapper(10), meaningOfLife, "someMethod")

  // compile-time error - no method found
  //val truth = true
  //val descriptorFail = callMethodDynamically(SimpleWrapper(10), truth, "magiMethod")

  val tuple3 = createTuple[10, String]("Scala")

  def main(args: Array[String]): Unit = {
    println(tuple3)
  }
}
