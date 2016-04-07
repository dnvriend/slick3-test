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

import scala.concurrent.ExecutionContext.Implicits.global
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import slick.driver.PostgresDriver.api._

object CoffeeStream extends App {
  implicit val db: Database = Database.forConfig("mydb")
  val log: Logger = LoggerFactory.getLogger(this.getClass)

  val action = PostgresCoffeeRepository.initialize
    .flatMap(_ ⇒ PostgresPersonRepository.initialize)
    .flatMap(_ ⇒ PostgresUserRepository.initialize)
    .flatMap(_ ⇒ PostgresCoffeeRepository.coffeeStream.foreach(println(_)))
  action.onComplete { _ ⇒ db.close }
  action.onFailure { case t: Throwable ⇒ log.error("Could not initialize the database", t) }
}
