package com.dabc.rtj.macros

import quoted.{Quotes, Type}


private[macros] def tpe[T: Type](using Quotes): String = Type.show[T]
