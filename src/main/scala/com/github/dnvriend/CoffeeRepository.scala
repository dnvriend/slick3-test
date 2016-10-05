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

import com.github.dnvriend.CoffeeRepository.{ CoffeeTableRow, SupplierTableRow }
import slick.backend.DatabasePublisher
import slick.driver.JdbcProfile
import slick.jdbc.GetResult

import scala.concurrent.{ ExecutionContext, Future }

object CoffeeRepository {
  final case class SupplierTableRow(id: Int, name: String, street: String, city: String, state: String, zip: String)
  final case class CoffeeTableRow(name: String, supID: Int, price: Double, sales: Int, total: Int)
}

trait CoffeeRepository {
  val profile: slick.driver.JdbcProfile
  import profile.api._

  class SupplierTable(tag: Tag) extends Table[SupplierTableRow](tag, "SUPPLIERS") {
    // Every table needs a * projection with the same type as the table's type parameter
    def * = (id, name, street, city, state, zip) <> (SupplierTableRow.tupled, SupplierTableRow.unapply)
    def id = column[Int]("SUP_ID", O.PrimaryKey) // This is the primary key column
    def name = column[String]("SUP_NAME")
    def street = column[String]("STREET")
    def city = column[String]("CITY")
    def state = column[String]("STATE")
    def zip = column[String]("ZIP")
  }

  lazy val SupplierTable = new TableQuery(tag => new SupplierTable(tag))

  implicit val resultToCoffeeMapper: GetResult[CoffeeRepository.CoffeeTableRow] = GetResult(r => CoffeeTableRow(r.<<, r.<<, r.<<, r.<<, r.<<))

  // Definition of the COFFEES table
  class CoffeeTable(tag: Tag) extends Table[CoffeeTableRow](tag, "COFFEES") {
    // A reified foreign key relation that can be navigated to create a join
    def * = (name, supID, price, sales, total) <> (CoffeeTableRow.tupled, CoffeeTableRow.unapply)
    def name = column[String]("COF_NAME", O.PrimaryKey)
    def supID = column[Int]("SUP_ID")
    def price = column[Double]("PRICE")
    def sales = column[Int]("SALES")
    def total = column[Int]("TOTAL")
    def supplier = foreignKey("SUP_FK", supID, SupplierTable)(_.id)
  }

  lazy val CoffeeTable = new TableQuery(tag => new CoffeeTable(tag))

  def dropCreateSchema(implicit db: Database, ec: ExecutionContext): Future[Unit] = {
    val schema: profile.DDL = SupplierTable.schema ++ CoffeeTable.schema
    db.run(schema.create)
      .recoverWith {
        case t: Throwable =>
          db.run(DBIO.seq(schema.drop, schema.create))
      }
  }

  /**
   * Initializes the database; creates the schema and inserts supplies and coffees
   */
  def initialize(implicit db: Database, ec: ExecutionContext): Future[Unit] = {
    val setup: DBIOAction[Unit, NoStream, Effect.Write with Effect.Transactional] = DBIO.seq(
      // Insert some suppliers
      SupplierTable ++= Seq(
        SupplierTableRow(101, "Acme, Inc.", "99 Market Street", "Groundsville", "CA", "95199"),
        SupplierTableRow(49, "Superior Coffee", "1 Party Place", "Mendocino", "CA", "95460"),
        SupplierTableRow(150, "The High Ground", "100 Coffee Lane", "Meadows", "CA", "93966")
      ),
      // Equivalent SQL code:
      // insert into SUPPLIERS(SUP_ID, SUP_NAME, STREET, CITY, STATE, ZIP) values (?,?,?,?,?,?)

      // Insert some coffees (using JDBC's batch insert feature, if supported by the DB)
      CoffeeTable ++= Seq(
        CoffeeTableRow("Colombian", 101, 7.99, 0, 0),
        CoffeeTableRow("French_Roast", 49, 8.99, 0, 0),
        CoffeeTableRow("Espresso", 150, 11.99, 0, 0),
        CoffeeTableRow("Colombian_Decaf", 101, 10.99, 0, 0),
        CoffeeTableRow("French_Roast_Decaf", 49, 9.99, 0, 0)
      )
    ).transactionally
    dropCreateSchema.flatMap(_ => db.run(setup))
  }

  def deleteCoffeeByName(name: String)(implicit db: Database): Future[Int] =
    db.run(CoffeeTable.filter(_.name === name).delete)

  def deleteCoffee(coffee: CoffeeTableRow)(implicit db: Database): Future[Int] =
    db.run(CoffeeTable.filter(_.name === coffee.name).delete)

  def clearCoffeesTable(implicit db: Database): Future[Int] = db.run(CoffeeTable.delete)

  def coffeeStream(implicit db: Database): DatabasePublisher[CoffeeTableRow] =
    db.stream(CoffeeTable.result)

  def coffee(name: String)(implicit db: Database): Future[Seq[CoffeeTableRow]] =
    db.run(CoffeeTable.filter(_.name === name).result)

  def listCoffees(limit: Long, offset: Long = Long.MaxValue)(implicit db: Database) =
    // select * from coffees limit $limit offset $offset
    db.run(CoffeeTable.drop(offset).take(limit).result)
}

object PostgresCoffeeRepository extends CoffeeRepository {
  override val profile: JdbcProfile = slick.driver.PostgresDriver
}
