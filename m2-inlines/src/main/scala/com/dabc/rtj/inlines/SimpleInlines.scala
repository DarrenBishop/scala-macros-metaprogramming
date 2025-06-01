package com.dabc.rtj.inlines

import scala.util.Random

object SimpleInlines {

  def increment(x: Int): Int = x + 1
  inline def inc(x: Int): Int = x + 1

  val aNumber = 3
  val four = aNumber + 1
  val four_v2 = inc(aNumber) // same, after inlining; reduces to `aNumber + 1` at COMPILE TIME

  val eight = inc(2 * (aNumber + 1))
  /*
      reduces to
      {
        val proxy = 2 * aNumber + 1
        proxy + 1
      }
   */

  // inline arguments - expanded within the method body
  inline def incia(inline x: Int) : Int = x +1
  val eight_v2 = incia(2 * (aNumber + 1)) // reduces to  `2 * (aNumber + 1) + 1`
    // conceptually similar to by-name invocation, only that the args are expanded at COMPILE TIME

  val eight_v3 = incia(2 * inc(aNumber))

  // transparent inline
  transparent inline def wrap(x: Int): Option[Int] = Some(x)

  val anOption: Option[Int] = wrap(7) // type-check? ok
  val aSome: Some[Int] = wrap(7) // type-check? ok, only if `wrap(...)` is transparent

  // perf optimization
  def testInline() = {
    inline def loop[A](inline start: => A, inline condition: A => Boolean, inline advance: A => A)(inline action: A => Any): Any = {
      var a = start
      while (condition(a)) {
        action(a)
        a = advance(a)
      }
    }

    /*
        a = array(10000)
        for (i = 0..10000)
          for (j = 0..10000)
            a[i] += u
          a[i] += r
     */

    val start = System.currentTimeMillis()
    val r = Random.nextInt(10_000)
    val u = Random.nextInt(10_000)
    val arr = Array.ofDim[Int](10_000)

    loop(0, _ < 10_000, _ + 1) { i =>
      loop(0, _ < 10_000, _ + 1) { _ =>
        arr(i) = arr(i) + u
      }
      arr(i) = arr(i) + r
    }

    println(s"Inline version: ${(System.currentTimeMillis() - start) / 1000.0} s")
  }

  def testNoInline() = {
    def loop[A](start: => A, condition: A => Boolean, advance: A => A)(action: A => Any): Any = {
      var a = start
      while (condition(a)) {
        action(a)
        a = advance(a)
      }
    }

    /*
        a = array(10000)
        for (i = 0..10000)
          for (j = 0..10000)
            a[i] += u
          a[i] += r
     */

    val start = System.currentTimeMillis()
    val r = Random.nextInt(10_000)
    val u = Random.nextInt(10_000)
    val arr = Array.ofDim[Int](10_000)

    loop(0, _ < 10_000, _ + 1) { i =>
      loop(0, _ < 10_000, _ + 1) { _ =>
        arr(i) = arr(i) + u
      }
      arr(i) = arr(i) + r
    }

    println(s"No inline version: ${(System.currentTimeMillis() - start) / 1000.0} s")
  }

  def main(args: Array[String]): Unit = {
    testInline()

    testNoInline()
  }
}
