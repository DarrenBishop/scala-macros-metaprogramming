package com.dabc.rtj.macros

//import rtj.all.*
import quoted.*

object ValDefs {

  inline def buildValDef =
    ${ buildValDefImpl }

  // val x: Int = "Scala".length
  private def buildValDefImpl(using Quotes): Expr[Int] = {
    import quotes.*
    import reflect.*

    val valSymbol = Symbol.newVal(
      parent = Symbol.spliceOwner, // synthesize new symbols within this parent (in this case, the macro is the owner)
      name = "myValue", // the name of the new val
      tpe = TypeRepr.of[Int], // type representation
      flags = Flags.Lazy, // any flags you wanted (e.g. lazy, inline, private, ...)
      privateWithin = Symbol.noSymbol // None for symbols
    )

    val valBody = {
      // technical detail: the given Quotes should be supplied by the created symbol
      given Quotes = valSymbol.asQuotes

      '{ "Scala".length }
    }

    // val myValue: Int = "Scala".length
    val valDef = ValDef(symbol = valSymbol, rhs = Some(valBody.asTerm))

    // myValue * 4 => refer to the value def

    val valRef = Ref(valSymbol).asExprOf[Int]

    // expression: myValue * 4
    val finalExpr = '{ $valRef * 4 }

    Block(
      stats = List(valDef), // all the definitions of the block (e.g. vals, defs, etc)
      expr = finalExpr.asTerm
    ).asExprOf[Int]
  }
}