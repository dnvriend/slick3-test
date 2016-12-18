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

import com.github.dnvriend.CoffeeRepository.CoffeeTableRow
import com.github.dnvriend.TestSpec

class BatchInsertTest extends TestSpec {
  import profile.api._
  import coffeeRepository._

  "Inserting Coffee using batch insert" should "insert multiple rows" in {
    Given("An empty coffees table")
    db.run(CoffeeTable.delete).futureValue
    db.run(CoffeeTable.length.result).futureValue shouldBe 0

    val numberOfRecords = 45005

    When(s"$numberOfRecords coffee object are created and batch inserted 100 a time")
    akka.stream.scaladsl.Source.fromIterator(() => Iterator from 1)
      .take(numberOfRecords)
      .map(i => CoffeeTableRow(s"Coffee-$i", 101, i.toDouble, i, i))
      .grouped(1000)
      .mapAsyncUnordered(8)(seqOfCoffees => db.run(CoffeeTable ++= seqOfCoffees))
      .runWith(akka.stream.scaladsl.Sink.ignore)
      .futureValue

    db.run(CoffeeTable.length.result).futureValue shouldBe numberOfRecords
  }
}

