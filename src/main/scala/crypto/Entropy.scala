package crypto

import cats.effect.IO
import java.io.{FileInputStream, InputStream}
import scala.util.Try
import util.Hex

/**
 * Object to provide entropy.
 */
object Entropy {
    val sy: Try[InputStream] = Try(new FileInputStream("/dev/random"))

    /**
     * Method to get a number of bits of entropy from the /dev/random device.
     * Since the result is an array of Byte, the number of bits is rounded up to the next multiple of 8.
     *
     * @param nBits the number of bits of entropy to gather.
     * @return an array of Byte long enough to provide the required nBits of entropy.
     */
    def getEntropy(nBits: Int): IO[Array[Byte]] = {
        val nBytes = (nBits + 7) / 8
        IO.fromTry(for {s <- sy
                        bytes = new Array[Byte](nBytes)
                        x = s.read(bytes, 0, bytes.length)
                        if x >= nBytes
                        } yield bytes)
    }
}
