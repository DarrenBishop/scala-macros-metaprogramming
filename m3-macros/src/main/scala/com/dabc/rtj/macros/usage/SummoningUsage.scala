package com.dabc.rtj.macros
package usage

object SummoningUsage {
  import Summoning.*

  given MyTypeClass[String]:
    def message: String = "String descriptor"

  val aTupleDescriptor = describeType[(Int, String, Boolean)]
  //val wrongTupleDescriptor = describeType[(String, Int, Boolean)] // does not  compile - error with my custom message
}
