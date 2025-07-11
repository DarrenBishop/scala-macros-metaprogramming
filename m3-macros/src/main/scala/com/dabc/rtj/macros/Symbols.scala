package com.dabc.rtj.macros

import rtj.all.*
import quoted.*

object Symbols {

  inline def describeSymbols[A]: Unit =
    ${ describeSymbolsImpl[A] }

  private def describeSymbolsImpl[A: Type](using q: Quotes): Expr[Unit] = {
    import q.reflect.*

    val typeA = TypeRepr.of[A]
    // symbol = describes the "identifier" of the type representation
    val typeSymbol = typeA.typeSymbol

    val firstMethod = typeSymbol
      .methodMember("changePermissions") //  returns a list, because methods can be overloaded
      .head

    val bitMaskField = typeSymbol.fieldMember("bitMask")

    val descriptions = List(
      typeSymbol.fullName,

      // companion - traits, classes, abstract-classes, opaque-types, enums
      typeSymbol.companionModule,

      firstMethod.paramSymss, // list of all parameter lists, expressed as symbols

      firstMethod.flags.show,

      // check if a symbol is a certain kind of definition
      firstMethod.isDefDef, // check if this def is a method (def)

      // check the position of this def in the source code
      bitMaskField.pos,

      // class hierarchy or inspection of subtypes
      typeSymbol
        .children(0) // can inspect child types
        .primaryConstructor // can check constructors
        .paramSymss,

      typeSymbol
        .children(0)
        .caseFields, // useful for cases of enums, and for case classes

      // can inspect annotations
      typeSymbol
        .getAnnotation(Symbol.classSymbol("scala.annotation.nowarn")),

      // check the "owner" of this symbol
      typeSymbol.owner, // also a symbol

      // ..and you can inspect it further
      typeSymbol.owner.owner
    )

    div()
    descriptions.foreach(println)
    div()

    '{ () }
  }
}
