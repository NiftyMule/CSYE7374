scalaVersion := "3.2.0"

resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

resolvers += "Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/"

lazy val tsecVersion = "0.4.0"
lazy val catsEffectVersion = "3.1.1"
lazy val fs2Version = "3.3.0"

libraryDependencies ++= Seq(
  "io.github.jmcardon" %% "tsec-mac" % tsecVersion,
  "io.github.jmcardon" %% "tsec-password" % tsecVersion,
  "io.github.jmcardon" %% "tsec-signatures" % tsecVersion,
  "io.github.jmcardon" %% "tsec-cipher-jca" % tsecVersion,
  "io.github.jmcardon" %% "tsec-cipher-bouncy" % tsecVersion,
  "io.github.jmcardon" %% "tsec-hash-jca" % tsecVersion,
  "io.github.jmcardon" %% "tsec-hash-bouncy" % tsecVersion,
  "co.fs2" %% "fs2-core" % fs2Version,
  "org.typelevel" %% "cats-effect-testing-scalatest" % "1.4.0" % Test,
  "org.scala-lang.modules" %% "scala-parser-combinators" % "2.1.1",
  "com.novocode" % "junit-interface" % "0.11" % "test",
  "org.scalatest" %% "scalatest" % "3.2.14" % Test,
  "org.bouncycastle" % "bcprov-jdk15on" % "1.70",
  "org.bouncycastle" % "bcpkix-jdk15on" % "1.70"
)
