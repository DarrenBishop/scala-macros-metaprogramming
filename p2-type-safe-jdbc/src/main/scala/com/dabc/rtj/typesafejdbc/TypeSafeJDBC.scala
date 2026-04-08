package com.dabc.rtj
package typesafejdbc

import cats.syntax.all.*
import JDBC.{Nullability, Type}
import cats.Show

import scala.reflect.ClassTag

object TypeSafeJDBC {

  transparent inline def getValues[T <: Type, N <: Nullability, C <: String](rows: List[Row])(column: C)(using CM: ColumnMapping[T, N, C]): List[CM.Result] = for {
      row <- rows
      value = row.data(column)
    } yield CM.reader.read(value)

  transparent inline def getValues[T <: Type, N <: Nullability](rows: List[Row])[C <: String](column: C)(using CM: ColumnMapping[T, N, C]): List[CM.Result] =
    getValues[T, N, C](rows)(column)


  def demoJDBCReaders(): Unit = {
    import JDBC.*

    val query = "select * from users"

    // print some rows
    val rows = Query.getRows(query)
    rows.foreach(r => println(r.show))

    // print columns
    val names = getValues[VarChar, NonNullable](rows)("name")
    names.foreach(r => println(r.show))

    val ages = getValues[Integer, NonNullable](rows)("age")
    ages.foreach(r => println(r.show))

    val hobbies = getValues[Array[VarChar], NonNullable](rows)("hobbies")
    hobbies.foreach(r => println(r.show))
  }

  def demoGenerateSchema(): Unit = {
    inline val query = "select * from users"
    println(Schema(query).show)
  }

  def demoRefinedType(): Unit = {
    inline val query = "select * from users"
    val decoder = QueryResultDecoder.make(query)
    val rows = Query.getRows(query)
    val results = rows.map(decoder.decode)
    results.foreach(r => println(r.show))
  }

  def demoRefinedType_v2(): Unit = {
    inline val query = "select * from users"
    val results = Query.run(query)
    results.foreach(r => println(r.show))
  }

  def main(args: Array[String]): Unit = {
    demoRefinedType()
  }
}
