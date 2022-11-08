package crypto

import cats.effect.IO
import util.Hex

import java.io.{FileInputStream, InputStream}
import scala.util.Try

object Entropy {
    val sy: Try[InputStream] = Try(new FileInputStream("/dev/random"))

    def getEntropy(nBits: Int): IO[Array[Byte]] = {
        val nBytes = (nBits + 7) / 8
        IO.fromTry(for {s <- sy
                        bytes = new Array[Byte](nBytes)
                        x = s.read(bytes, 0, bytes.length)
                        if x >= nBytes
                        } yield bytes)
    }


}
