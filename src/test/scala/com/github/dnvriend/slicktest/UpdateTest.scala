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

import com.github.dnvriend.TestSpec
import com.github.dnvriend.UserRepository._

class UpdateTest extends TestSpec {
  import profile.api._
  import userRepository._

  "UpdateTest" should "update a user" in {
    val userQuery = UserTable.filter(u => u.first === "Steve" && u.last === "Jobs")
    db.run(userQuery.result.head).flatMap { user =>
      db.run(userQuery.update(user.copy(first = "APPLE", last = "ROCKS")))
    }.futureValue shouldBe 1

    db.run(UserTable.sortBy(_.id).result).futureValue shouldBe List(
      UserTableRow(Some(1), "Bill", "Gates"),
      UserTableRow(Some(2), "Steve", "Balmer"),
      UserTableRow(Some(3), "APPLE", "ROCKS"),
      UserTableRow(Some(4), "Steve", "Wozniak")
    )
  }

  it should "update a user using for expressions" in {

    val userQuery = for (u <- UserTable if u.first === "Steve" && u.last === "Jobs") yield u

    val nrUpdated = for {
      user <- db.run(userQuery.result.head)
      nrUpdated <- db.run(userQuery.update(user.copy(first = "APPLE", last = "ROCKS")))
    } yield nrUpdated

    nrUpdated.futureValue shouldBe 1

    db.run(UserTable.sortBy(_.id).result).futureValue shouldBe List(
      UserTableRow(Some(1), "Bill", "Gates"),
      UserTableRow(Some(2), "Steve", "Balmer"),
      UserTableRow(Some(3), "APPLE", "ROCKS"),
      UserTableRow(Some(4), "Steve", "Wozniak")
    )
  }

  it should "update a single field in a record" in {
    db.run(UserTable.filter(_.id === 2).map(_.last).update("Wunderbar")).futureValue shouldBe 1
    db.run(UserTable.sortBy(_.id).result).futureValue shouldBe List(
      UserTableRow(Some(1), "Bill", "Gates"),
      UserTableRow(Some(2), "Steve", "Wunderbar"),
      UserTableRow(Some(3), "Steve", "Jobs"),
      UserTableRow(Some(4), "Steve", "Wozniak")
    )
  }

  it should "update multiple fields in a single record" in {
    db.run(UserTable.filter(_.id === 3).map(row => (row.first, row.last)).update(("HELLO", "WORLD"))).futureValue shouldBe 1
    db.run(UserTable.sortBy(_.id).result).futureValue shouldBe List(
      UserTableRow(Some(1), "Bill", "Gates"),
      UserTableRow(Some(2), "Steve", "Balmer"),
      UserTableRow(Some(3), "HELLO", "WORLD"),
      UserTableRow(Some(4), "Steve", "Wozniak")
    )
  }
}
