package com.dabc.rtj.typesafejdbc

import quoted.*

import com.dabc.rtj.*
import com.dabc.rtj.macros.*

trait QueryResultDecoder[A] {
  def decode(row: Row): A
}

object QueryResultDecoder {
  case class ColumnToMapping private[QueryResultDecoder] (
    column: Column,
    mapping: Expr[ColumnMapping[?, ?, ?]],
    types: (name: Type[?], jdbc: Type[?], nullability: Type[?])
  )

  private def produceColumnMappingError[T <: JDBC.Type : Type, N <: JDBC.Nullability: Type, C <: String: Type](using Quotes): Nothing = {
    import quotes.reflect.*

    given Printer[TypeRepr] = Printer.TypeReprShortCode
    val tType = TypeRepr.of[T].show
    val nType = TypeRepr.of[N].show
    val cType = TypeRepr.of[C].show

    report.errorAndAbort(s"Failed to summon `ColumnMapping[$tType, $nType, $cType]`")
  }

  def toType[T <: JDBC.Type: Type](tpe: T)(using Quotes): Type[? <: JDBC.Type] = {
    import qr.*

    tpe match {
      case  JDBC.VarChar => Type.of[JDBC.VarChar]
      case  JDBC.Integer => Type.of[JDBC.Integer]
      case  JDBC.Double => Type.of[JDBC.Double]
      case  JDBC.Float => Type.of[JDBC.Float]
      case  JDBC.Boolean => Type.of[JDBC.Boolean]
      case JDBC.Array(elem) =>
        elem match {
          case  JDBC.VarChar => Type.of[JDBC.Array[JDBC.VarChar]]
          case  JDBC.Integer => Type.of[JDBC.Array[JDBC.Integer]]
          case  JDBC.Double => Type.of[JDBC.Array[JDBC.Double]]
          case  JDBC.Float => Type.of[JDBC.Array[JDBC.Float]]
          case  JDBC.Boolean => Type.of[JDBC.Array[JDBC.Boolean]]
          //case '[type e <: JDBC.Type; type t <: JDBC.Array[e]; `t`] =>
          //  Type.of[JDBC.Array[e]]
          //case '[type t <: JDBC.Array.Array[?, ?]; t] =>
          //  report.errorAndAbort(s"Failed to parse array column-type: $tpe => $elem [${Type.show[t]}]")
          case _ | JDBC.NotSupported =>
            report.errorAndAbort(s"Failed to parse array column-type: $tpe")
        }
      case _ | JDBC.NotSupported =>
        report.errorAndAbort(s"Failed to parse column-type: $tpe")
    }
    //Type.of[T]
  }

  def toType[N <: JDBC.Nullability: Type](tpe: N)(using Quotes): Type[? <: JDBC.Nullability] = {
    tpe match {
      case JDBC.Nullable => Type.of[JDBC.Nullable]
      case JDBC.NonNullable => Type.of[JDBC.NonNullable]
    }
    //Type.of[N]
  }

  def toType[C <: String : Type](name: C)(using Quotes): Type[?] = {
    import quotes.reflect.*
    ConstantType(StringConstant(name)).asType
  }

  def toType2[C <: String : Type](name: C)(using Quotes): Type[? <: String] = {
    Type.of[C] match {
      case '[type c <: String; `c`] => Type.of[c]
    }
  }

  // fetches the correct given `ColumnMapping` for this column
  private[typesafejdbc] def toMapping(column: Column)(using Quotes): ColumnToMapping = {
    //import qr.*

    // JDBC.VarChar => JDBC.VarChar.type
    val typeType = toType(column.jdbcType)

    // JDBC.Nullable => JDBC.Nullable.type
    val nullabilityType = toType(column.jdbcNullability)

    // "id" => "id" <: String i.e. singleton-literal type of string "id"
    val nameType = toType(column.name)

    val types = (typeType, nullabilityType, nameType)

    val mapping = types match {
      case ('[type t <: JDBC.Type; `t`], '[type n <: JDBC.Nullability; `n`], '[type c <: String; `c`]) =>

        // fetch a given ColumnMapping[t, n, c]
        //Printer.TreeShortCode.give {
        //  //report.error(s"Found jdbc type ${Type.show[t]}")
        //  //report.error(s"Found nullability type ${Type.show[n]}")
        //  //report.error(s"Found name type ${Type.show[c]}")
        //}

        Expr.summon[ColumnMapping[t, n, c]].getOrElse { produceColumnMappingError[t, n, c] }
      case _ =>
        reports.errorAndAbort(s"Failed to match parameter types in `ColumnMapping[$typeType, $nullabilityType, $nameType]`")
    }

    ColumnToMapping(column, mapping, types)
  }

  //private def badFieldError(expr: Expr[?])(using Quotes) =
  //  qr.report.errorAndAbort(s"Only literal values and compile-time computable expression allowed; got ${expr.show}")

  // creates a `QueryResult { /* correct refinements */ }` based on all columns and respective mappings
  private[typesafejdbc] def makeRefinedType[T: Type](mappings: List[ColumnToMapping])(using Quotes): Type[?] = {
    //import qr.*
    import quotes.reflect.*

    def refine[F: Type](refinedType: TypeRepr, fieldName: String): TypeRepr = {
      val fieldType = TypeRepr.of[F]
      Refinement(refinedType, fieldName, fieldType)
    }

    val resultType =  mappings.foldLeft(TypeRepr.of[T]) { (refinedType, ctm) =>
      // add a new field to refinedType of the form `val $name: $type`

      val name = ctm.column.name

      val fieldType = ctm.types match {
        case ('[type u; type t <: JDBC.Type { type Underlying = u }; u], '[? <: JDBC.Nullable], _) => TypeRepr.of[Option[u]]
        case ('[type u; type t <: JDBC.Type { type Underlying = u }; u], _, _) => TypeRepr.of[u]
        case _ =>
          report.errorAndAbort("Failed to match column type")
      }

      val fieldType2 = ctm.mapping match {
        case '{ $mapping: ColumnMapping.Aux[_, _, _, colType] } => TypeRepr.of[colType]
      }

      //if (fieldType != fieldType2)
      //  report.errorAndAbort(s"Column-descriptor type is not aligned with column-mapper type: ${fieldType.show} vs ${fieldType2.show}")

      Refinement(refinedType, name, fieldType2)
    }

    resultType.asType
  }

  // fetches all readers of the correct types, so that column-values can be read into to the correct local types
  private[typesafejdbc] def getColumnReaders(mappings: List[ColumnToMapping])(using Quotes): Expr[List[(String, JDBCReader[?])]] = {
    val jdbcReaders = mappings.map { ctm =>
      val nameExpr = Expr(ctm.column.name)
      ctm.mapping.match {
        case '{ $mapping: ColumnMapping.Aux[_, _, _, colType] } =>
          '{ $nameExpr -> $mapping.reader }
      }
    }

    Expr.ofList(jdbcReaders)
  }

  // get the final decoder that can read entire rows into the structural type inferred earlier
  private[typesafejdbc] def makeDecoder(readers: Expr[List[(String, JDBCReader[?])]], refinedType: Type[?])(using Quotes): Expr[QueryResultDecoder[?]] = {
    refinedType match {
      case '[r] =>
        '{
          new QueryResultDecoder[r] {
            def decode(row: Row): r = Query.Result($readers)(row).asInstanceOf[r]
          }
        }
    }
  }

  transparent inline def make(inline query: String): QueryResultDecoder[?] =
    ${ makeImpl('query) }

  private[typesafejdbc] def makeImpl(queryE: Expr[String])(using Quotes): Expr[QueryResultDecoder[?]] = {
    val query = queryE.valueOrAbort

    // 1 - find the schema... don't gasp at connecting to the database at compile time
    val schema = Schema(query)

    // 2 - get all column mappings for all columns in the schema
    val mappings = schema.columns.map(toMapping)

    // 3 - build the correct type-refinement
    /**
     * ```
     *  type RefinedResult = Query.Result {
     *    val id: idMapping.Result
     *    val name: nameMapping.Result
     *    // other columns
     *  }
     * ```
     */
    val refinedType = makeRefinedType(mappings)

    // 4 - get teh column readers
    val columReaders = getColumnReaders(mappings)

    // 5 - get the query decoder
    makeDecoder(columReaders, refinedType)
  }
}
