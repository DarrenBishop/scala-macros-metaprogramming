package com.dabc.rtj
package typesafejdbc

import java.sql.*
import scala.util.NotGiven

object JDBCCommunication {

  private def useConnection[R](f: Connection => R): R = {
    // load the driver
    Class.forName("org.postgresql.Driver") // necessary to load the driver during macro expansion

    Using.resource(DriverManager.getConnection("jdbc:postgresql://localhost:5432/", "docker", "docker"))(f)
  }

  //private[typesafejdbc] def withConnection[R](using C: Connection)(f: Connection => R): R = { f(C) }

  private[typesafejdbc] def withConnection[R](using NotGiven[Connection])(f: Connection => R): R =
    useConnection(f)

  //private[typesafejdbc] def withConnection[R](using NotGiven[Connection])(f: Connection ?=> Connection => R): R =
  //  withConnection(conn => f(using conn)(conn))
}
