package com.dabc.rtj.warmup

object CustomStringInterpolators {

  // s-interpolator
  val pi = 3.14159
  val sInterpolator = s"The value of PI is approx ${pi + 0.000002}, the regular pi is $pi"

  // f-interpolator, similar to printf
  val fInterpolator = f"The value of PI up to 3 sig digits is $pi%3.2f"

  // raw-interpolator = escape sequence
  val rawInterpolator = raw"The value of pi is $pi\n this is NOT newline"

  // sql: select * from ..."
  // $"first_name"

  case class Person(name: String, age: Int)

  // name, age => Person
  def stringToPerson(line: String): Person = {
    val tokens = line.split(",")
    Person(tokens(0), tokens(1).toInt)
  }

  // pers" $name,$age" -> Person("name", age)
  // StringContext + extension method
  extension (sc: StringContext)
    def pers(args: Any*): Person = stringToPerson(sc.s(args*))

  val daniel = pers"Daniel,99"
  val name = "Daniel"
  val age = 91
  val daniel_v2 = pers"$name,$age"

  def main(args: Array[String]): Unit = {
    println(sInterpolator)
    println(fInterpolator)
    println(rawInterpolator)
    println(daniel)
    println(daniel_v2)
  }
}
