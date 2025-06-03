package com.dabc.rtj.macros

import quoted.*

object ErrorReporting {
  inline def funcWithErrors(x: Int) =
    ${ funcWithErrorsImpl('x) }

  private def funcWithErrorsImpl(x: Expr[Int])(using Quotes): Expr[Int] = {
    // in order to run error reports, we need to import the quotes' reflect package
    import reflects.*

    if (x.valueOrAbort < 0) {
      // reports a compile-time error + stops the compiler
      report.errorAndAbort(s"${x.show} is negative")
    }

    '{ $x + 3 }
  }

  inline def funcWithErrorsNoAbort(x: Int) =
    ${ funcWithErrorsNoAbortImpl('x) }

  private def funcWithErrorsNoAbortImpl(x: Expr[Int])(using Quotes): Expr[Int] = {
    // in order to run error reports, we need to import the quotes' reflect package
    import reflects.*

    val value = x.valueOrAbort

    // these errors will not be accumulated; the compiler will stop at the first one
    if (value < 0)
      report.error(s"${x.show} is negative")

    if (value < 10)
      report.error(s"${x.show} is not big enough")

    '{ $x + 3 }
  }

  inline def errorReport2(x: Int, y: Int) =
    ${ errorReport2Impl('x, 'y) }

  private def errorReport2Impl(x: Expr[Int], y: Expr[Int])(using Quotes): Expr[Int] = {
    // in order to run error reports, we need to import the quotes' reflect package
    import reflects.*

    val xValue = x.valueOrAbort
    val yValue = y.valueOrAbort

    // these errors will not be accumulated; the compiler will stop at the first one
    if (xValue < 0)
      report.error(s"${x.show} is negative", x)

    if (yValue < 10)
      report.error(s"${y.show} is not big enough", y)

    '{ $x + $y }
  }
}
