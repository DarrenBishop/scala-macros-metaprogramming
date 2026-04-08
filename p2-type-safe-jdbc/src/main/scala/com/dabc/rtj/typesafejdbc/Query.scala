package com.dabc.rtj.typesafejdbc

import quoted.*
import java.sql.{Connection, ResultSet}
import scala.collection.mutable.ListBuffer
import com.dabc.rtj.*

////opaque type Query <: String  = String
//type Query = String

object Query {
  class Result(readers: List[(String, JDBCReader[?])])(row: Row) extends Selectable {
    private val namedReaders = readers.toMap

    def selectDynamic(name: String): Any = {
      // will return the correct value for the column name `name` out fo a JDBC row
      val reader = namedReaders(name, !!?(s"invalid column name `$name`"))
      val value = row.data(name, !!?(s"invalid column name `$name`"))
      reader.read(value)
    }

    override def toString: String = {
      namedReaders.keys
        .foldLeft(StringBuilder()) {
          (sb, name) => selectDynamic(name) match {
            case Some(Array(vs*)) =>
              sb.append(s"\tresult: $name => ${vs.mkString(", ")}\n")
            case Some(v) =>
              sb.append(s"\tresult: $name => $v\n")
            case None =>
              sb
            case v =>
              sb.append(s"\tresult: $name => $v\n")
          }
        }
        .mkString
    }
  }

  object Result {
    given Show[Result] = Show.fromToString
  }

  private def parseRows(schema: Schema, rs: ResultSet): List[Row] = {
    val result = ListBuffer.empty[Row]

    while (rs.next()) {
      // at this point the result-set is "looking at" a row
      val row = schema.columns.map { column =>
        // take out the value at column.index
        val value = rs.getObject(column.index) match {
          case array: java.sql.Array => array.getArray
          case obj => obj
        }
        column.name -> value
      }
      result += Row(row.toMap)
    }

    result.toList
  }

  def getRows(query: String): List[Row] = JDBCCommunication.withConnection { conn =>
    given Connection = conn
    val statement = conn.createStatement()
    val results = statement.executeQuery(query)
    val metadata = results.getMetaData
    val schema = Schema(metadata)

    parseRows(schema, results)
  }

  transparent inline def run(inline query: String): List[?] =
    ${ runImpl('query) }

  //private def runImpl(queryE: Expr[String])(using Quotes): Expr[List[?]] = {
  //  import com.dabc.rtj.typesafejdbc.QueryResultDecoder.*
  //
  //  val query = queryE.valueOrAbort
  //  val schema = Schema(query)
  //  val mappings = schema.columns.map(toMapping)
  //  val refinedType = makeRefinedType(mappings)
  //  val columReaders = getColumnReaders(mappings)
  //  val decoder = makeDecoder(columReaders, refinedType)
  //  refinedType match {
  //    case '[t] =>
  //      '{
  //        Query.getRows($queryE).map($decoder.decode).asInstanceOf[List[t]]
  //      }
  //  }
  //}

  private def runImpl(query: Expr[String])(using Quotes): Expr[List[?]] = {
    QueryResultDecoder.makeImpl(query) match {
      case '{ $decoder: QueryResultDecoder[t] } =>
        '{
          Query.getRows($query).map($decoder.decode).asInstanceOf[List[t]]
        }
    }
  }
}
