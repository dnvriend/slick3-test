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

import akka.actor.{ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider}
import slick.jdbc.JdbcBackend

object DbExtension extends ExtensionId[DbExtensionImpl] with ExtensionIdProvider {
  override def createExtension(system: ExtendedActorSystem): DbExtensionImpl = new DbExtensionImpl()(system)

  override def lookup(): ExtensionId[_ <: Extension] = DbExtension
}

class DbExtensionImpl()(implicit val system: ExtendedActorSystem) extends JdbcBackend with Extension {
  implicit val db: Database = Database.forConfig("mydb")
}
