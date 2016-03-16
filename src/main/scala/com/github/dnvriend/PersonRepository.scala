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

import java.sql.{ Date, Timestamp }
import java.text.SimpleDateFormat
import java.util.UUID

import com.github.dnvriend.PersonRepository.PersonTableRow
import slick.driver.JdbcProfile

import scala.concurrent.{ ExecutionContext, Future }

object PersonRepository {
  final case class PersonTableRow(name: String, dateOfBirth: Date, created: Timestamp = new Timestamp(System.currentTimeMillis()), id: String = UUID.randomUUID.toString)
}

trait PersonRepository {
  val profile: slick.driver.JdbcProfile
  import profile.api._

  implicit class DateString(dateString: String) {
    def date: java.sql.Date = {
      val sdf = new SimpleDateFormat("yyyy-MM-dd")
      new java.sql.Date(sdf.parse(dateString).getTime)
    }
  }

  class PersonTable(tag: Tag) extends Table[PersonTableRow](tag, "PERSONS") {
    def * = (name, dateOfBirth, created, id) <> (PersonTableRow.tupled, PersonTableRow.unapply)
    def id = column[String]("ID", O.PrimaryKey)
    def name = column[String]("NAME")
    def dateOfBirth = column[Date]("DATE_OF_BIRTH")
    def created = column[Timestamp]("CREATED")
  }

  lazy val PersonTable = new TableQuery(tag ⇒ new PersonTable(tag))

  def dropCreateSchema(implicit db: Database, ec: ExecutionContext): Future[Unit] = {
    val schema: profile.SchemaDescription = PersonTable.schema
    db.run(schema.create)
      .recoverWith {
        case t: Throwable ⇒
          db.run(DBIO.seq(schema.drop, schema.create))
      }
  }

  /**
   * Initializes the database; creates the schema and inserts persons
   */
  def initialize(implicit db: Database, ec: ExecutionContext): Future[Unit] = {
    val setup: DBIOAction[Unit, NoStream, Effect.Write with Effect.Transactional] = DBIO.seq(
      // Insert some persons
      PersonTable ++= Seq(
        PersonTableRow("Arnold Schwarzenegger", "1947-07-30".date),
        PersonTableRow("Bruce Willis", "1955-03-19".date),
        PersonTableRow("Jackie Chan", "1954-04-07".date),
        PersonTableRow("Bruce Lee", "1940-11-27".date),
        PersonTableRow("Sigourney Weaver", "1949-10-08".date),
        PersonTableRow("Harrison Ford", "1942-07-13".date),
        PersonTableRow("Patrick Stewart", "1940-07-13".date),
        PersonTableRow("Kate Mulgrew", "1955-04-29".date)
      )
    ).transactionally
    dropCreateSchema.flatMap(_ ⇒ db.run(setup))
  }
}

object PostgresPersonRepository extends PersonRepository {
  override val profile: JdbcProfile = slick.driver.PostgresDriver
}

