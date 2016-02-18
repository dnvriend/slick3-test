name := "slick3-test"

organization := "com.github.dnvriend"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= {
  val akkaVersion = "2.4.2"
  val slickVersion = "3.1.1"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream" % akkaVersion,
    "io.reactivex" %% "rxscala" % "0.26.0",
    "io.reactivex" % "rxjava-reactive-streams" % "1.0.1",
    "com.typesafe.slick" %% "slick" % slickVersion,
    "com.typesafe.slick" %% "slick-hikaricp" % slickVersion,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaVersion,
    "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
    "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
    "com.h2database" % "h2" % "1.4.191",
    "org.scalatest" %% "scalatest" % "2.2.4" % Test
  )
}

fork in Test := true

parallelExecution := false

testForkedParallel in Test := false

licenses +=("Apache-2.0", url("http://opensource.org/licenses/apache2.0.php"))

// enable scala code formatting //
import scalariform.formatter.preferences._

scalariformSettings

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 100)
  .setPreference(DoubleIndentClassDeclaration, true)
  .setPreference(RewriteArrowSymbols, true)

// enable updating file headers //
import de.heikoseeberger.sbtheader.license.Apache2_0

headers := Map(
  "scala" -> Apache2_0("2015", "Dennis Vriend"),
  "conf" -> Apache2_0("2015", "Dennis Vriend", "#")
)

enablePlugins(AutomateHeaderPlugin)