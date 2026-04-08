package com.dabc.rtj.typesafejdbc

import JDBC.*

import scala.reflect.ClassTag

trait ColumnMapping[T <: Type, N <: Nullability, C <: String] {
  type Result // left abstract, and will be inferred by the compiler

  def reader: JDBCReader[Result]
}

// val columnMapping = ColumnMapping[JDBC.Integer, JDBC.NonNullable, "id"]
// (type Result = Int, inferred by the compiler)

object ColumnMapping {

  type Aux[T <: Type, N <: Nullability, C <: String, R] = ColumnMapping[T, N, C] {
    type Result = R
  }

  def apply[T <: Type, N <: Nullability, C <: String, R](using ev: Aux[T, N, C, R]): ev.type = ev

  //given [C <: String]: ColumnMapping[VarChar, NonNullable, C] with {
  //  type Result = String
  //  def reader: JDBCReader[String] = JDBCReader.string
  //}
  //
  //given [C <: String]: ColumnMapping[Integer, NonNullable, C] with {
  //  type Result = Int
  //  def reader: JDBCReader[Int] = JDBCReader.int
  //}
  //
  //given [C <: String]: ColumnMapping[Double, NonNullable, C] with {
  //  type Result = scala.Double
  //  def reader: JDBCReader[scala.Double] = JDBCReader.double
  //}
  //
  //given [C <: String]: ColumnMapping[Float, NonNullable, C] with {
  //  type Result = scala.Float
  //  def reader: JDBCReader[scala.Float] = JDBCReader.float
  //}
  //
  //given [C <: String]: ColumnMapping[Boolean, NonNullable, C] with {
  //  type Result = scala.Boolean
  //  def reader: JDBCReader[scala.Boolean] = JDBCReader.boolean
  //}

  // non-nullable colum mappings
  given [T <: Type, C <: String] => (T: T, R: JDBCReader[T.Underlying]) => ColumnMapping[T, NonNullable, C] {
    type Result = T.Underlying
    def reader: JDBCReader[Result] = R
  }

  // nullable column mappings
  given [T <: Type, C <: String, R] => (CM: Aux[T, NonNullable, C, R]) => ColumnMapping[T, Nullable, C] {
    type Result = Option[R]
    def reader: JDBCReader[Result] = CM.reader.toOption
  }

  // array column mappings
  given [T <: Type, C <: String, R] =>(CM: Aux[T, NonNullable, C, R]) => (ClassTag[R]) => ColumnMapping[Array[T], NonNullable, C] {
    type Result = scala.Array[R]
    def reader: JDBCReader[Result] = CM.reader.toArray
  }
  //given [T <: Type, C <: String] =>(CM: ColumnMapping[T, NonNullable, C]) => (ClassTag[CM.Result]) => ColumnMapping[Array[T], NonNullable, C] {
  //  type Result = List[CM.Result]
  //  def reader: JDBCReader[Result] = CM.reader.toList
  //}
}
