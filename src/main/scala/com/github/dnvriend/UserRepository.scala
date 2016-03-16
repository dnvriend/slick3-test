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

import com.github.dnvriend.UserRepository.UserTableRow
import slick.driver.JdbcProfile

import scala.concurrent.{ ExecutionContext, Future }

object UserRepository {

  final case class UserTableRow(id: Option[Int], first: String, last: String)

}

trait UserRepository {
  val profile: slick.driver.JdbcProfile

  import profile.api._

  class UserTable(tag: Tag) extends Table[UserTableRow](tag, "users") {
    def * = (id.?, first, last) <> (UserTableRow.tupled, UserTableRow.unapply)

    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def first = column[String]("first")

    def last = column[String]("last")
  }

  lazy val UserTable = new TableQuery(tag ⇒ new UserTable(tag))

  def dropCreateSchema(implicit db: Database, ec: ExecutionContext): Future[Unit] = {
    val schema: profile.SchemaDescription = UserTable.schema
    db.run(schema.create)
      .recoverWith {
        case t: Throwable ⇒
          db.run(DBIO.seq(schema.drop, schema.create))
      }
  }

  /**
   * Initializes the database; creates the schema and inserts users
   */
  def initialize(implicit db: Database, ec: ExecutionContext): Future[Unit] = {
    val setup: DBIOAction[Unit, NoStream, Effect.Write with Effect.Transactional] = DBIO.seq(
      // insert some users
      UserTable ++= Seq(
        UserTableRow(None, "Bill", "Gates"),
        UserTableRow(None, "Steve", "Balmer"),
        UserTableRow(None, "Steve", "Jobs"),
        UserTableRow(None, "Steve", "Wozniak")
      )
    ).transactionally
    dropCreateSchema.flatMap(_ ⇒ db.run(setup))
  }
}

object PostgresUserRepository extends UserRepository {
  override val profile: JdbcProfile = slick.driver.PostgresDriver
}

