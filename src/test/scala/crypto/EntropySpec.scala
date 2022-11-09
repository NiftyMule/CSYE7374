package crypto

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import util.Hex

class EntropySpec extends AnyFlatSpec with should.Matchers {

    behavior of "Entropy"

    it should "getEntropy 1" in {
        val bai: IO[Array[Byte]] = Entropy.getEntropy(252)
        bai.unsafeRunSync().length shouldBe 32
    }

    it should "getEntropy 2" in {
        val same: IO[Boolean] = for {
            bytes1 <- Entropy.getEntropy(256)
            bytes2 <- Entropy.getEntropy(256)
        } yield Hex.bytesToHexString(bytes1) == Hex.bytesToHexString(bytes2)
        same.unsafeRunSync() shouldBe false
    }

}
