package com.dabc.rtj.inlines

import scala.compiletime.{summonFrom, summonInline}

object InlineSummoning {

  trait Semigroup[A] {
    def combine(a1: A, a2: A): A
  }

  inline  def double [A](a: A)(using Semigroup[A]): A =
    summon[Semigroup[A]].combine(a, a)

  //val four = double(2) // does not compile

  // `summon` does not work with type class derivation + inlines

  // works
  /*
      Even though we don't have all the info about A,
      `summonInline` defers the act of summoning to the call sire of the function,
        and the A type will be concrete o the compiler, and it will be clear
        whether summoning is possible or not.
   */
  inline def doubleX[A](a: A): A =
    summonInline[Semigroup[A]].combine(a, a)

  given Semigroup[Int] = _ + _
  val four = double(2)
  val four_v2 = doubleX(2)

  // conditional summoning - summonFrom

  trait Messenger[A] {
    def message: String
  }

  given Messenger[Int]:
    def message: String = "this is an Int speaking"

  /*
      With `summonFrom`, we can conditionally produce values at compile time (inlined) depending on the givens the compiler found.
      The pattern match will return the expression for the first matched given found at the call site.
   */
  inline def produceMessage[A] = summonFrom {
    case ev:  Messenger[A] => " Found messenger: " + ev.message
    case _ => "Bummer, no messenger found for this type"
  }

  val intMessage = produceMessage[Int]
  val otherMessage = produceMessage[String]
}
