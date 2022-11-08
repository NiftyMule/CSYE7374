package crypto

import cats.effect.unsafe.implicits.global
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import util.Hex

class EntropySpec extends AnyFlatSpec with should.Matchers {

    behavior of "Entropy"

    it should "getEntropy" in {
        val hex1 = Hex.bytesToHexString(Entropy.getEntropy(16).unsafeRunSync())
        val hex2 = Hex.bytesToHexString(Entropy.getEntropy(16).unsafeRunSync())
        hex1 shouldNot be(hex2)
    }

}
