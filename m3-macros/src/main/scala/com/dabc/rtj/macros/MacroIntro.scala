package com.dabc.rtj.macros

object MacroIntro {

  object my {
    trait Expr // "code element" = an AST
    case class Num(value: Double) extends Expr
    case class Sum(left: Expr, right: Expr) extends Expr
    case class Div(left: Expr, right: Expr) extends Expr
    // same for all ops
    case class Sin(expr: Expr) extends Expr
  }

  // compiler: code (text) => AST => bytecode => binary
  // metaprogramming = programming with "code elements' as first-class values, i.e. manipulate code at compile-time
  // 2 + 3 / 4 + sin(30) => Sum(Sum(Num(2.0), Div(Num(3.0), Num(4.0))), Sin(Num(30.0)))

  // code => AST (quoting) => new code (splicing) => compile later
  //              | ------------    macro   ----------------|

  import quoted.*

  /*
      A macro is an AST to AST pure function
      Macro Structure
      - inline function with some args
      - args/expressions can be QUOTED => turned into ASTs, as `Expr[A]` for an arg of type `A`
      - those ASTs are manipulated into some other (new) AST, as `Expr[B]` where `B` may or may not be `A`
      - that new AST is injected into the code i.e. SPLICED => a final (new) value expression is returned
   */
  inline def firstMacro(number: Int, string: String): String =
    ${ firstMacroImpl('number, 'string) } // ${ AST } = splicing this AST

  // macro implementation = manipulating ASTs
  // this runs at COMPILE TIME
  def firstMacroImpl(numAST: Expr[Int], stringAST: Expr[String])(using Quotes): Expr[String] = {
    // Expr[A] can be turned into a valie, iff it's known at compile-time
    val numValue = numAST.valueOrAbort
    val stringValue = stringAST.valueOrAbort
    // expressions can be evaluated at compile-time
    // you have access sto the Scala standard library
    val newString =
      if (stringValue.length >10) stringValue.take(numValue)
      else stringValue.repeat(numValue)

    Expr("This macro impl is: " + newString) // can build Exprs manually
  }

  // macro with inline arguments
  inline def firstMacroIA(inline number: Int, inline string: String): String =
    ${ firstMacroIAImpl('number, 'string) } // quoting an inline arg actually expands the  entire expression, it does not compute it

  def firstMacroIAImpl(numAST: Expr[Int], stringAST: Expr[String])(using Quotes): Expr[String] = {
    Expr("The number: " + numAST.show + "; The string: " + stringAST.show)
  }
}
