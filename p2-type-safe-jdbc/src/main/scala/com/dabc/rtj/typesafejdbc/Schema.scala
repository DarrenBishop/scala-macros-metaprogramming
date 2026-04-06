package com.dabc.rtj
package typesafejdbc

trait Descriptor {}

case class Column(
  index: Int,
  name: String,
  jdbcType: JDBC.Type,
  jdbcNullability: JDBC.Nullability
) extends Descriptor

case class Schema(column: List[Column])
