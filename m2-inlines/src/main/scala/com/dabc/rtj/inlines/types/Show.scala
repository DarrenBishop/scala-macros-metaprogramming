package com.dabc.rtj.inlines.types


trait Show[A] {
  def show(a: A): String
  def comap[B](f: B => A): Show[B] = b => show(f(b))
}

object Show {
  given Show[String] = identity(_)
  given Show[Int] = _.toString
  given Show[Boolean] = _.toString
}
