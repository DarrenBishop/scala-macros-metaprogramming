package com.dabc.rtj.macros
package usage

object ValDefsUsage {
  import ValDefs.*

  /*
    synthesized:
    scalaLength = {
      lazy val myValue = "Scala".length // new value definition
      myValue * 4
    }
   */
  val scalaLength = buildValDef
}

