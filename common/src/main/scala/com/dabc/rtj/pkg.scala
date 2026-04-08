package com.dabc.rtj


import scala.compiletime.summonFrom
import cats.{Foldable, Functor, Show}
import cats.syntax.all.*

export rtj.syntax.*

type Using = scala.util.Using.type
val Using = scala.util.Using

extension [A](any: A)
  def peek(f: A => Unit): A = {f(any); any}
  def ?< (f: A => Unit): A = peek(f)

type Show[T] = cats.Show[T]
val Show = cats.Show

inline given [T] => Show[T] = summonFrom {
  case ev: Show[T] => ev
  case _ => Show.fromToString
}

given [E: Show] => Show[Array[E]] = _.map(_.show).mkString(", ")

given [E: Show, C[_] : {Functor, Foldable}] => Show[C[E]] = _.map(_.show).intercalate(", ")
