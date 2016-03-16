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

import CoffeeRepository._
import com.github.dnvriend.PostgresCoffeeRepository._
import com.github.dnvriend.PostgresCoffeeRepository.profile.api._

class QueryCoffeesTest extends TestSpec {

  /**
   * A Query can be converted into an Action by calling its `result` method.
   * The Action can then be executed directly in a streaming or fully materialized way,
   * or composed further with other Actions.
   */

  "Coffees" should "determine whether there are records in the table" in {
    // SELECT COUNT(1) FROM COFFEES
    db.run(CoffeeTable.exists.result).futureValue shouldBe true
  }

  it should "count records" in {
    // SELECT EXISTS(SELECT * FROM COFFEES)
    db.run(CoffeeTable.length.result).futureValue shouldBe 5
  }

  it should "get only a single result value" in {
    db.run(CoffeeTable.result.head).futureValue shouldBe
      CoffeeTableRow("Colombian", 101, 7.99, 0, 0)
  }

  it should "get only a single result option value" in {
    db.run(CoffeeTable.result.headOption).futureValue shouldBe
      Option(CoffeeTableRow("Colombian", 101, 7.99, 0, 0))
  }

  it should "add a record and query the new record" in {
    // INSERT a coffee AND SELECT it
    db.run(CoffeeTable += CoffeeTableRow("Foo", 49, 12.99, 0, 0))
      .flatMap(_ ⇒ db.run(CoffeeTable.filter(_.name === "Foo").result))
      .futureValue.head shouldBe CoffeeTableRow("Foo", 49, 12.99, 0, 0)
  }

  it should "delete a coffee" in {
    db.run(CoffeeTable.filter(_.name === "Colombian").result).futureValue should not be 'empty
    db.run(CoffeeTable.filter(_.name === "Colombian").delete).futureValue shouldBe 1
    db.run(CoffeeTable.filter(_.name === "Colombian").result).futureValue shouldBe 'empty
  }

  it should "be sorted asc" in {
    // SELECT * FROM COFFEES ORDER BY NAME ASC
    db.run(CoffeeTable.sortBy(_.name).result).futureValue shouldBe
      List(
        CoffeeTableRow("Colombian", 101, 7.99, 0, 0),
        CoffeeTableRow("Colombian_Decaf", 101, 10.99, 0, 0),
        CoffeeTableRow("Espresso", 150, 11.99, 0, 0),
        CoffeeTableRow("French_Roast", 49, 8.99, 0, 0),
        CoffeeTableRow("French_Roast_Decaf", 49, 9.99, 0, 0)
      )
  }

  it should "be sorted desc" in {
    // SELECT * FROM COFFEES ORDER BY NAME DESC
    db.run(CoffeeTable.sortBy(_.name.desc).result).futureValue shouldBe
      List(
        CoffeeTableRow("French_Roast_Decaf", 49, 9.99, 0, 0),
        CoffeeTableRow("French_Roast", 49, 8.99, 0, 0),
        CoffeeTableRow("Espresso", 150, 11.99, 0, 0),
        CoffeeTableRow("Colombian_Decaf", 101, 10.99, 0, 0),
        CoffeeTableRow("Colombian", 101, 7.99, 0, 0)
      )
  }

  it should "sort on price" in {
    // SELECT * FROM COFFEES ORDER BY PRICE DESC
    db.run(CoffeeTable.sortBy(_.price.desc).result).futureValue shouldBe
      List(
        CoffeeTableRow("Espresso", 150, 11.99, 0, 0),
        CoffeeTableRow("Colombian_Decaf", 101, 10.99, 0, 0),
        CoffeeTableRow("French_Roast_Decaf", 49, 9.99, 0, 0),
        CoffeeTableRow("French_Roast", 49, 8.99, 0, 0),
        CoffeeTableRow("Colombian", 101, 7.99, 0, 0)
      )
  }

  it should "sort on two fields" in {
    // SELECT * FROM COFFEES ORDER BY NAME ASC, PRICE DESC
    db.run(CoffeeTable.sortBy(c ⇒ (c.name.desc, c.price.desc)).result).futureValue shouldBe
      List(
        CoffeeTableRow("French_Roast_Decaf", 49, 9.99, 0, 0),
        CoffeeTableRow("French_Roast", 49, 8.99, 0, 0),
        CoffeeTableRow("Espresso", 150, 11.99, 0, 0),
        CoffeeTableRow("Colombian_Decaf", 101, 10.99, 0, 0),
        CoffeeTableRow("Colombian", 101, 7.99, 0, 0)
      )
  }

  it should "query for coffee names with a price less than 10, sorted by name" in {
    db.run(CoffeeTable.filter(_.price < 10.0).sortBy(_.name).map(_.name).result).futureValue shouldBe
      List("Colombian", "French_Roast", "French_Roast_Decaf")
  }

  it should "get the max price" in {
    db.run(CoffeeTable.map(_.price).max.result).futureValue.value shouldBe 11.99
  }

  it should "get the min price" in {
    db.run(CoffeeTable.map(_.price).min.result).futureValue.value shouldBe 7.99
  }

  it should "get the average price" in {
    db.run(CoffeeTable.map(_.price).avg.result).futureValue.value shouldBe 9.99
  }

  it should "get the sum price" in {
    db.run(CoffeeTable.map(_.price).sum.result).futureValue.value shouldBe 49.95
  }

  it should "get the name field only" in {
    // SELECT NAME FROM COFFEES
    db.run(CoffeeTable.map(_.name).result).futureValue shouldBe
      List("Colombian", "French_Roast", "Espresso", "Colombian_Decaf", "French_Roast_Decaf")
  }

  it should "get coffees sorted by name, null values first" in {
    db.run(CoffeeTable.sortBy(_.name.desc.nullsFirst).result).futureValue shouldBe
      List(
        CoffeeTableRow("French_Roast_Decaf", 49, 9.99, 0, 0),
        CoffeeTableRow("French_Roast", 49, 8.99, 0, 0),
        CoffeeTableRow("Espresso", 150, 11.99, 0, 0),
        CoffeeTableRow("Colombian_Decaf", 101, 10.99, 0, 0),
        CoffeeTableRow("Colombian", 101, 7.99, 0, 0)
      )
  }

  it should "get a coffee with limit and offset" in {
    // select * from coffees limit 1 offset 2
    db.run(CoffeeTable.drop(2).take(1).result).futureValue should not be 'empty
  }

  it should "query for coffees using a criteria" in {
    val criteriaColombian: Option[String] = Option("Colombian")
    val criteriaEspresso: Option[String] = Option("Espresso")
    val criteriaRoast: Option[String] = None

    val q = CoffeeTable.filter { coffee ⇒
      List(
        criteriaColombian.map(coffee.name === _),
        criteriaEspresso.map(coffee.name === _),
        criteriaRoast.map(coffee.name === _) // not a condition as `criteriaRoast` evaluates to `None`
      ).collect({ case Some(criteria) ⇒ criteria }).reduceLeftOption(_ || _).getOrElse(slick.lifted.LiteralColumn[Boolean](true))
    }

    //    q.result.statements.foreach(println)

    db.run(q.result).futureValue shouldBe List(
      CoffeeTableRow("Colombian", 101, 7.99, 0, 0),
      CoffeeTableRow("Espresso", 150, 11.99, 0, 0)
    )
  }
}
