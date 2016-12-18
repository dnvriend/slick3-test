name := "slick3-test"

organization := "com.github.dnvriend"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.8"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.4.12"
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.4.12"
libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % "2.4.12"

libraryDependencies += "com.h2database" % "h2" % "1.4.193"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.7"
// https://www.playframework.com/documentation/2.5.x/PlaySlick
libraryDependencies += "com.typesafe.play" %% "play-slick" % "2.0.2"
libraryDependencies += "com.typesafe.play" %% "play-slick-evolutions" % "2.0.2"
//libraryDependencies += "com.typesafe.slick" %% "slick" % "3.2.0-M2"
//libraryDependencies += "com.typesafe.slick" %% "slick-hikaricp" % "3.2.0-M2"
libraryDependencies += "com.typesafe.slick" %% "slick" % "3.1.1"
libraryDependencies += "com.typesafe.slick" %% "slick-hikaricp" % "3.1.1"

libraryDependencies += "com.typesafe.akka" %% "akka-stream-testkit" % "2.4.12" % Test
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.4.12" % Test
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0-M1" % Test
libraryDependencies += "org.typelevel" %% "scalaz-scalatest" % "1.1.1" % Test

fork in Test := true

parallelExecution := false

licenses +=("Apache-2.0", url("http://opensource.org/licenses/apache2.0.php"))

import scalariform.formatter.preferences._
import com.typesafe.sbt.SbtScalariform

SbtScalariform.autoImport.scalariformPreferences := SbtScalariform.autoImport.scalariformPreferences.value
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 100)
  .setPreference(DoubleIndentClassDeclaration, true)

import de.heikoseeberger.sbtheader.license.Apache2_0

headers := Map(
  "scala" -> Apache2_0("2015", "Dennis Vriend"),
  "conf" -> Apache2_0("2015", "Dennis Vriend", "#")
)

enablePlugins(AutomateHeaderPlugin)
enablePlugins(SbtScalariform)
enablePlugins(PlayScala)
disablePlugins(PlayLayoutPlugin)