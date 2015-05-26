package com.github.dnvriend

import akka.actor.{ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider}
import akka.event.{Logging, LoggingAdapter}
import akka.stream.{ActorFlowMaterializer, FlowMaterializer}
import slick.jdbc.JdbcBackend

import scala.concurrent.ExecutionContext

object DbExtension extends ExtensionId[DbExtensionImpl] with ExtensionIdProvider {
  override def createExtension(system: ExtendedActorSystem): DbExtensionImpl = new DbExtensionImpl()(system)

  override def lookup(): ExtensionId[_ <: Extension] = DbExtension
}

class DbExtensionImpl()(implicit val system: ExtendedActorSystem) extends JdbcBackend with Extension {
  implicit val flowMaterializer: FlowMaterializer = ActorFlowMaterializer()
  implicit val log: LoggingAdapter = Logging(system, this.getClass)
  implicit val ec: ExecutionContext = system.dispatcher
  implicit val db: Database = Database.forConfig("mydb")

  CoffeeRepository.initialize
}
