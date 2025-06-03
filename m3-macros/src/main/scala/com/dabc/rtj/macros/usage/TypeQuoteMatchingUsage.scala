package com.dabc.rtj.macros
package usage

import scala.util.Try

object TypeQuoteMatchingUsage {
  import TypeQuoteMatching.*

  val  intDescriptor = matchType[Int]
  val  listIntDescriptor = matchType[List[Int]]
  val  listBooleanDescriptor = matchType[List[Boolean]]
  val  eitherThrowableOrIntDescriptor = matchType[Either[Throwable, Int]]
  val  functionDescriptor = matchType[Int => String]
  val tupleDescriptor = matchType[(Int, String, Int)]
  val tryIntDescriptor = matchType[Try[Int]]
  val tryStringDescriptor = matchType[Try[String]]
}
