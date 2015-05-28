package com.github.dnvriend

import java.sql.{Date, Timestamp}
import java.text.SimpleDateFormat
import java.util.UUID

import slick.dbio.DBIO
import slick.driver.PostgresDriver.api._

import scala.concurrent.{ExecutionContext, Future}

object PersonRepository {

  implicit class DateString(dateString: String) {
    def date: java.sql.Date = {
      val sdf = new SimpleDateFormat("yyyy-MM-dd")
      new java.sql.Date(sdf.parse(dateString).getTime)
    }
  }

  case class Person(name: String, dateOfBirth: Date, created: Timestamp = new Timestamp(System.currentTimeMillis()), id: String = UUID.randomUUID.toString)

  class Persons(tag: Tag) extends Table[Person](tag, "PERSONS") {
    def id = column[String]("ID", O.PrimaryKey)
    def name = column[String]("NAME")
    def dateOfBirth = column[Date]("DATE_OF_BIRTH")
    def created = column[Timestamp]("CREATED")
    def * = (name, dateOfBirth, created, id) <> (Person.tupled, Person.unapply)
  }

  val persons = TableQuery[Persons]

  def dropCreateSchema(implicit db: Database, ec: ExecutionContext): Future[Unit] = {
    val schema = persons.schema
    db.run(schema.create)
      .recoverWith { case t: Throwable =>
      db.run(DBIO.seq(schema.drop, schema.create))
    }
  }

  /**
   * Initializes the database; creates the schema and inserts supplies and coffees
   */
  def initialize(implicit db: Database, ec: ExecutionContext): Future[Unit] = {
    val setup = DBIO.seq(
      // Insert some persons
      persons += Person("Arnold Schwarzenegger", "1947-07-30".date),
      persons += Person("Bruce Willis", "1955-03-19".date),
      persons += Person("Jackie Chan", "1954-04-07".date),
      persons += Person("Bruce Lee", "1940-11-27".date),
      persons += Person("Sigourney Weaver", "1949-10-08".date),
      persons += Person("Harrison Ford", "1942-07-13".date),
      persons += Person("Patrick Stewart", "1940-07-13".date),
      persons += Person("Kate Mulgrew", "1955-04-29".date)
    )
    dropCreateSchema.flatMap(_ => db.run(setup))
  }
}
