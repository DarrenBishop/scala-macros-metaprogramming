package com.dabc.rtj
package typesafejdbc

import scala.reflect.ClassTag

trait JDBCReader[T] {
  def read(value: Any): T
  def map[R](f: T => R): JDBCReader[R] = value => f(read(value))
  def toOption: JDBCReader[Option[T]] = map(Option(_))
  def toArray(using ClassTag[T]): JDBCReader[Array[T]] = value => value.asInstanceOf[Array[Any]].map(read)
  def toList(using ClassTag[T]): JDBCReader[List[T]] = toArray.map(_.toList)
}

object JDBCReader {
  given string: JDBCReader[String] = (value: Any) => value.asInstanceOf
  given int: JDBCReader[Int] = (value: Any) => value.asInstanceOf
  given double: JDBCReader[Double] = (value: Any) => value.asInstanceOf
  given float: JDBCReader[Float] = (value: Any) => value.asInstanceOf
  given boolean: JDBCReader[Boolean] = (value: Any) => value.asInstanceOf
}
