package com.dabc.rtj.inlines

import com.dabc.rtj.inlines.types.Show

import scala.deriving.Mirror
import compiletime.*

object Mirrors {

  // "product"
  case class Person(name: String, age: Int, programmer: Boolean) derives Show
  // compiler will look for a method `derived` in the Show companion object
  // such that it returns a Show[Person]
  // `derives Show` will synthesize a `given Show[Person]` in the Person companion object

  // "sum"
  enum Permissions {
    case READ, WRITE, EXECUTE
  }

  // automatically derive Show[A] where A can be any Sum or Product type

  object product {
    // mirror for a Product type
    val personMirror = summon[Mirror.Of[Person]] // Mirror.ProductOf[Person]
    // mirror contains all type information

    val daniel: Person = personMirror.fromTuple(("Daniel", 99, true))
    val aTuple: (String, Int, Boolean) = Tuple.fromProductTyped(daniel)

    val className = constValue[personMirror.MirroredLabel] // name of the class, known at compile-time
    val fieldNames = constValueTuple[personMirror.MirroredElemLabels] // ...names of the fields
  }

  object sum {

    // mirror of a Sum type
    val permissionsMirror = summon[Mirror.Of[Permissions]] // Mirror.SumOf[Permissions]
    // we can get the type name
    val className = constValue[permissionsMirror.MirroredLabel] // name of the Sum (enum), known at compile-time
    // we can list all the cases
    val allCases = constValueTuple[permissionsMirror.MirroredElemLabels] // ...all the cases of the enum as strings
  }

  object coproduct {

    sealed trait Permissions
    case object Read extends Permissions
    case object Write extends Permissions
    case object Execute extends Permissions

    // mirror of a Coproduct type
    val permissionsMirror = summon[Mirror.Of[Permissions]] // Mirror... ???
    // we can get the type name
    val className = constValue[permissionsMirror.MirroredLabel] // name of the Coproduct root class, known at compile-time
    // we can list all the cases
    val allCases = constValueTuple[permissionsMirror.MirroredElemLabels] // ...all the instances of the Coproduct as strings
  }

  val masterYoda = Person("Master Yoda", 800, false)
  val showPerson = Show.derived[Person] // explicit
  val showPerson_v2 = summon[Show[Person]] // implicit
  val showPerson_v3 = Person.derived$Show // explicit type-class instance, synthesized by `derives Show`
  val masterYodaShown = showPerson.show(masterYoda)

  def printThing[A](thing: A)(using Show[A]): Unit =
    println(summon[Show[A]].show(thing))

  def main(args: Array[String]): Unit = {
    val shownTuple  = Show.showTuple[(String, Int, Boolean), ("name", "age", "programmer")](("Daniel", 99, true))
    println(shownTuple)

    println(product.daniel.show)

    println(masterYodaShown)

    printThing(masterYoda) // <-- `Sow[Person]` passed implicitly here
  }
}
