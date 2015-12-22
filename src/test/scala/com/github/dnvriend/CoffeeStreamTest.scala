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
import com.github.dnvriend.CoffeeRepository.Coffee
import scala.collection.JavaConversions._

class CoffeeStreamTest extends TestSpec {

  "DatabasePublisher" should "stream coffee" in {
    Source.fromPublisher(CoffeeRepository.coffeeStream).runFold(Seq.empty[Coffee]) {
      case (seq, coffee) ⇒ seq :+ coffee
    }.futureValue should not be 'empty
  }

  it should "be converted to an RxObservable" in {
    CoffeeRepository.coffeeStream.toObservable.toList.toBlocking.single() should not be 'empty
    CoffeeRepository.coffeeStream.toObservable.toList.toBlocking.single().sortBy(_.name) shouldBe
      List(
        Coffee("Colombian", 101, 7.99, 0, 0),
        Coffee("Colombian_Decaf", 101, 10.99, 0, 0),
        Coffee("Espresso", 150, 11.99, 0, 0),
        Coffee("French_Roast", 49, 8.99, 0, 0),
        Coffee("French_Roast_Decaf", 49, 9.99, 0, 0)
      )
  }
}
