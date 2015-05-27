package com.github.dnvriend

import akka.actor.{ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider}
import slick.jdbc.JdbcBackend

object DbExtension extends ExtensionId[DbExtensionImpl] with ExtensionIdProvider {
  override def createExtension(system: ExtendedActorSystem): DbExtensionImpl = new DbExtensionImpl()(system)

  override def lookup(): ExtensionId[_ <: Extension] = DbExtension
}

class DbExtensionImpl()(implicit val system: ExtendedActorSystem) extends JdbcBackend with Extension {
  implicit val db: Database = Database.forConfig("mydb")
}
