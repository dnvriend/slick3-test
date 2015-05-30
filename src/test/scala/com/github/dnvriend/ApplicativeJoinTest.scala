package com.github.dnvriend

import slick.driver.PostgresDriver.api._

class ApplicativeJoinTest extends TestSpec {

  import CoffeeRepository._

  /**
   * Joins are used to combine two different tables or queries into a single query.
   * There are two different ways of writing joins: Applicative and monadic.
   */

  /**
   * Applicative joins are performed by calling a method that joins two queries into a
   * single query of a tuple of the individual results. They have the same restrictions
   * as joins in SQL, i.e. the right-hand side may not depend on the left-hand side.
   * This is enforced naturally through Scala’s scoping rules.
   */

  "Applicative Joins" should "crossjoin" in {
    val crossJoin = for {
      (c, s) <- coffees join suppliers
    } yield (c.name, s.name)

    db.run(crossJoin.result).futureValue shouldBe List(
      ("Colombian", "Acme, Inc."),
      ("Colombian", "Superior Coffee"),
      ("Colombian", "The High Ground"),
      ("French_Roast", "Acme, Inc."),
      ("French_Roast", "Superior Coffee"),
      ("French_Roast", "The High Ground"),
      ("Espresso", "Acme, Inc."),
      ("Espresso", "Superior Coffee"),
      ("Espresso", "The High Ground"),
      ("Colombian_Decaf", "Acme, Inc."),
      ("Colombian_Decaf", "Superior Coffee"),
      ("Colombian_Decaf", "The High Ground"),
      ("French_Roast_Decaf", "Acme, Inc."),
      ("French_Roast_Decaf", "Superior Coffee"),
      ("French_Roast_Decaf", "The High Ground")
    )
  }

  it should "innerJoin" in {
    val innerJoin = for {
      (c, s) <- coffees join suppliers on (_.supID === _.id)
    } yield (c.name, s.name)

    db.run(innerJoin.result).futureValue shouldBe List(
      ("Colombian", "Acme, Inc."),
      ("French_Roast", "Superior Coffee"),
      ("Espresso", "The High Ground"),
      ("Colombian_Decaf", "Acme, Inc."),
      ("French_Roast_Decaf", "Superior Coffee")
    )
  }

  /**
   * Note the use of map in the yield clauses of the outer joins. Since these joins can introduce
   * additional NULL values (on the right-hand side for a left outer join, on the left-hand sides
   * for a right outer join, and on both sides for a full outer join), the respective sides of
   * the join are wrapped in an Option (with None representing a row that was not matched).
   */

  it should "leftOuterJoin" in {
    val leftOuterJoin = for {
      (c, s) <- coffees joinLeft suppliers on (_.supID === _.id)
    } yield (c.name, s.map(_.name))

    db.run(leftOuterJoin.result).futureValue shouldBe List(
      ("Colombian", Some("Acme, Inc.")),
      ("French_Roast", Some("Superior Coffee")),
      ("Espresso", Some("The High Ground")),
      ("Colombian_Decaf", Some("Acme, Inc.")),
      ("French_Roast_Decaf", Some("Superior Coffee"))
    )
  }

  it should "rightOuterJoin" in {
    val rightOuterJoin = for {
      (c, s) <- coffees joinRight suppliers on (_.supID === _.id)
    } yield (c.map(_.name), s.name)

    db.run(rightOuterJoin.result).futureValue shouldBe List(
      (Some("Colombian"), "Acme, Inc."),
      (Some("French_Roast"), "Superior Coffee"),
      (Some("Espresso"), "The High Ground"),
      (Some("Colombian_Decaf"), "Acme, Inc."),
      (Some("French_Roast_Decaf"), "Superior Coffee")
    )
  }

  it should "fullOuterJoin" in {
    val fullOuterJoin = for {
      (c, s) <- coffees joinFull suppliers on (_.supID === _.id)
    } yield (c.map(_.name), s.map(_.name))

    db.run(fullOuterJoin.result).futureValue shouldBe List(
      (Some("Colombian"), Some("Acme, Inc.")),
      (Some("French_Roast"), Some("Superior Coffee")),
      (Some("Espresso"), Some("The High Ground")),
      (Some("Colombian_Decaf"), Some("Acme, Inc.")),
      (Some("French_Roast_Decaf"), Some("Superior Coffee"))
    )
  }
}
