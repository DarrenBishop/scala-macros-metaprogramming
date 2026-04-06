package com.dabc.rtj

type Using = scala.util.Using.type
val Using = scala.util.Using

extension [A](any: A)
  def peek(f: A => Unit): A = {f(any); any}
  def ?< (f: A => Unit): A = peek(f)
