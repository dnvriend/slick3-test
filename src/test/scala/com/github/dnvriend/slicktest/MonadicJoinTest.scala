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

class MonadicJoinTest extends TestSpec {
  import profile.api._
  import coffeeRepository._

  /**
   * Monadic joins are created with flatMap. They are theoretically more powerful
   * than applicative joins because the right-hand side may depend on the left-hand side.
   * However, this is not possible in standard SQL, so Slick has to compile them down to
   * applicative joins, which is possible in many useful cases but not in all of them
   * (and there are cases where it is possible in theory but Slick cannot perform the
   * required transformation yet).
   *
   * If a monadic join cannot be properly translated, it will fail at runtime.
   */

  /**
   * A cross-join is created with a flatMap operation on a Query (i.e. by introducing
   * more than one generator in a for-comprehension):
   */

  ignore should "crossJoin" in {
    val monadicCrossJoin = for {
      c <- CoffeeTable
      s <- SupplierTable
    } yield (c.name, s.name)

    db.run(monadicCrossJoin.result).futureValue shouldBe List(
      ("Colombian", "Superior Coffee"),
      ("Colombian", "Acme, Inc."),
      ("Colombian", "The High Ground"),
      ("Colombian_Decaf", "Superior Coffee"),
      ("Colombian_Decaf", "Acme, Inc."),
      ("Colombian_Decaf", "The High Ground"),
      ("Espresso", "Superior Coffee"),
      ("Espresso,Acme", "Inc."),
      ("Espresso", "The High Ground"),
      ("French_Roast", "Superior Coffee"),
      ("French_Roast", "Acme, Inc."),
      ("French_Roast", "The High Ground"),
      ("French_Roast_Decaf", "Superior Coffee"),
      ("French_Roast_Decaf", "Acme, Inc."),
      ("French_Roast_Decaf", "The High Ground")
    )
  }

  it should "innerJoin" in {
    // If you add a filter expression, it becomes an inner join:
    val monadicInnerJoin = for {
      c <- CoffeeTable
      s <- SupplierTable if c.supID === s.id
    } yield (c.name, s.name)

    db.run(monadicInnerJoin.result).futureValue shouldBe List(
      ("Colombian", "Acme, Inc."),
      ("French_Roast", "Superior Coffee"),
      ("Espresso", "The High Ground"),
      ("Colombian_Decaf", "Acme, Inc."),
      ("French_Roast_Decaf", "Superior Coffee")
    )
  }
}
