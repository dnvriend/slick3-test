package com.github.dnvriend

import slick.driver.PostgresDriver.api._

class PlainSqlTest extends TestSpec {
  import CoffeeRepository._

  /**
   * The database connection is opened in the usual way. All Plain SQL queries result in `DBIOActions`
   * that can be composed and run like any other action.
   *
   * Plain SQL queries in Slick are built via string interpolation using the `sql`, `sqlu` and `tsql`
   * interpolators which return  a `SQLActionBuilder`. They are available through the standard api._
   * imported from a Slick driver like eg: import slick.driver.PostgresDriver.api._`
   *
   * The following string interpolators are available:
   *
   *  - sql: The sql interpolator which returns a result set produced by a statement. The interpolator by itself
   *         does not produce a DBIO value. It needs to be followed by a call to .as to define the row type.
   *  - sqlu: The sqlu interpolator is used for DML statements which produce a row count instead of a result set.
   *          Therefore they are of type DBIO[Int].
   *  - tsql: Builds an invoker for a statement with computed types
   */

  "Plain SQL on Coffee" should "select all coffee" in {
    // be sure to 'escape' the table and field names with double quotes
    val actions: DBIO[Seq[Coffee]] = sql"""SELECT * FROM "COFFEES" ORDER BY "COF_NAME" ASC""".as[Coffee]
    db.run(actions).futureValue shouldBe List(
      Coffee("Colombian", 101, 7.99, 0, 0),
      Coffee("Colombian_Decaf", 101, 10.99, 0, 0),
      Coffee("Espresso", 150, 11.99, 0, 0),
      Coffee("French_Roast", 49, 8.99, 0, 0),
      Coffee("French_Roast_Decaf", 49, 9.99, 0, 0)
    )
  }
}