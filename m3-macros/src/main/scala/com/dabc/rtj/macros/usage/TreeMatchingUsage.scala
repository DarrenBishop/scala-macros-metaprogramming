package com.dabc.rtj.macros
package usage

object TreeMatchingUsage {
  import TreeMatching.*

  def multiply(x: Int, y: Int): Int = x * y

  demoTreeMatching(multiply(3, 5))
}
