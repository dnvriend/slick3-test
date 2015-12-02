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

import slick.driver.H2Driver.api._

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
