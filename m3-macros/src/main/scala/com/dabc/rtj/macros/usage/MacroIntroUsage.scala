package com.dabc.rtj.macros
package usage

object MacroIntroUsage {
  import MacroIntro.*

  val firstMacroValue = firstMacro(42, "Scala")
  val secondMacroValue = firstMacro(2 + 3, " RocK") // arguments are computed before the macro expansion

  // the following will not compile...
  val aNumber = 42
  val aString = "Scala"
  //val improperMacroValue = firstMacro(aNumber, aString) // error - the values are not known at compile-time i.e. not constants

  // this will expand the expressions literally in the macro implementation
  val inlineExpandedMacro = firstMacroIA(1 + aNumber / 4, aString.repeat(3)) // actually dos compile; see SBT
}
