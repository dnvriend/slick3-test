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

import slick.driver.PostgresDriver.api._

class UnionTest extends TestSpec {

  import CoffeeRepository._

  /**
   * Two queries can be concatenated with the ++ (or unionAll) and union operators if they have compatible types.
   *
   * Unlike union which filters out duplicate values, ++ simply concatenates the results of the individual
   * queries, which is usually more efficient.
   */

  val q1 = coffees.filter(_.price < 8.0)
  val q2 = coffees.filter(_.price < 8.0)
  val q3 = coffees.filter(_.price > 9.0)

  "Union" should "concatenate the result of queries" in {
    db.run((q1 ++ q2 ++ q3).sortBy(_.price).result).futureValue shouldBe List(
      Coffee("Colombian", 101, 7.99, 0, 0),
      Coffee("Colombian", 101, 7.99, 0, 0),
      Coffee("French_Roast_Decaf", 49, 9.99, 0, 0),
      Coffee("Colombian_Decaf", 101, 10.99, 0, 0),
      Coffee("Espresso", 150, 11.99, 0, 0)
    )
  }

  it should "filter out duplicate values" in {
    db.run((q1 union q2 union q3).sortBy(_.price).result).futureValue shouldBe List(
      Coffee("Colombian", 101, 7.99, 0, 0),
      Coffee("French_Roast_Decaf", 49, 9.99, 0, 0),
      Coffee("Colombian_Decaf", 101, 10.99, 0, 0),
      Coffee("Espresso", 150, 11.99, 0, 0)
    )
  }
}
