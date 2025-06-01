# inline Methods

### Method invocations are replaced with the body of the method

```scala 3
inline def inc(x: Int): Int = x + 1

val aNumber = 3
val four = inc(aNumber) // reduces to `aNumber + 1` at COMPILE TIME
```

### Inline arguments are replaced with the expressions they're used with

```scala 3
inline def incia(inline x: Int) : Int = x +1
val eight = incia(2 * (aNumber + 1)) // reduces to  `2 * (aNumber + 1) + 1`
```

### Transparent inlines let the compiler see the most concrete type of the impl

```scala 3
  transparent inline def wrap(x: Int): Option[Int] = Some(x)

  val anOption: Option[Int] = wrap(7) // type-check? ok
  val aSome: Some[Int] = wrap(7) // type-check? ok, only if transparent
```
