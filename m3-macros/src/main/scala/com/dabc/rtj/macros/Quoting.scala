package com.dabc.rtj.macros

object Quoting {
  import quoted.*

  // part 1 - the macro declaration
  inline def runPlayground(string: String) =
    ${ macroPlayground('string) }

  // part 2- macro implementation
  def macroPlayground(stringExpr: Expr[String])(using Quotes): Expr[String] = {
    // code <--> Expr
    // quoting == code -> Expr
    // splicing == Expr -> code

    // can quote expressions even INSIDE a macro implementation
    val anExpr: Expr[String] = '{ "some constant string" }

    // can quote and splice at multiple levels
    val moreComplexExpr: Expr[String] = '{ "more complex string: " + $stringExpr }

    // exprs are typed
    val lostTypeInfo: Expr[Any] = '{ "Scala" } // not an Expt[String] but something wider
    //val aQuoteExpr = '{ $lostTypeInfo.drop(1) + "!" } // does not compile

    // asExperOf analogous to asInstanceOf

    // for values
    val a: Any = "Scala"
    // a.drop(1) + "!" // does not compile
    val b = a.asInstanceOf[String]
    b.drop(1) + "!"

    // for Exprs
    //... = '{ $lostTypeInfo.drop(1) + "!" } // does not compile
    val recoveredTypeInfo = lostTypeInfo.asExprOf[String]
    val aQuotedExprRecovered = '{ $recoveredTypeInfo.drop(1) + "!" }

    val aSimpleString = "Rock the JVM" // "level 0"

    // level consistency
    val anExprWithSimpleString = '{ // level 1 - quoting starts  a new "level"
      //"The Scala learning platform is " + aSimpleString // illegal
      // ...a variable defined at a level cannot be (provably) expanded at any other level
      "The Scala learning platform is " + ${ Expr(aSimpleString) }
    }

    // the correct way of creating expressions for further quotes
    // 1 - quote that expression
    // 2 - when you start a new level, splice (expand) that quoted expression
    //val aSimpleExpr = '{ "Rock the JVM".repeat(10000) } // Expr
    val aSimpleExpr = Expr(aSimpleString) // Expr
    val anExprWithString = '{ // one level down, I can splice (expand) that Expr
      "The Scala learning platform is " + $aSimpleExpr
    }

    // open a quote -> increase the level
    // splice an expr -> decrease the level
    //val nestedExpr = '{ // level 1
    //  val a = "Scala"
    //  // in order to run a nested quote (level 1), we need to provide a given Quotes in this scope
    //  given q1: Quotes = ??? // impossible to create manually
    //  // ...will be passed by the compiler to methods of the form `myMacro(myArg: MyTpe)(using Quotes)`
    //  '{ // level 2
    //    val b = "is"
    //    '{ // level 3
    //      val c = "great"
    //      '{ // level 4
    //        a + b + c + ${ // back to level 3
    //          aSimpleExpr
    //        }
    //      }
    //    }
    //  }
    //}

    anExprWithString
  }
}
