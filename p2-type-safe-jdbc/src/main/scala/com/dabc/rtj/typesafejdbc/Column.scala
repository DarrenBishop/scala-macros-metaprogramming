package com.dabc.rtj
package typesafejdbc

case class Column(
  index: Int,
  name: String,
  jdbcType: JDBC.Type,
  jdbcNullability: JDBC.Nullability
)
