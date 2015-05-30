package com.github.dnvriend

import slick.driver.PostgresDriver.api._

class DeleteTest extends TestSpec {

  import CoffeeRepository._

  /**
   * Deleting works very similarly to querying. You write a query which selects the rows to delete and then
   * get an Action by calling the delete method on it.
   *
   * A query for deleting must only select from a single table. Any projection is ignored
   * (it always deletes full rows).
   */

  "Deleting" should "delete all coffees" in {
    db.run(coffees.delete).futureValue shouldBe 5
    db.run(coffees.length.result).futureValue shouldBe 0
    db.run(coffees.exists.result).futureValue shouldBe false
    db.run(coffees.result).futureValue shouldBe 'empty
  }

  it should "delete a single row" in {
    db.run(coffees.filter(_.supID === 150).delete).futureValue shouldBe 1
    db.run(coffees.length.result).futureValue shouldBe 4
  }

  it should "delete multiple rows" in {
    db.run(coffees.filter(_.supID === 49).delete).futureValue shouldBe 2
    db.run(coffees.length.result).futureValue shouldBe 3
  }
}
