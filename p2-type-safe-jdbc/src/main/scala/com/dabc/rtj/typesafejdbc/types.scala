package com.dabc.rtj
package typesafejdbc

object JDBC {
  sealed trait Descriptor

  sealed trait DescriptorC[U <: AnyKind] extends Descriptor { //self: Singleton =>
    type Underlying = U
    given this.type = this
  }

  sealed trait Type extends Descriptor
  sealed trait TypeC[U <: AnyKind] extends Type, DescriptorC[U]

  case object String extends TypeC[Predef.String]
  opaque type String <: Type = String.type

  case object VarChar extends TypeC[Predef.String]
  opaque type VarChar <: Type = VarChar.type

  case object Integer extends TypeC[scala.Int]
  opaque type Integer <: Type = Integer.type

  case object Double extends TypeC[scala.Double]
  opaque type Double <: Type = Double.type

  case object Float extends TypeC[scala.Float]
  opaque type Float <: Type = Float.type

  case object Boolean extends TypeC[scala.Boolean]
  opaque type Boolean <: Type = Boolean.type

  case class Array[U](elem: TypeC[U]) extends TypeC[scala.Array[U]]

  case object NotSupported extends TypeC[Nothing]
  opaque type NotSupported <: Type = NotSupported.type

  // Nullability, essentially true or false
  sealed trait Nullability extends Descriptor
  sealed trait NullabilityC[U] extends Nullability, DescriptorC[U]

  case object Nullable extends NullabilityC[true]
    opaque type Nullable <: Nullability = Nullable.type

  case object NonNullable extends NullabilityC[false]
  opaque type NonNullable <: Nullability = NonNullable.type
}
