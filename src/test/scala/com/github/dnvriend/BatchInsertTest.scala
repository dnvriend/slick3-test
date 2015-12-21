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

import akka.stream.scaladsl.Source
import slick.driver.PostgresDriver.api._

class BatchInsertTest extends TestSpec {
  import CoffeeRepository._

  "Inserting Coffee using batch insert" should "insert multiple rows" in {
    Given("An empty coffees table")
    db.run(coffees.delete).futureValue
    db.run(coffees.length.result).futureValue shouldBe 0

    val numberOfRecords = 45005

    When(s"$numberOfRecords coffee object are created and batch inserted 100 a time")
    val batch = Source(() ⇒ Iterator from 1)
      .take(numberOfRecords)
      .map(i ⇒ Coffee(s"Coffee-$i", 101, i.toDouble, i, i))
      .grouped(100)
      .mapAsync(1)(seqOfCoffees ⇒ db.run(coffees ++= seqOfCoffees))
      .runFold(0L) { (c, result) ⇒ c + result.getOrElse(0) }
      .futureValue shouldBe numberOfRecords

    Then(s"$numberOfRecords coffee entries should be persisted")
    db.run(coffees.length.result).futureValue shouldBe numberOfRecords
  }
}

