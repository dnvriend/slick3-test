/*
 * Copyright 2015 Dennis Vriend
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.dnvriend.slicktest

import com.github.dnvriend.CoffeeRepository.CoffeeTableRow
import com.github.dnvriend.TestSpec

class PlainSqlTest extends TestSpec {
  import profile.api._
  import coffeeRepository._

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
    val actions: DBIO[Seq[CoffeeTableRow]] = sql"""SELECT * FROM "COFFEES" ORDER BY "COF_NAME" ASC""".as[CoffeeTableRow]
    db.run(actions).futureValue shouldBe List(
      CoffeeTableRow("Colombian", 101, 7.99, 0, 0),
      CoffeeTableRow("Colombian_Decaf", 101, 10.99, 0, 0),
      CoffeeTableRow("Espresso", 150, 11.99, 0, 0),
      CoffeeTableRow("French_Roast", 49, 8.99, 0, 0),
      CoffeeTableRow("French_Roast_Decaf", 49, 9.99, 0, 0)
    )
  }

  it should "select only coffee names where the price < 10" in {
    val limit = 10.0
    val actions = sql"""select "COF_NAME" from "COFFEES" where "PRICE" < $limit""".as[String]
    db.run(actions).futureValue shouldBe
      List("Colombian", "French_Roast", "French_Roast_Decaf")

  }
}
