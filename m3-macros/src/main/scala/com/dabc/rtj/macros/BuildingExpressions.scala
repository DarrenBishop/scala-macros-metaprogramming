package com.dabc.rtj.macros

import quoted.*

object BuildingExpressions {
  enum Permissions {
    case Denied
    case Bitset(value: Int)
    case Custom(dirs: List[String])
  }

  def buildStringExpr(using Quotes): Expr[String] =
    Expr("this is a string expression") // ToExpr[String] exists, same for Int, ...

  inline def createDefaultPermissions(): Permissions =
    ${ buildPermissionsExpr }

  import Permissions.*

  // does not compile UNLESS we have a `given ToExpr[Permissions]`
  def buildPermissionsExpr(using Quotes): Expr[Permissions] =
    Expr(Custom(List("photos", "books"))) // ...requires a `given ToExpr[Permissions]`

  // given ToExpr[CustomType]
  given ToExpr[Permissions]:
    def apply(value: Permissions)(using Quotes): Expr[Permissions] =
      value match {
        case Denied => '{ Denied }
        case Bitset(value) =>
          val valueExpr = Expr(value) // this is possible because we have ToExpr[Int]
          '{ Bitset($valueExpr) }
        case Custom(dirs) =>
          val dirsExpr = Expr(dirs) // this is possible for List, std collections
          '{ Custom($dirsExpr) }
      }

  // given FromExpr[CustomType]
  given FromExpr[Permissions]:
    def unapply(x: Expr[Permissions])(using Quotes): Option[Permissions] =
      x match {
        case '{ Denied } => Some(Denied)
        case '{ Bitset($value) } => // value = Expr[Int]
          val knownValue = value.valueOrAbort // can surface compile errors
          Some(Bitset(knownValue))
        case '{ Custom($dirs) } => // dirs = Expr[List[String]]
          val knownDirs = dirs.valueOrAbort
          Some(Custom(knownDirs))
        case _ => None
      }

  inline def describePermission(inline permissions: Permissions) =
    ${ describePermissionImpl('permissions) }

  private def describePermissionImpl(permissions: Expr[Permissions])(using Quotes): Expr[String] = {
    val p = permissions.valueOrAbort // can turn an `Expr[T]` into a value of `T`, if there is a `given FromExpr[T]` in scope

    val result = p match {
      case Denied => "no permissions"
      case Bitset(value) => s"limited general permissions: $value"
      case Custom(dirs) => s"wide permissions for a limited directory set: ${dirs.mkString(", ")}"
    }

    Expr(result)
  }
}
