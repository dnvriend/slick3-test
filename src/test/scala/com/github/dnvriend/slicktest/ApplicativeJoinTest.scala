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
import org.scalatest.Ignore

@Ignore
class ApplicativeJoinTest extends TestSpec {
  import profile.api._
  import coffeeRepository._
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
      (c, s) <- CoffeeTable join SupplierTable
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
      (c, s) <- CoffeeTable join SupplierTable on (_.supID === _.id)
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
      (c, s) <- CoffeeTable joinLeft SupplierTable on (_.supID === _.id)
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
      (c, s) <- CoffeeTable joinRight SupplierTable on (_.supID === _.id)
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
      (c, s) <- CoffeeTable joinFull SupplierTable on (_.supID === _.id)
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
