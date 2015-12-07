# slick3-test
A study on Slick 3, reactive-streams and RxScala/RxJava

# TL;DR
[Slick 3.0](http://slick.typesafe.com) has a completely new API, which is more friendly for executing 
database actions asynchronously that, when executed, return a [Scala Future](http://www.scala-lang.org/api/current/index.html#scala.concurrent.Future$) or a [Reactive Streams](http://www.reactive-streams.org/) Publisher. 
An [action](http://slick.typesafe.com/doc/3.1.0/api/index.html#slick.dbio.DBIOAction) is anything that you can execute on a database, 
like querying or inserting data. 

# Slick
> [Slick](http://slick.typesafe.com) (“Scala Language-Integrated Connection Kit”) is Typesafe‘s Functional Relational Mapping (FRM) library for Scala that makes it easy to work with relational databases. It allows you to work with stored data almost as if you were using Scala collections while at the same time giving you full control over when a database access happens and which data is transferred.  You can write your database queries in Scala instead of SQL, thus profiting from the static checking, compile-time safety and compositionality of Scala, but you can also use SQL directly. Execution of database actions is done asynchronously, making Slick a perfect fit for your reactive applications based on Play and Akka.

> [Slick](http://slick.typesafe.com) features an extensible query compiler which can generate code for different backends. From version 3.0 and up, Slick is reactive and asynchronous. It has a lot of [new features](http://slick.typesafe.com/news/2015/04/29/slick-3.0.0-released.html) most notably: A new API for composing and executing database actions, support for the [Reactive Streams API](http://www.reactive-streams.org) for streaming results from the database and improved configuration of database connections via Typesafe Config, including built-in support for HikariCP.
-- <quote>[Slick](http://slick.typesafe.com)</quote>

## Documentation
- [Slick 3.1.0 Manual](http://slick.typesafe.com/doc/3.1.0/)
- [Slick 3.1.0 - Getting Started](http://slick.typesafe.com/doc/3.1.0/gettingstarted.html)

## Code Generation
The [Slick code generator](http://slick.typesafe.com/doc/3.1.0/code-generation.html) is a convenient tool for working 
with an existing or evolving database schema. It can be run stand-alone or integrated into you sbt/Maven build for creating 
all code Slick needs to work.

## Video
A full list of slick talks can be found [here](http://slick.typesafe.com/docs/#talks), below are the ones I found useful:
- [Parleys - Scala Days San Fransisco 2015 - Stefan Zeiger - Reactive Slick for Database Programming](https://www.parleys.com/tutorial/reactive-slick-database-programming)
- [Youtube - Typesafe Webinar July 2015 - Endre Varga - A Deeper Look at Reactive Streams with Akka Streams 1.0 and Slick 3.0](https://www.youtube.com/watch?v=U9mJANtSpc4)

