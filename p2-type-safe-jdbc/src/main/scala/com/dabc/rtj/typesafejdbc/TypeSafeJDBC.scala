package com.dabc.rtj
package typesafejdbc

import scala.reflect.Selectable.reflectiveSelectable
import cats.syntax.all.*
import JDBC.{Nullability, Type}
import cats.Show

import scala.reflect.ClassTag

object TypeSafeJDBC {

  object Ideal {

    val query = "select * from users"

    // 1 - find the schema
    val schema = Schema(query)

    // 2 - identify column mappings for all columns (synthesized as givens)
    val idMapping = ColumnMapping[JDBC.Integer, JDBC.NonNullable, "id"]

    // 3 - infer the result type
    type RefinedResult = Query.Result {
      val id: idMapping.Result // the same as Int
      // same for the rest of the columns (automatically)
    }

    // 4 - ability to read values from JDBC into the correct types
    val idColumnReader = idMapping.reader
    // same for the rest of the columns

    // 5 - run the query and return the correct type
    val magicResult = Query.run[RefinedResult](query)
    //                                              ^^^^^^^^^^^^^^ passed by the macro automatically

    // 6 - profit
    val ids = magicResult.map(_.id)
  }

  transparent inline def getValues[T <: Type, N <: Nullability, C <: String](rows: List[Row])(column: C)(using CM: ColumnMapping[T, N, C]): List[CM.Result] = for {
      row <- rows
      value = row.data(column)
    } yield CM.reader.read(value)

  transparent inline def getValues[T <: Type, N <: Nullability](rows: List[Row])[C <: String](column: C)(using CM: ColumnMapping[T, N, C]): List[CM.Result] =
    getValues[T, N, C](rows)(column)

  def main(args: Array[String]): Unit = {
    import JDBC.*

    val query = "select * from users"

    // 1 - print the schema
    println(Schema(query).show)

    // 2 - print some rows
    Query.runX(query).foreach(r => println(r.show))

    // 3 - print a column... or two
    given [E] => Show[List[E]] = _.mkString(", ")
    given [E] => Show[scala.Array[E]] = _.mkString(", ")

    val names = getValues[VarChar, NonNullable](Query.runX(query))("name")
    names.foreach(r => println(r.show))
    val ages = getValues[Integer, NonNullable](Query.runX(query))("age")
    ages.foreach(r => println(r.show))
    val hobbies = getValues[Array[VarChar], NonNullable](Query.runX(query))("hobbies")
    hobbies.foreach(r => println(r.show))
  }
}
