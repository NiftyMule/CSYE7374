scalaVersion := "3.1.0"

libraryDependencies ++= Seq(
  // https://mvnrepository.com/artifact/org.scala-lang.modules/scala-parser-combinators
  "org.scala-lang.modules" %% "scala-parser-combinators" % "2.1.1",
  "org.scalatest" %% "scalatest" % "3.2.12" % Test,
  // junit support
  "com.novocode" % "junit-interface" % "0.11" % "test"
)