package com.dabc.rtj.inlines

object InlineConditions {

  inline def condition(b: Boolean): String = if (b) "yes" else "no"

  val positive = condition(true) // reduces to `if (b) "yes" else "no"`

  inline def condition_v2(b: Boolean): String = inline if (b) "yes" else "no"

  val positive_v2 = condition_v2(true) // known to be "yes" at COMPILE TIME
  /*
      reduces to...

      => first 'round' of inlining
      inline if (true) "yes" else "no

      => second 'round' of inlining
      "yes"
   */

  val positive_v3 = condition_v2(true && !false) // als known to be "yes" at COMPILE TIME

  val variable = true
  //val question = condition_v2(variable) // does not compile because the variable is not known at COMPILE TIME

  transparent inline def conditionUnion(b: Boolean): String | Int = inline if (b) " yes" else 0

    val aString = conditionUnion(true) // known to be String
    val anInt = conditionUnion(true && false) // known to be Int

    // inline matches
    inline def matcher(x: Int) = inline x match {
      case 1 => "one"
      case 2 => "two"
      case 3 => "three"
      //case _ => "nothing"
    }

    val theOne = matcher(1)
    //val nothing = matcher(99) // will not compile unless there is a pattern to match this

    transparent inline def matcher_v2[T](x: Int): String | Int = inline x match {
      case 1 => 1
      case 2 => "two"
      case 3 => "three"
      case _ => 0
    }

    val theOneInt = matcher_v2(1)

    inline def matchOption(x: Option[Any]): String = inline x match {
      case Some(value: String) => value
      case Some(value: Int) => value.toString
      case None => "nothing"
    }

    val something = matchOption(Some("something")) // known to be the value of `Some('something")`
    //val aBoolean = matchOption(Some(true)) // does not compule (exhaustiveness)

    val anOption = Option("my perfect string")
    //val myPerfectString = matchOption(anOption) // won't compile (too general a type)

    // recursion!
    transparent inline def sum(n: Int): Int = inline if (n <= 0) 0 else n + sum(n - 1)

    val ten: 10 = sum(4) // 10
    // recursion has its limits
    //val boom = sum(10000) // this will crash
}
