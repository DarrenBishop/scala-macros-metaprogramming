package com.dabc.rtj.macros
package usage

object DefDefsUsage {
  import DefDefs.*

  def main(args: Array[String]): Unit = {

    /*
    Synthetic code:
    {
      def myFunction(int: Int, str: String, bool: Boolean): String = {
        if (boolean) int else string.length
      }

      myfunction(theInt, theString, theBoolean)
    }
     */

    //println(generateDynamicFunction(3, "Scala", true))
    //
    //println(generateDynamicFunction(3, "Scala is the best", false))


    /*
    Synthetic code:
    {
      def myFunction[A <: Int](a: A, str: String, bool: Boolean): String = {
        if (boolean) int else string.length
      }

      myfunction(theInt, theString, theBoolean)
    }
     */

    println(generateDynamicFunction(3, "Scala", true))

    println(generateDynamicFunction(3, "Scala is the best", false))

  }
}
