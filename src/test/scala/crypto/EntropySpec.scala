package crypto

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should
import util.Hex

class EntropySpec extends AsyncFreeSpec with AsyncIOSpec with should.Matchers {

    "Entropy" - {
        "getEntropy 1" in {
            Entropy.getEntropy(252).asserting(_.length shouldBe 32)
        }
        "getEntropy 2" in {
            val same: IO[Boolean] = for {
                bytes1 <- Entropy.getEntropy(256)
                bytes2 <- Entropy.getEntropy(256)
            } yield Hex.bytesToHexString(bytes1) == Hex.bytesToHexString(bytes2)
            same.asserting(_ shouldBe false)
        }
    }
}
