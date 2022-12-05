package util

import cats.effect.IO
import java.nio.file.{Files, Path, Paths}
import scala.util.{Try, Using}

object DevRandom {
    /**
     * Method to get n bytes of entropy from the given randomSource
     *
     * @param n the number of bytes of entropy required.
     * @return an array of Byte with the length given by n.
     */
    def getRandom(n: Int)(implicit randomPath: Path): IO[Array[Byte]] =
        IO.fromTry(TryUsing(Try(Files.newInputStream(randomPath))) { stream => Try(stream.readNBytes(n)) })

    implicit val standardRandom: Path = Paths.get("/dev/random")
}
