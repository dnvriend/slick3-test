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

import com.github.dnvriend.CoffeeRepository._
import com.github.dnvriend.PostgresCoffeeRepository._
import com.github.dnvriend.PostgresUserRepository._
import com.github.dnvriend.PostgresUserRepository.profile.api._
import com.github.dnvriend.UserRepository._

class InsertTest extends TestSpec {

  /**
   * Inserts are done based on a projection of columns from a single table. When you use the table directly,
   * the insert is performed against its * projection. Omitting some of a table’s columns when inserting causes
   * the database to use the default values specified in the table definition, or a type-specific default in case
   * no explicit default was given.
   */

  "Inserting Coffee" should "insert a single row" in {
    db.run(CoffeeTable.length.result).futureValue shouldBe 5
    //  += gives you a count of the number of affected rows (which will usually be 1)
    db.run(CoffeeTable += CoffeeTableRow("Colombian_Extra_Decaf", 101, 7.99, 0, 0)).futureValue shouldBe 1
    db.run(CoffeeTable.length.result).futureValue shouldBe 6
  }

  it should "insert multiple rows" in {
    val insertAction = CoffeeTable ++= Seq(
      CoffeeTableRow("Italic_Roast", 49, 8.99, 0, 0),
      CoffeeTableRow("Spanish_Espresso", 150, 9.99, 0, 0)
    )

    db.run(CoffeeTable.length.result).futureValue shouldBe 5
    // ++= gives an accumulated count in an Option (which can be None if the database system
    // does not provide counts for all rows)
    db.run(insertAction).futureValue.value shouldBe 2
    db.run(CoffeeTable.length.result).futureValue shouldBe 7
  }

  it should "get the auto-generated primary key for a user" in {
    val userIdAction = (UserTable returning UserTable.map(_.id)) += UserTableRow(None, "Stefan", "Zeiger")
    // the returning method where you specify the columns to be returned
    // (as a single value or tuple from += and a Seq of such values from ++=)
    val id = db.run(userIdAction).futureValue
    id shouldBe an[java.lang.Integer]
    db.run(UserTable.filter(_.id === id).result.headOption).futureValue shouldBe 'defined
  }

  it should "map the auto-generated primary key for a user into the case class" in {
    // You can follow the returning method with the into method to map the inserted values and
    // the generated keys (specified in returning) to a desired value. Here is an example of
    // using this feature to return an object with an updated id.
    val userWithIdAction =
      (UserTable returning UserTable.map(_.id)
        into ((user, id) ⇒ user.copy(id = Option(id)))
      ) += UserTableRow(None, "Stefan", "Zeiger") // don't you like functional style :)

    db.run(userWithIdAction).futureValue should matchPattern {
      case UserTableRow(Some(id), "Stefan", "Zeiger") if id > 4 ⇒
    }
  }
}
