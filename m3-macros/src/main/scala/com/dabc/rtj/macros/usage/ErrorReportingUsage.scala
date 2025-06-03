package com.dabc.rtj.macros
package usage

object ErrorReportingUsage {
  import ErrorReporting.*

  val someIntExpression = funcWithErrors(2 +34) // ok
  //val someIntExpression_v2 = funcWithErrors(-1 -3) // does not compile - expr is negative

  val aVariable = 2 + 87
  //val notCompilableExpression = funcWithErrors(aVariable) // does not compile - expr is a variable i.e. not known at compile-time

  // non-accumulating errors
  //val noAccumError = funcWithErrorsNoAbort(-1)
  //val noAccumError_v2 = funcWithErrorsNoAbort(4)

  //val accumError = errorReport2(-1, 45) // one error for x
  //val accumError_v2 = errorReport2(33, 5) // one error for y
  //val accumError_v3 = errorReport2(-1, 5) // one error for y, one error for y
}
