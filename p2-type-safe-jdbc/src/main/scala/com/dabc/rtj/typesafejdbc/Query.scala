package com.dabc.rtj
package typesafejdbc

import java.sql.{Connection, ResultSet}
import scala.collection.mutable.ListBuffer

//opaque type Query <: String  = String
type Query = String

object Query {
  class Result extends Selectable

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

  //def run(query: Query): List[(d: Int, name: String, age: Int, hobbies: List[String])] = ???
  def run[R](query: Query): List[R] = ???

  def runX(query: Query): List[Row] = JDBCCommunication.withConnection { conn =>
    given Connection = conn
    val statement = conn.createStatement()
    val results = statement.executeQuery(query)
    val metadata = results.getMetaData
    val schema = Schema(metadata)

    parseRows(schema, results)
  }
}
