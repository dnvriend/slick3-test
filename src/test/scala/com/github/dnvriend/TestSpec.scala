package com.github.dnvriend

import akka.actor._
import akka.event.{Logging, LoggingAdapter}
import akka.stream.{ActorFlowMaterializer, FlowMaterializer}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
import spray.json.DefaultJsonProtocol
import slick.driver.PostgresDriver.api._

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

trait TestSpec extends FlatSpec with Matchers with ScalaFutures with BeforeAndAfterAll with DefaultJsonProtocol {
  implicit val system: ActorSystem = ActorSystem()
  implicit val ec: ExecutionContext = system.dispatcher
  implicit val flowMaterializer: FlowMaterializer = ActorFlowMaterializer()
  implicit val log: LoggingAdapter = Logging(system, this.getClass)
  implicit val pc: PatienceConfig = PatienceConfig(timeout = 50.seconds)
  implicit val db: Database = DbExtension(system).db

  implicit class FutureToTry[T](f: Future[T]) {
    def toTry: Try[T] = Try(f.futureValue)
  }

  override protected def afterAll(): Unit = {
    system.shutdown()
    system.awaitTermination()
  }
}
