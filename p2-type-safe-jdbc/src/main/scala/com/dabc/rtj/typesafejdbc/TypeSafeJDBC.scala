package com.dabc.rtj
package typesafejdbc

object TypeSafeJDBC {

  val query = "select * from users"
  Query.run(query).map(_.name)
  // i.e. we want to derive some structured type from the DB schema that has a `name` field

  // 1 - find the schema
  val schema = JDBCCommunication.getSchema(query)

  // 2 - identify column mappings for all columns (synthesized as givens)
  val idMapping: ColumnMapping[JDBC.Integer, JDBC.NonNullable, "id"] = ??? // summon

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
  val ids = magicResult.map(_,id)

  def main(args: Array[String]): Unit = {
    println("lock and load")
  }
}
