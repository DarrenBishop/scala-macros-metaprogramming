package com.dabc.rtj.inlines

import com.dabc.rtj.inlines.types.Show

import scala.deriving.Mirror
import compiletime.*

object Mirrors {

  // "product"
  case class Person(name: String, age: Int, programmer: Boolean)

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

  // auto-derivation for a serialization type class

  // showTuple[(String, Int, Boolean), ("name", "age", "programmer")](("Daniel", 99, true))
  // ["name: Daniel", "age: 99","programmer: true"]
  inline def showTuple[E <: Tuple, L <: Tuple](elements: E): List[String] =
    inline (elements, erasedValue[L]) match {
      case (vs: (vth *: vtt), _: (lth *: ltt)) =>
        val vh *: vt = vs
        val label = constValue[lth]
        val value = summonInline[Show[vth]].show(vh)
        s"$label: $value" :: showTuple[vtt, ltt](vt)
      case _ => Nil
    }

  inline def showProduct[A <: Product](using M: Mirror.ProductOf[A]): Show[A] = (a: A) => {
    val className = constValue[M.MirroredLabel]
    val fields = showTuple[M.MirroredElemTypes, M.MirroredElemLabels]{Tuple.fromProductTyped(a)}
    (s"__type: $className" :: fields).mkString("{\n\t", ",\n\t", "\n}")
  }

  inline given [A <: Product: Mirror.ProductOf]: Show[A] = showProduct[A]

  // reduced to one step/function... does not compile
  //inline given [A <: Product] => (M: Mirror.ProductOf[A]) => Show[A]:
  //  def show(a: A): String = {
  //    val className = constValue[M.MirroredLabel]
  //    val fields = showTuple[M.MirroredElemTypes, M.MirroredElemLabels]{Tuple.fromProductTyped(a)}
  //    s"$className${fields.mkString("(\n\t", ",\n\t", "\n)")}"
  //  }

  def show[A](a: A)(using S: Show[A]): String = S.show(a)

  def main(args: Array[String]): Unit = {
    val shownTuple  = showTuple[(String, Int, Boolean), ("name", "age", "programmer")](("Daniel", 99, true))
    println(shownTuple)

    val shownPerson = show(product.daniel)
    println(shownPerson)
  }
}
