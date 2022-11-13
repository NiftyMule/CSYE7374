package util

import java.nio.file.{Files, Paths}
import scala.util.{Try, Using}

object DevRandom {
    private val randomSource = "/dev/random"

    def getRandom(n: Int): Try[Array[Byte]] =
        TryUsing(Try(Files.newInputStream(Paths.get(randomSource))))(stream => Try(stream.readNBytes(n)))
}
