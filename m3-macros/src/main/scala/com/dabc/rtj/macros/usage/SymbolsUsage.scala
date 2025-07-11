package com.dabc.rtj.macros
package usage

import scala.annotation.nowarn

object SymbolsUsage {
  import Symbols.*

  @nowarn
  enum Permissions {
    case Read(bitset: Int, dir: String, mask: Boolean)
    case Denied

    private def changePermissions(b: Int, dir: String): String =
      s"dir $dir just changed permissions to $b"

    val bitMask: Int = 0xFF
  } 

  describeSymbols[Permissions]
}
