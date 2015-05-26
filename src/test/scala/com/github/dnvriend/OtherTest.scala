package com.github.dnvriend

trait OtherTest extends TestSpec {
  "OtherTest" should "do some test" in {
    CoffeeRepository.coffees.toObservable.toList.toBlocking.first() should not be 'empty
  }
}
