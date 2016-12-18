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

package com.github.dnvriend.slicktest

import com.github.dnvriend.TestSpec

class DateTimeTest extends TestSpec {
  import profile.api._
  import personRepository._

  "Persons" should "be ordered on date of birth asc" in {
    db.run(PersonTable.sortBy(_.dateOfBirth).result)
      .futureValue
      .map(p => (p.name, p.dateOfBirth.toString)) shouldBe
      List(
        ("Patrick Stewart", "1940-07-13"),
        ("Bruce Lee", "1940-11-27"),
        ("Harrison Ford", "1942-07-13"),
        ("Arnold Schwarzenegger", "1947-07-30"),
        ("Sigourney Weaver", "1949-10-08"),
        ("Jackie Chan", "1954-04-07"),
        ("Bruce Willis", "1955-03-19"),
        ("Kate Mulgrew", "1955-04-29")
      )
  }

  it should "be ordered on date of birth desc" in {
    db.run(PersonTable.sortBy(_.dateOfBirth.desc).result)
      .futureValue
      .map(p => (p.name, p.dateOfBirth.toString)) shouldBe
      List(
        ("Kate Mulgrew", "1955-04-29"),
        ("Bruce Willis", "1955-03-19"),
        ("Jackie Chan", "1954-04-07"),
        ("Sigourney Weaver", "1949-10-08"),
        ("Arnold Schwarzenegger", "1947-07-30"),
        ("Harrison Ford", "1942-07-13"),
        ("Bruce Lee", "1940-11-27"),
        ("Patrick Stewart", "1940-07-13")
      )
  }

  it should "select persons born > 1950-01-01" in {
    db.run(PersonTable.filter(_.dateOfBirth > "1950-01-01".date).sortBy(_.dateOfBirth).result)
      .futureValue.map(_.name) shouldBe List("Jackie Chan", "Bruce Willis", "Kate Mulgrew")
  }

  it should "select persons born in 1940" in {
    db.run(PersonTable.filter(p => p.dateOfBirth > "1940-01-01".date && p.dateOfBirth < "1940-12-31".date).sortBy(_.dateOfBirth).result)
      .futureValue.map(_.name) shouldBe List("Patrick Stewart", "Bruce Lee")
  }
}
