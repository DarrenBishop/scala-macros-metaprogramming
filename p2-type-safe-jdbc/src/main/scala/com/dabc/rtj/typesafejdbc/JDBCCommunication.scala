package com.dabc.rtj
package typesafejdbc

import java.sql.*

object JDBCCommunication {
  def getSchema(query: Query): Schema = {
    // load the driver
    Class.forName("org.postgresql.Driver") // necessary to load the driver during macro expansion

    Using.resource(DriverManager.getConnection("jdbc:postgresql://localhost:5432/", "docker", "docker")) {
      // Use a connection to the DB
      conn =>

      // create a PreparedStatement
      val statement = conn.prepareStatement(query)

      // get the metadata out of the PreparedStatement
      val metadata = statement.getMetaData

      // => Schema

      Schema(metadata)
    }
  }
}
