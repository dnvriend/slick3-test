package com.github.dnvriend

import akka.stream.scaladsl.Source
import com.github.dnvriend.CoffeeRepository.Coffee
import scala.collection.JavaConversions._

class CoffeeStreamTest extends TestSpec {
  "DatabasePublisher" should "stream coffee" in {
    Source(CoffeeRepository.coffeeStream).runFold(Seq.empty[Coffee]) {
      case (seq, coffee) => seq :+ coffee
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
