package com.dabc.rtj
package typesafejdbc

object QueryMagic {
  def run(query: String): List[(d: Int, name: String, age: int, hobbies: List[String])] = ???
}

object TypeSageJDBC {

  val query = "select * from users"
  QueryMagic.run(query).map(_.name) // i.e. we want to derive some structured type from the DB schema that has a `name` field

  def main(args: Array[String]): Unit = {
    println("lock and load")
  }
}
