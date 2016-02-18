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

import slick.dbio.DBIO
import slick.driver.H2Driver.api._

import scala.concurrent.{ ExecutionContext, Future }

object UsersRepository {

  case class User(id: Option[Int], first: String, last: String)

  class Users(tag: Tag) extends Table[User](tag, "users") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def first = column[String]("first")
    def last = column[String]("last")
    def * = (id.?, first, last) <> (User.tupled, User.unapply)
  }
  val users: TableQuery[UsersRepository.Users] = TableQuery[Users]

  def dropCreateSchema(implicit db: Database, ec: ExecutionContext): Future[Unit] = {
    val schema = users.schema
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
    val setup: DBIOAction[Unit, NoStream, Effect.Write] = DBIO.seq(
      users += User(None, "Bill", "Gates"),
      users += User(None, "Steve", "Balmer"),
      users += User(None, "Steve", "Jobs"),
      users += User(None, "Steve", "Wozniak")
    )
    dropCreateSchema.flatMap(_ ⇒ db.run(setup))
  }
}
