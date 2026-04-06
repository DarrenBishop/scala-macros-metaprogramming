package com.dabc.rtj.typesafejdbc

trait ColumnMapping[T <: JDBC.Type, N <: JDBC.Nullability, C <: String] {
  type Result // left abstract, and will be inferred by the compiler

  def reader: JDBCReader[Result]
}

// val columnMapping = ColumnMapping[JDBC.Integer, JDBC.NonNullable, "id"]
// (type Result = Int, inferred by the compiler)

object ColumnMapping {
  def apply[T <: JDBC.Type, N <: JDBC.Nullability, C <: String](using C: ColumnMapping[T, N, C]): C.type = C
}
