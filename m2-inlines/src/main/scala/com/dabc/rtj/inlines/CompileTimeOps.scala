package com.dabc.rtj.inlines

import scala.annotation.nowarn

object CompileTimeOps {

  object Ints {
    import compiletime.ops.int.{ToString as _, *}
    import compiletime.ops.any.*

    val two: 1 + 1 = 2
    val four: 2 * 2 = 4
    val truth: 3 <= 4 = true
    val aString: ToString[2 * 4] = "8"
  }

  object Booleans {
    import compiletime.ops.boolean.*

    val lie: ![true] = false
    val combination: true && false = false
  }

  object Strings {
    import compiletime.ops.string.*

    val aLiteral: "Scala" = "Scala"
    val aLength: Length["Scala"] = 5
    val regexMatching: Matches["Scala", ".*al*"] = true
  }

  // compile time values
  // `constValue`
  @nowarn
  object Values {
    import compiletime.ops.any.ToString
    import compiletime.ops.int.{ToString as _, *}
    import compiletime.ops.string.Length
    import compiletime.{constValue, constValueOpt}

    val five: 5 = constValue[2 + 3] // the type 5 => the VALUE 5
    val five_v2: 5 = constValue[Length["Scala"]] // the type 5 => the VALUE 5

    // anything other than a literal will fail
    //val anInt = constValue[Int] // does not work

    // `constValueOpt` will give you an Option, if you  have a literal=> Some, otherwise None
    val fiveOpt = constValueOpt[2 + 3]
    val fiveNone = constValueOpt[Int]

    inline def customErrorCode[N <: Int] =
      compiletime.error("Error number: "+ constValue[ToString[N]])

    //val customError = customErrorCode[6] // fails, for the right reason and with the right message
  }
}
