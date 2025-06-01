# Summoning

```scala 3 mdoc:invisible
trait Semigroup[A] {
  def combine(a1: A, a2: A): A
}
```

### Delay the summoning of givens to the call site with `summonInline`:

```scala 3
inline  def double [A](a: A)(using Semigroup[A]): A =
  summon[Semigroup[A]].combine(a, a)

given Semigroup[Int] = _ + _
val four = double(2) // ok
// val scalaX2 = double("Scala") // not ok
```

### Conditionally inline different expressions based on existing givens with `summonFrom:

```scala 3 mdoc:invisible
trait Messenger[A] {
  def message: String
}
```

```scala 3
inline def produceMessage[A] = summonFrom {
    case ev:  Messenger[A] => " Found messenger: " + ev.message
    case _ => "Bummer, no messenger found for this type"
}

given Messenger[Int]:
  def message: String = "this is an Int speaking"

val intMessage = produceMessage[Int] // "Found messenger: " + Messenger[Int].message
val otherMessage = produceMessage[String] // "Bummer, no messenger found for this type"
````
