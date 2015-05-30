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

  it should "update a user using for expressions" in {

    val userQuery = for(u <- users if u.first === "Steve" && u.last === "Jobs") yield u
    
    val nrUpdated = for {
      user <- db.run(userQuery.result.head)
      nrUpdated <- db.run(userQuery.update(user.copy(first = "APPLE", last = "ROCKS")))
    } yield nrUpdated
    
    nrUpdated.futureValue shouldBe 1

    db.run(users.sortBy(_.id).result).futureValue shouldBe List(
      User(Some(1), "Bill", "Gates"),
      User(Some(2), "Steve", "Balmer"),
      User(Some(3), "APPLE", "ROCKS"),
      User(Some(4), "Steve", "Wozniak")
    )
  }
}
