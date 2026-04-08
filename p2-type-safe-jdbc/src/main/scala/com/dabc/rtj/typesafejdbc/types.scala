package com.dabc.rtj
package typesafejdbc

object JDBC {
  sealed trait Descriptor {
    given this.type = this
  }

  sealed trait Type extends Descriptor {
    type Underlying
  }

  sealed trait TypeC[U] extends Type, Descriptor {
    type Underlying = U
    given TypeC[Underlying] = this
  }

  case object VarChar extends TypeC[Predef.String]
  type VarChar = VarChar.type

  case object Integer extends TypeC[scala.Int]
  type Integer = Integer.type

  case object Double extends TypeC[scala.Double]
  type Double = Double.type

  case object Float extends TypeC[scala.Float]
  type Float = Float.type

  case object Boolean extends TypeC[scala.Boolean]
  type Boolean = Boolean.type

  sealed trait Array[T <: Type] extends TypeC[?] {
    type Elem = T
    val t: Elem
  }
  case object Array {
    case class Array[T <: Type, U] private[JDBC] (t: T) extends JDBC.Array[T], TypeC[scala.Array[U]]
    given apply[T <: Type](using T: T): JDBC.Array[T] = new Array[T, T.Underlying](T)
    def unapply[T <: Type](array: Array[T, ?]): Option[T] = Some(array.t)
  }

  case object NotSupported extends TypeC[Nothing]
  type NotSupported = NotSupported.type

  // Nullability, essentially true or false
  sealed trait Nullability extends Descriptor

  case object Nullable extends Nullability
  type Nullable = Nullable.type

  case object NonNullable extends Nullability
  type NonNullable = NonNullable.type
}
