package com.github.dnvriend

import slick.backend.DatabasePublisher
import slick.dbio.DBIO
import slick.driver.PostgresDriver.api._

import scala.concurrent.{ExecutionContext, Future}

object CoffeeRepository {

  case class Supplier(id: Int, name: String, street: String, city: String, state: String, zip: String)

  private class Suppliers(tag: Tag) extends Table[Supplier](tag, "SUPPLIERS") {
    def id = column[Int]("SUP_ID", O.PrimaryKey) // This is the primary key column
    def name = column[String]("SUP_NAME")
    def street = column[String]("STREET")
    def city = column[String]("CITY")
    def state = column[String]("STATE")
    def zip = column[String]("ZIP")
    // Every table needs a * projection with the same type as the table's type parameter
    def * = (id, name, street, city, state, zip) <> (Supplier.tupled, Supplier.unapply)
  }

  case class Coffee(name: String, supID: Int, price: Double, sales: Int, total: Int)

  // Definition of the COFFEES table
  private class Coffees(tag: Tag) extends Table[Coffee](tag, "COFFEES") {
    def name = column[String]("COF_NAME", O.PrimaryKey)
    def supID = column[Int]("SUP_ID")
    def price = column[Double]("PRICE")
    def sales = column[Int]("SALES")
    def total = column[Int]("TOTAL")
    def * = (name, supID, price, sales, total) <> (Coffee.tupled, Coffee.unapply)
    // A reified foreign key relation that can be navigated to create a join
    def supplier = foreignKey("SUP_FK", supID, suppliersTQ)(_.id)
  }

  private val suppliersTQ = TableQuery[Suppliers]
  private val coffeesTQ = TableQuery[Coffees]

  def dropCreateSchema(implicit db: Database, ec: ExecutionContext): Future[Unit] = {
    val schema = suppliersTQ.schema ++ coffeesTQ.schema
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
      // Insert some suppliers
      suppliersTQ += Supplier(101, "Acme, Inc.", "99 Market Street", "Groundsville", "CA", "95199"),
      suppliersTQ += Supplier(49, "Superior Coffee", "1 Party Place", "Mendocino", "CA", "95460"),
      suppliersTQ += Supplier(150, "The High Ground", "100 Coffee Lane", "Meadows", "CA", "93966"),
      // Equivalent SQL code:
      // insert into SUPPLIERS(SUP_ID, SUP_NAME, STREET, CITY, STATE, ZIP) values (?,?,?,?,?,?)

      // Insert some coffees (using JDBC's batch insert feature, if supported by the DB)
      coffeesTQ ++= Seq(
        Coffee("Colombian", 101, 7.99, 0, 0),
        Coffee("French_Roast", 49, 8.99, 0, 0),
        Coffee("Espresso", 150, 9.99, 0, 0),
        Coffee("Colombian_Decaf", 101, 8.99, 0, 0),
        Coffee("French_Roast_Decaf", 49, 9.99, 0, 0)
      )
    )
    dropCreateSchema.flatMap(_ => db.run(setup))
  }

  /**
   * Returns a Stream of Coffee
   */
  def coffees(implicit db: Database): DatabasePublisher[Coffee] = db.stream[Coffee](coffeesTQ.result)
}
