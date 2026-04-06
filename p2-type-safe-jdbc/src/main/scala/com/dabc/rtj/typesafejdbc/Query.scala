package com.dabc.rtj
package typesafejdbc

//opaque type Query <: String  = String
type Query = String

object Query {
  class Result extends Selectable

  //def run(query: Query): List[(d: Int, name: String, age: Int, hobbies: List[String])] = ???
  def run[R](query: Query): List[R] = ???
}
