package com.github.dnvriend

import akka.actor._
import akka.event.{Logging, LoggingAdapter}
import akka.stream.{ActorFlowMaterializer, FlowMaterializer}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest._
import spray.json.DefaultJsonProtocol
import slick.driver.PostgresDriver.api._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try
import scala.util.Failure

trait TestSpec extends FlatSpec with Matchers with ScalaFutures with OptionValues with BeforeAndAfterEach with BeforeAndAfterAll with DefaultJsonProtocol {
  implicit val system: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContext = system.dispatcher
  implicit val flowMaterializer: FlowMaterializer = ActorFlowMaterializer()
  implicit val log: LoggingAdapter = Logging(system, this.getClass)
  implicit val pc: PatienceConfig = PatienceConfig(timeout = 50.seconds)
  implicit val db: Database = DbExtension(system).db

  implicit class FutureToTry[T](f: Future[T]) {
    def toTry: Try[T] = Try(f.futureValue)
  }

  override protected def beforeEach(): Unit = {
    CoffeeRepository.initialize
      .flatMap(_ => PersonRepository.initialize)
      .flatMap(_ => UsersRepository.initialize)
      .toTry recoverWith { case t: Throwable => log.error(t, ""); Failure(t) } should be a 'success
  }

  implicit class MustBeWord[T](self: T) {
    def mustBe(pf: PartialFunction[T, Unit]): Unit =
      if(!pf.isDefinedAt(self)) throw new TestFailedException("Unexpected: " + self, 0)
  }

  override protected def afterAll(): Unit = {
    system.shutdown()
    system.awaitTermination()
  }
}
