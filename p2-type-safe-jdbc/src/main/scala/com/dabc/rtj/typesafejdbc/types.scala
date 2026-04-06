package com.dabc.rtj
package typesafejdbc

object JDBC {

  opaque type Descriptor = Any
  sealed trait DescriptorC[U] { self: Singleton =>
    type Underlying = U
    given this.type = this
  }

  opaque type Type <: Descriptor = Descriptor
  sealed trait TypeC[U] extends DescriptorC[U]

  opaque type String <: Type = String.Underlying
  case object String extends TypeC[Predef.String]

  opaque type Integer <: Type = Integer.Underlying
  case object Integer extends TypeC[Int]

  opaque type Boolean <: Type = Boolean.Underlying
  case object Boolean extends TypeC[scala.Boolean]

  opaque type Nullability <: Descriptor = Descriptor
  sealed trait NullabilityC[U] extends DescriptorC[U]

  opaque type Nullable <: Nullability = Nullable.Underlying
  case object Nullable extends NullabilityC[true]

  opaque type NonNullable <: Nullability = NonNullable.Underlying
  case object NonNullable extends NullabilityC[false]
}
