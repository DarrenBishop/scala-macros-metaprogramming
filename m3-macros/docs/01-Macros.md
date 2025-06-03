# Macros

### Manipulate code at compile-time

- transform code into an AST - quoting
- change the AST - our job, the macro implementation
- inject the AST back into the code - splicing

```scala 3
inline def firstMacro(number: Int, string: String): String =
  ${ firstMacroImpl('number, 'string) }

def firstMacroImpl(numAST: Expr[Int], stringAST: Expr[String])(using Quotes): Expr[String] = {
  val numValue = numAST.valueOrAbort
  val stringValue = stringAST.valueOrAbort
  
  val newString =
    if (stringValue.length >10) stringValue.take(numValue)
    else stringValue.repeat(numValue)

  Expr("This macro impl is: " + newString)
}
```

#### Quoting - turn expressions into ASTs

```scala 3
'number, 'string
```

#### Splicing - pull and AST back in the code

```scala 3
${ ... }
```


#### Evaluate - turn AST that can be computed at compile-time into a value

```scala 3
numAST.valueOrAbort
```