package com.github.dnvriend

import slick.dbio.DBIO
import slick.driver.PostgresDriver.api._

import scala.concurrent.{ExecutionContext, Future}

object UsersRepository {

  case class User(id: Option[Int], first: String, last: String)

  class Users(tag: Tag) extends Table[User](tag, "users") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def first = column[String]("first")
    def last = column[String]("last")
    def * = (id.?, first, last) <> (User.tupled, User.unapply)
  }
  val users = TableQuery[Users]

  def dropCreateSchema(implicit db: Database, ec: ExecutionContext): Future[Unit] = {
    val schema = users.schema
    db.run(schema.create)
      .recoverWith { case t: Throwable =>
      db.run(DBIO.seq(schema.drop, schema.create))
    }
  }

  /**
   * Initializes the database; creates the schema and inserts users
   */
  def initialize(implicit db: Database, ec: ExecutionContext): Future[Unit] = {
    val setup = DBIO.seq(
      users += User(None, "Bill", "Gates"),
      users += User(None, "Steve", "Balmer"),
      users += User(None, "Steve", "Jobs"),
      users += User(None, "Steve", "Wozniak")
    )
    dropCreateSchema.flatMap(_ => db.run(setup))
  }
}
