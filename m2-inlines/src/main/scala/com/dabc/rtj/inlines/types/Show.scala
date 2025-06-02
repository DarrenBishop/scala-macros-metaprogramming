package com.dabc.rtj.inlines.types

import scala.deriving.Mirror
import compiletime.*

trait Show[A] { tc =>
  def comap[B](f: B => A): Show[B] = b => show(f(b))
  extension (a: A)
    def show: String
}

object Show {

  private def create[A](f: A => String): Show[A] = f(_)

  given Show[String] = identity(_)
  given Show[Int] = _.toString
  given Show[Boolean] = _.toString

  // auto-derivation for a serialization type class

  // showTuple[(String, Int, Boolean), ("name", "age", "programmer")](("Daniel", 99, true))
  // ["name: Daniel", "age: 99","programmer: true"]
  inline def showTuple[E <: Tuple, L <: Tuple](elements: E): List[String] = {
    inline (elements, erasedValue[L]) match {
      case (vs: (vth *: vtt), _: (lth *: ltt)) =>
        val vh *: vt = vs
        val label = constValue[lth]
        val value = summonInline[Show[vth]].show(vh)
        s"$label: $value" :: showTuple[vtt, ltt](vt)
      case _ => Nil
    }
  }

  /*
      Necessary for type class derivation
      Signature
      - must be called `derived`
      - must not have na explicit argument list of its own; implicit/using argument list is allowed
      - must return a `Show[of that particular type]`
   */
  inline def derived[A <: Product](using M: Mirror.ProductOf[A]): Show[A] = create { a =>
    val className = constValue[M.MirroredLabel]
    val fields = showTuple[M.MirroredElemTypes, M.MirroredElemLabels]{Tuple.fromProductTyped(a)}
    // implement encoding appropriate for this type-class
    (s"__type: $className" :: fields).mkString("{\n\t", ",\n\t", "\n}")
  }
}
