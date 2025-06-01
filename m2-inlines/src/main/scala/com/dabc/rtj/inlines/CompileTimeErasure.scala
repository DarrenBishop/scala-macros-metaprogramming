package com.dabc.rtj.inlines

object CompileTimeErasure {

  import compiletime.{constValue, erasedValue}
  import compiletime.ops.int.*

  inline def pmOnType[A] =
    inline erasedValue[A] match {
      case _: String => "a string type"
      case _: Int => "an integer type"
      case _: Boolean => "not supported type"
    }


  val messageString = pmOnType[String] // "a string type"
  val messageInt = pmOnType[Int] // "an integer type"
  val messageBoolean = pmOnType[Boolean] // "not supported type"

  // erasedValue[A] is a "fictitious" value, only used for inline expressions
  // we cannot use erasedValue[A] at runtime
  inline def returnToRuntime[A] =
    inline erasedValue[A] match {
      case s: String => s.length
    }

  //val aStringLength = returnToRuntime[String] // fails, because erasedValue cannot be used at runtime

  // example of compile-time calculations
  transparent inline def factorial[N <: Int]: Int =
    inline erasedValue[N] match {
      case _: 0 => 1
      case _: S[n] => constValue[n + 1] * factorial[n]
    }

  val fac: 24 = factorial[4]
}
