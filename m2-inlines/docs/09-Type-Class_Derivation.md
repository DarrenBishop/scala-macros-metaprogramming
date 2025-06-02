# Type-Class Derivation

### Allow the compiler to auto-synthesize type class instances as given values

```scala 3
case class Person(name: String, age: Int, programmer: Boolean) derives Show
```

### Requirement: a public `derived` method in the companion object with
 - no proper (explcit) argument list
 - `Show[that type]` as return value
 ```scala 3 mdoc:ignore
 trait Show[A] { ... }
 
 object Show {
   inline def derived[A <: Product](using M: Mirror.ProductOf[A]): Show[A] = ...
 }
 ```

### Signature amd implementation are flexible
- can be `inline`
- can use generics (and it usually will)
- can have using clauses
- can be implemented in any way
