package com.dabc.rtj
package typesafejdbc

trait JDBCReader[T] {
  def read(value: Any): T
}
