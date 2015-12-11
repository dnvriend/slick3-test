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

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.scalatest.concurrent.ScalaFutures
import org.scalatest._
import org.scalatest.exceptions._
import spray.json.DefaultJsonProtocol
import slick.driver.PostgresDriver.api._

import scala.concurrent.duration._
import scala.concurrent.{ ExecutionContext, Future }
import scala.util.Try
import scala.util.Failure

trait TestSpec extends FlatSpecLike with Matchers with ScalaFutures with OptionValues with BeforeAndAfterEach with BeforeAndAfterAll with DefaultJsonProtocol {
  implicit val ec: ExecutionContext = ExecutionContext.Implicits.global
  implicit val pc: PatienceConfig = PatienceConfig(timeout = 50.seconds)
  val log: Logger = LoggerFactory.getLogger(this.getClass)
  implicit val db: Database = Database.forConfig("mydb")

  implicit class FutureToTry[T](f: Future[T]) {
    def toTry: Try[T] = Try(f.futureValue)
  }

  override protected def beforeEach(): Unit = {
    CoffeeRepository.initialize
      .flatMap(_ ⇒ PersonRepository.initialize)
      .flatMap(_ ⇒ UsersRepository.initialize)
      .toTry recover { case t: Throwable ⇒ log.error("Could not initialize the database", t) } should be a 'success
  }

  implicit class MustBeWord[T](self: T) {
    def mustBe(pf: PartialFunction[T, Unit]): Unit =
      if (!pf.isDefinedAt(self)) throw new TestFailedException("Unexpected: " + self, 0)
  }

  override protected def afterAll(): Unit = {
  }
}
