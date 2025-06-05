package com.dabc.rtj.macros
package usage

object BuildingExpressionsUsage {
  import BuildingExpressions.*

  val defaultPermission  = createDefaultPermissions()

  // convert exprs to regular data structures example usage
  val description = describePermission(Permissions.Custom(List("documents", "media")))
}
