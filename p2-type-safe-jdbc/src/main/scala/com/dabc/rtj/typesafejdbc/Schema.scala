package com.dabc.rtj
package typesafejdbc

import cats.{Foldable, Functor, Show}
import cats.syntax.all.*
import alleycats.std.iterable.alleycatsStdIterableTraverse

import java.sql.{ResultSetMetaData, Types}
import scala.reflect.ClassTag
import scala.util.NotGiven

case class Column(
  index: Int,
  name: String,
  jdbcType: JDBC.Type,
  jdbcNullability: JDBC.Nullability
)

case class Row(data: Map[String, Any])

object Row {
  import scala.compiletime.summonFrom

  inline given [T] => Show[T] = summonFrom {
    case ev: Show[T] => ev
    case _ => Show.fromToString
  }

  given [E: Show] => Show[Array[E]] = _.map(_.show).mkString(", ")

  given [E: Show, C[_]: {Functor, Foldable}] => Show[C[E]] = _.map(_.show).intercalate(", ")

  given Show[Row] = { row =>
    StringBuilder().peek { sb =>
      sb.append("Printing Rows:\n")
      row.data.foreach {
        case (k, vs: Array[e]) => sb.append(show"\t$k: $vs\n")
        case (k, vs: List[e]) => sb.append(show"\t$k: $vs\n")
        case (k, v) => sb.append(show"\t$k: $v\n")
      }
    }
    .mkString
  }
}

case class Schema(columns: List[Column])

object Schema {
  given Show[Schema] = { schema =>
      schema.columns
        .foldLeft(StringBuilder().append("Printing Schema:\n")) {
          (sb, c) => sb.append(s"\t$c\n")
        }
        .mkString
  }


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
          case n if n.contains("varchar") => JDBC.Array[JDBC.VarChar]
          case n if n.contains("character") => JDBC.Array[JDBC.VarChar]
          case n if n.contains("integer") => JDBC.Array[JDBC.Integer]
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

  def fromMetaData(metadata: ResultSetMetaData): Schema = {
    val descriptors = for {
      index <- (1 to  metadata.getColumnCount).toList
      name = metadata.getName(index)
      jdbcType = metadata.getType(index)
      nullable = metadata.getNullable(index)
    } yield Column(index, name, jdbcType, nullable)

    Schema(descriptors)
  }

  def apply(metadata: ResultSetMetaData): Schema = fromMetaData(metadata)

  def apply(query: String): Schema = JDBCCommunication.withConnection {
    // Use a connection to the DB
    conn =>

    // create a PreparedStatement
    val statement = conn.prepareStatement(query)

    // get the metadata out of the PreparedStatement
    val metadata = statement.getMetaData

    // => Schema

    Schema(metadata)
  }
}
