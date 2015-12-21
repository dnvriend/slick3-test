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

package com.github.dnvriend

import slick.driver.PostgresDriver.api._

class InsertTest extends TestSpec {

  import CoffeeRepository._
  import UsersRepository._

  /**
   * Inserts are done based on a projection of columns from a single table. When you use the table directly,
   * the insert is performed against its * projection. Omitting some of a table’s columns when inserting causes
   * the database to use the default values specified in the table definition, or a type-specific default in case
   * no explicit default was given.
   */

  "Inserting Coffee" should "insert a single row" in {
    db.run(coffees.length.result).futureValue shouldBe 5
    //  += gives you a count of the number of affected rows (which will usually be 1)
    db.run(coffees += Coffee("Colombian_Extra_Decaf", 101, 7.99, 0, 0)).futureValue shouldBe 1
    db.run(coffees.length.result).futureValue shouldBe 6
  }

  it should "insert multiple rows" in {
    val insertAction = coffees ++= Seq(
      Coffee("Italic_Roast", 49, 8.99, 0, 0),
      Coffee("Spanish_Espresso", 150, 9.99, 0, 0)
    )

    db.run(coffees.length.result).futureValue shouldBe 5
    // ++= gives an accumulated count in an Option (which can be None if the database system
    // does not provide counts for all rows)
    db.run(insertAction).futureValue.value shouldBe 2
    db.run(coffees.length.result).futureValue shouldBe 7
  }

  it should "get the auto-generated primary key for a user" in {
    val userIdAction = (users returning users.map(_.id)) += User(None, "Stefan", "Zeiger")
    // the returning method where you specify the columns to be returned
    // (as a single value or tuple from += and a Seq of such values from ++=)
    db.run(userIdAction).futureValue shouldBe an[java.lang.Integer]
  }

  it should "map the auto-generated primary key for a user into the case class" in {
    // You can follow the returning method with the into method to map the inserted values and
    // the generated keys (specified in returning) to a desired value. Here is an example of
    // using this feature to return an object with an updated id.
    val userWithIdAction =
      (users returning users.map(_.id)
        into ((user, id) ⇒ user.copy(id = Option(id)))
      ) += User(None, "Stefan", "Zeiger") // don't you like functional style :)

    db.run(userWithIdAction).futureValue should matchPattern {
      case User(Some(id), "Stefan", "Zeiger") if id > 4 ⇒
    }
  }
}
