package com.dabc.rtj.macros

import quoted.*

object StructuralTypes {

  class Record(val fields: Map[String, Any]) extends Selectable {
    // any refinement of this type will have access to statically available fields
    // with a method selectDynamic
    def selectDynamic(fieldName: String): Any = fields(fieldName)
  }

  // works for explicitly defined structural types based on Record
  def structuralType(): Unit = {
    type Car = Record { val make: String; val model: String }
    val car: Car = ???
    val carModel = car.model // actually calls Record.selectDynamic("model")
  }

  // but we would like to GENERATE the appropriate structural type based on the fields

  object Record {
    // padawan: return Record(fields.toMap)
    // jedi master: return Record(fields.toMap) { val name: String =  "Daniel"; val age: String =  99; val favLanguage: String = "Scala" }
    transparent inline def make(inline fields: (String, Any)*): Record =
      ${ makeImpl('fields) }

    def makeImpl(fields: Expr[Seq[(String, Any)]])(using Quotes): Expr[Record] = {
      import quotes.reflect.*

      val parentType = TypeRepr.of[Record]
      // refinement with a field "jediLevel": String
      val fieldName: String = "jediLevel"
      val fieldType = TypeRepr.of[String]
      val refinement = Refinement(parentType, fieldName, fieldType) // type representation of `Record (val jediLevel: String }`

      // if you want to add multiple fields, just refine further from the lastest refinement

      def refine[T: Type](parentType: TypeRepr, fieldNameExpr: Expr[String]): TypeRepr = {
        val fieldName = fieldNameExpr.value.getOrElse(badFieldError(fieldNameExpr))
        val fieldType = TypeRepr.of[T]
        Refinement(parentType, fieldName, fieldType)
      }

      val resultType = fields match {
        case Varargs(list) =>
          list.foldLeft(parentType) { (parentType, field) =>
            field match {
              case '{ ($fieldNameExpr: String, $value: a) } =>
                // build a refinement
                refine[a](parentType, fieldNameExpr)
              case '{ ($fieldNameExpr: String) -> ($value: a) } =>
                refine[a](parentType, fieldNameExpr)
            }
          }
        case _ => report.errorAndAbort(s"Cannot build refinement type for $fields")
      }

      // resultType: TypeRepr
      // need the COMPLETE type `Record { val ...; val ... }`
      resultType.asType match {
        case '[type r <: Record; r] =>
          '{ Record($fields.toMap).asInstanceOf[r] }
      }
    }
  }

  def badFieldError(expr: Expr[?])(using q: Quotes) = {
    import q.reflect.*
    report.errorAndAbort(s"Only literal values and compile-time computable expression allowed; got ${expr.show}")
  }
}
