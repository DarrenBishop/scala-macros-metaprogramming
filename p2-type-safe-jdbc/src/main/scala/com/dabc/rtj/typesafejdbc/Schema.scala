package com.dabc.rtj
package typesafejdbc

import cats.Show

import java.sql.{ResultSetMetaData, Types}

case class Column(
  index: Int,
  name: String,
  jdbcType: JDBC.Type,
  jdbcNullability: JDBC.Nullability
)

case class Schema(columns: List[Column])

object Schema {
  given Show[Schema]:
    def show(t: Schema): String =
      t.columns
        .foldLeft(StringBuilder().append("Printing Schema:\n")) {
          (sb, c) => sb.append(s"\t$c\n")
        }
        .mkString


  //case object Name {
  //  inline def unapply(name: String): Boolean = name.startsWith(prefix)
  //  inline def apply(inline prefix: String): Prefix = Prefix(prefix)
  //}
  extension (md: ResultSetMetaData)
    def getName(index: Int): String = md.getColumnLabel(index)
    def getType(index: Int): JDBC.Type = md.getColumnType(index) match {
      case Types.VARCHAR => JDBC.VarChar
      case Types.INTEGER => JDBC.Integer
      case Types.DOUBLE => JDBC.Double
      case Types.FLOAT => JDBC.Float
      case Types.BOOLEAN => JDBC.Boolean
      case Types.ARRAY =>
        md.getColumnTypeName(index) match {
          case n if n.contains("varchar") => JDBC.Array(JDBC.VarChar)
          case n if n.contains("character") => JDBC.Array(JDBC.VarChar)
          case n if n.contains("integer") => JDBC.Array(JDBC.Integer)
          // FIXME: add all other cases if you want this library production-ready
          case name =>
            println(s"Could not infer array type for $name!")
            JDBC.NotSupported
        }
      case _ => JDBC.NotSupported
    }
    def getNullable(index: Int): JDBC.Nullability = md.isNullable(index) match {
      case ResultSetMetaData.columnNoNulls => JDBC.NonNullable
      case _ => JDBC.Nullable
    }

  def apply(metadata: ResultSetMetaData): Schema = fromMetaData(metadata)

  def fromMetaData(metadata: ResultSetMetaData): Schema = {
    val descriptors = for {
      index <- (1 to  metadata.getColumnCount).toList
      name = metadata.getName(index)
      jdbcType = metadata.getType(index)
      nullable = metadata.getNullable(index)
    } yield Column(index, name, jdbcType, nullable)

    Schema(descriptors)
  }
}
