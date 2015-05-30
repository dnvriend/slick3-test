package com.github.dnvriend

import slick.driver.PostgresDriver.api._

class GroupingTest extends TestSpec {

  import CoffeeRepository._

  /**
   * Grouping is done with the groupBy method. It has the same semantics as for Scala collections.
   *
   * The intermediate query q contains nested values of type Query. These would turn into nested
   * collections when executing the query, which is not supported at the moment. Therefore it is
   * necessary to flatten the nested queries immediately by aggregating their values (or individual columns)
   * as done in q2.
   */

  "GroupingTest" should "group results" in {
    val q = (for {
      c <- coffees
      s <- c.supplier
    } yield (c, s)).groupBy(_._1.supID)

    val q2 = q.map { case (supID, css) =>
      (supID, css.length, css.map(_._1.price).avg)
    }

    db.run(q2.result).futureValue shouldBe List(
      (101,2,Some(9.49)),
      (49,2,Some(9.49)),
      (150,1,Some(11.99))
    )
  }
}
