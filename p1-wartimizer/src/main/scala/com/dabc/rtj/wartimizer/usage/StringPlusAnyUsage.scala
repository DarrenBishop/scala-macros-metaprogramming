package com.dabc.rtj.wartimizer
package usage

//import Wartimizer.wartimize

case class Person(name: String, email: String)

object StringPlusAnyUsage {

  val badPractice = "This is Scala: " + Person("Darren Bishop",  "my@email.com")

  // this does not compile (good!)
  //val linted = wartimize(StringPlusAny)("This is Scala: " + Person("Darren Bishop",  "my@email.com"))

  //val wartimizer = StringPlusAny
  //// this does not compile - but finding the `val` wartimizer is not the issue
  //val linted = wartimize(wartimizer)("This is Scala: " + Person("Darren Bishop",  "my@email.com"))

  //val wartimizer: Wartimization = StringPlusAny
  //// this does not compile because the variable is not a compile-time known singleton instance (object)
  //val linted = wartimize(wartimizer)("This is Scala: " + Person("Darren Bishop",  "my@email.com"))

  //val wartimizer: StringPlusAny.type = StringPlusAny
  //// this does not compile (good!)
  //val linted = wartimize(wartimizer)("This is Scala: " + Person("Darren Bishop",  "my@email.com"))

  def main(args: Array[String]): Unit = {
    println(badPractice)
  }
}
