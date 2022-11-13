package util

import cats.effect.IO

import java.nio.file.{Files, Paths}
import scala.util.{Try, Using}

object DevRandom {
    private val randomSource = "/dev/random"

    def getRandom(n: Int): IO[Array[Byte]] =
        IO.fromTry(TryUsing(Try(Files.newInputStream(Paths.get(randomSource)))) { stream => Try(stream.readNBytes(n)) })
}
