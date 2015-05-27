package com.github.dnvriend

import slick.driver.PostgresDriver.api._

class QueryCoffeesTest extends TestSpec {

  import CoffeeRepository._

  "Coffees" should "determine whether there are records in the table" in {
    // SELECT COUNT(1) FROM COFFEES
    db.run(coffees.exists.result).futureValue shouldBe true
  }

  it should "count records" in {
    // SELECT EXISTS(SELECT * FROM COFFEES)
    db.run(coffees.length.result).futureValue shouldBe 5
  }

  it should "add a record and query the new record" in {
    // INSERT a coffee AND SELECT it
    db.run(coffees += Coffee("Foo", 49, 12.99, 0, 0))
      .flatMap(_ => db.run(coffees.filter(_.name === "Foo").result))
      .futureValue.head shouldBe Coffee("Foo", 49, 12.99, 0, 0)
  }

  it should "delete a coffee" in {
    db.run(coffees.filter(_.name === "Colombian").result).futureValue should not be 'empty
    db.run(coffees.filter(_.name === "Colombian").delete).futureValue shouldBe 1
    db.run(coffees.filter(_.name === "Colombian").result).futureValue shouldBe 'empty
  }

  it should "be sorted asc" in {
    // SELECT * FROM COFFEES ORDER BY NAME ASC
    db.run(coffees.sortBy(_.name).result).futureValue shouldBe
      List(
        Coffee("Colombian", 101, 7.99, 0, 0),
        Coffee("Colombian_Decaf", 101, 10.99, 0, 0),
        Coffee("Espresso", 150, 11.99, 0, 0),
        Coffee("French_Roast", 49, 8.99, 0, 0),
        Coffee("French_Roast_Decaf", 49, 9.99, 0, 0)
      )
  }

  it should "be sorted desc" in {
    // SELECT * FROM COFFEES ORDER BY NAME DESC
    db.run(coffees.sortBy(_.name.desc).result).futureValue shouldBe
      List(
        Coffee("French_Roast_Decaf", 49, 9.99, 0, 0),
        Coffee("French_Roast", 49, 8.99, 0, 0),
        Coffee("Espresso", 150, 11.99, 0, 0),
        Coffee("Colombian_Decaf", 101, 10.99, 0, 0),
        Coffee("Colombian", 101, 7.99, 0, 0)
      )
  }

  it should "sort on price" in {
    // SELECT * FROM COFFEES ORDER BY PRICE DESC
    db.run(coffees.sortBy(_.price.desc).result).futureValue shouldBe
      List(
        Coffee("Espresso", 150, 11.99, 0, 0),
        Coffee("Colombian_Decaf", 101, 10.99, 0, 0),
        Coffee("French_Roast_Decaf", 49, 9.99, 0, 0),
        Coffee("French_Roast", 49, 8.99, 0, 0),
        Coffee("Colombian", 101, 7.99, 0, 0)
      )
  }

  it should "sort on two fields" in {
    // SELECT * FROM COFFEES ORDER BY NAME ASC, PRICE DESC
    db.run(coffees.sortBy(c => (c.name.desc, c.price.desc)).result).futureValue shouldBe
      List(
        Coffee("French_Roast_Decaf", 49, 9.99, 0, 0),
        Coffee("French_Roast", 49, 8.99, 0, 0),
        Coffee("Espresso", 150, 11.99, 0, 0),
        Coffee("Colombian_Decaf", 101, 10.99, 0, 0),
        Coffee("Colombian", 101, 7.99, 0, 0)
      )
  }

  it should "get the max price" in {
    db.run(coffees.map(_.price).max.result).futureValue.value shouldBe 11.99
  }

  it should "get the min price" in {
    db.run(coffees.map(_.price).min.result).futureValue.value shouldBe 7.99
  }

  it should "get the average price" in {
    db.run(coffees.map(_.price).avg.result).futureValue.value shouldBe 9.99
  }
}
