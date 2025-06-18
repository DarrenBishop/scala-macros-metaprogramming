package com.dabc.rtj.macros
package usage

/*
    Structural type = "compile time duck typing"
 */
class Person(val name: String, val age: Int)

object StructuralTypesUsage {
  import StructuralTypes.*

  def makePerson(name: String): Person = Person(name, age = 0)
  def makeProgrammer(name: String): Person { val favLanguage: String } = ???
  //                                                            ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^ structural type


  val simpleRecord = Record.make(
    "name" -> "Daniel",
    "age" -> 99,
    "favLanguage" -> "Scala"
  )

  val name = simpleRecord.fields.getOrElse("name", "")
  // we would like to use the fields STATICALLY
  // (with auto-completion)

  val nameStatic = simpleRecord.name
  val ageStatic = simpleRecord.age
}
