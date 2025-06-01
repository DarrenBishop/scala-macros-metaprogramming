package com.dabc.rtj.inlines

object CompileTimeErrors {

  inline def compileTimeError(x: Int): Int =
    compiletime.error("this should fail to compile")

  // this fails with a custom error
  //val three = compileTimeError(3)

  inline def pmWtiCTError(x: Option[Any]): String = inline x match {
    case Some(v: Int) => v.toString
    case Some(v: String) => v
    case None => "nothing"
    case _ => compiletime.error("this value is not supported; only Option[Int] or Option[String]")
  }

  //val somethingElse = pmWtiCTError(Some(true))

  inline def improperCTError(x: String) =
    compiletime.error(s"error with $x")

  //val improperError = improperCTError("something") // fails - argument to `compiletime.error` must be a constant

  inline def properCTError(x: String) =
    compiletime.error("error with " + x)

  //val properError = properCTError("Failure in all caps") // fails - fo the right reason

  val x = "BIG FAIL" // variable
  //val improperError = properCTError(x) // fails - x is a value to be computed at runtime
}
