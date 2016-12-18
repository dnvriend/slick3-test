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

import akka.stream.scaladsl.{ Sink, Source }
import com.github.dnvriend.CoffeeRepository.CoffeeTableRow
import com.github.dnvriend.TestSpec

class CoffeeStreamTest extends TestSpec {
  import profile.api._
  import coffeeRepository._

  "DatabasePublisher" should "stream coffee" in {
    Source.fromPublisher(coffeeStream).runWith(Sink.seq).futureValue should not be 'empty
  }

  it should "be converted to an RxObservable" in {
    val xs = Source.fromPublisher(coffeeStream).runWith(Sink.seq).futureValue
    xs.sortBy(_.name) shouldBe
      List(
        CoffeeTableRow("Colombian", 101, 7.99, 0, 0),
        CoffeeTableRow("Colombian_Decaf", 101, 10.99, 0, 0),
        CoffeeTableRow("Espresso", 150, 11.99, 0, 0),
        CoffeeTableRow("French_Roast", 49, 8.99, 0, 0),
        CoffeeTableRow("French_Roast_Decaf", 49, 9.99, 0, 0)
      )
  }
}
