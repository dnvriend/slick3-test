name := "slick3-test"

version := "1.0"

scalaVersion := "2.11.6"

libraryDependencies ++= {
  val akkaVersion    = "2.3.11"
  val streamsVersion = "1.0-RC3"
  Seq(
    "com.typesafe.akka"  %%  "akka-actor"                       % akkaVersion,
    "com.typesafe.akka"  %%  "akka-slf4j"                       % akkaVersion,
    "ch.qos.logback"      %  "logback-classic"                  % "1.1.2",
    "com.typesafe.akka"  %%  "akka-stream-experimental"         % streamsVersion,
    "io.reactivex"       %%  "rxscala"                          % "0.24.1",
    "io.reactivex"        %  "rxjava-reactive-streams"          % "1.0.0",
    "com.typesafe.slick" %%  "slick"                            % "3.0.0",
    "com.zaxxer"          %  "HikariCP-java6"                   % "2.3.5",
    "org.postgresql"      %  "postgresql"                       % "9.4-1201-jdbc41",
    "io.spray"           %%  "spray-json"                       % "1.3.2",
    "com.typesafe.akka"  %%  "akka-testkit"                     % akkaVersion        % Test,
    "org.scalatest"      %%  "scalatest"                        % "2.2.4"            % Test
  )
}

fork in Test := true

parallelExecution in Test := false