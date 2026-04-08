package com.dabc.rtj
package typesafejdbc

import quoted.*
import macros.*

trait QueryResultDecoder[A] {
  def decode(row: Row): A
}

object QueryResultDecoder {
  case class ColumnToMapping private[QueryResultDecoder] (
    column: Column,
    mapping: Expr[ColumnMapping[?, ?, ?]]
  )

  private def produceColumnMappingError[T <: JDBC.Type : Type, N <: JDBC.Nullability: Type, C <: String: Type](using Quotes): Nothing = {
    import qr.*

    val tType = TypeRepr.of[T].show
    val nType = TypeRepr.of[N].show
    val cType = TypeRepr.of[C].show

    val cmType = TypeRepr.of[ColumnMapping[T, N, C]].show

    report.errorAndAbort(s"Failed to summon `ColumnMapping[$tType, $nType, $cType]`")
  }

  def toType(tpe: JDBC.Type)(using Quotes): Type[? <: JDBC.Type] = ???
  def toType(tpe: JDBC.Nullability)(using Quotes): Type[? <: JDBC.Nullability] = ???
  def toType(name: String)(using Quotes): Type[? <: String] = ???

  // fetches the correct given `ColumnMapping` for this column
  private def toMapping(column: Column)(using Quotes): ColumnToMapping = {
    import qr.*

    // JDBC.VarChar => JDBC.VarChar.type
    val typeType = toType(column.jdbcType)

    // JDBC.Nullable => JDBC.Nullable.type
    val nullabilityType = toType(column.jdbcNullability)

    // "id" => "id" <: String i.e. singleton-literal type of string "id"
    val nameType = toType(column.name)

    val mapping = (typeType, nullabilityType, nameType) match {
      case (
        '[type t <: JDBC.Type; `t`],
        '[type n <: JDBC.Nullability; `n`],
        '[type c <: String; `c`]
      ) =>
        // fetch a given ColumnMapping[t, n, c]
        Expr.summon[ColumnMapping[t, n, c]].getOrElse { produceColumnMappingError[t, n, c] }
      case _ =>
        report.errorAndAbort(s"Failed to match parameter types in `ColumnMapping[$typeType, $nullabilityType, $nameType]`")
    }

    ColumnToMapping(column, mapping)
  }

  // creates a `QueryResult { /* correct refinements */ }` based on all columns and respective mappings
  private def makeRefinedType(mappings: List[ColumnToMapping])(using Quotes): Type[?] = ???

  // fetches all readers of the correct types, so that column-values can be read into to the correct local types
  private def getColumnReaders(mappings: List[ColumnToMapping])(using Quotes): List[JDBCReader[?]] = ???

  // get the final decoder that can read entire rows into the structural type inferred earlier
  private def makeDecoder(readers: List[JDBCReader[?]], refinedType: Type[?])(using Quotes): Expr[QueryResultDecoder[?]] = ???

  transparent inline def make(query: String): QueryResultDecoder[?] =
    ${ makeImpl('query) }

  private def makeImpl(query: Expr[String])(using Quotes): Expr[QueryResultDecoder[?]] = {
    // 1 - find the schema

    // don't gasp at connecting to the database at compile time
    val schema = Schema(query.valueOrAbort)

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
