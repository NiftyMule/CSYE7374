package crypto

import cats.effect.IO
import util.Hex

import java.io.{FileInputStream, InputStream}
import scala.util.Try

object Entropy {
    val sy: Try[InputStream] = Try(new FileInputStream("/dev/random"))
    val n = 32
    getEntropy(n)

    def getEntropy(n: Int): IO[Array[Byte]] = {
        IO.fromTry(for {s <- sy
                        bytes = new Array[Byte](n)
                        x = s.read(bytes, 0, bytes.length)
                        if x >= n
                        } yield bytes)
    }


}
