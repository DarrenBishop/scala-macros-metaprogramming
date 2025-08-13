package com.dabc.rtj.macros

//import rtj.all.*
import quoted.*

object DefDefs {
  
  inline def generateDynamicFunction(anInt: Int, aString: String, aBoolean: Boolean) =
    ${ generateDynamicFunctionImpl('anInt, 'aString, 'aBoolean) }

  private def generateDynamicFunctionImpl(theInt: Expr[Int], theString: Expr[String], theBoolean: Expr[Boolean])(using Quotes) = {
    import quotes.*
    import reflect.*

    // method signature
    val defSymbol = Symbol.newMethod(
      parent = Symbol.spliceOwner, // the scope where you want to add it
      name = "myFunction",
      tpe = MethodType( // type of the method is its entire signature
        paramNames = List("anInt", "aString", "aBoolean"),
      )(
        paramInfosExp = _ => List(TypeRepr.of[Int], TypeRepr.of[String], TypeRepr.of[Boolean]),
        resultTypeExp = _ => TypeRepr.of[Int]
      ),
      flags = Flags.EmptyFlags,
      privateWithin = Symbol.noSymbol
    )

    // method signature #2
    val defSymbol2 = Symbol.newMethod(
      parent = Symbol.spliceOwner, // the scope where you want to add it
      name = "myFunction",
      tpe = PolyType( // type of the polymorphic method
        paramNames = List("A")
      )(
        paramBoundsExp = _ => List(
            TypeBounds.upper(TypeRepr.of[Int]) // type bounds for the first type argument
        ),
        resultTypeExp = typeParams => MethodType( // type of the method is its entire signature
          paramNames = List("a", "aString", "aBoolean")
        )(
          paramInfosExp = _ => List(typeParams.param(0), TypeRepr.of[String], TypeRepr.of[Boolean]),
          resultTypeExp = _ => TypeRepr.of[Int]
        )
      ),
      flags = Flags.EmptyFlags,
      privateWithin = Symbol.noSymbol
    )

    // method body
    def defBody(args: List[List[Tree]]): Option[Term] = Some {
      //given Quotes = defSymbol.asQuotes // needed to make this term "owned" by the definition

      val List(listOfType, List(intTerm, stringTerm, booleanTerm)) = args

      val theInt = intTerm.asExprOf[Int]
      val theString = stringTerm.asExprOf[String]
      val theBoolean = booleanTerm.asExprOf[Boolean]

      '{ if ($theBoolean) $theInt else $theString.length }
        .asTerm
        .changeOwner(defSymbol) // same as with the given Quotes
    }

    // method definition - signature + implementation
    val defDef = DefDef(defSymbol2, defBody)

    // using this method needs to REFER to it
    val defRef = Ref(defSymbol2)

    // invoke the Ref
    val defUsage = defRef
      .appliedToTypes(List(TypeRepr.of[Int]))
      .appliedTo(theInt.asTerm, theString.asTerm, theBoolean.asTerm)

    Block(
      List(defDef),
      defUsage
    ).asExpr
  }
}
