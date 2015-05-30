package com.github.dnvriend

import slick.driver.PostgresDriver.api._

class UpdateTest extends TestSpec {

  import UsersRepository._

  "UpdateTest" should "update a user" in {
    val userQuery = users.filter(u => u.first === "Steve" && u.last === "Jobs")
    db.run(userQuery.result.head).flatMap { user =>
      db.run(userQuery.update(user.copy(first = "APPLE", last = "ROCKS")))
    }.futureValue shouldBe 1

    db.run(users.sortBy(_.id).result).futureValue shouldBe List(
      User(Some(1), "Bill", "Gates"),
      User(Some(2), "Steve", "Balmer"),
      User(Some(3), "APPLE", "ROCKS"),
      User(Some(4), "Steve", "Wozniak")
    )
  }
}
