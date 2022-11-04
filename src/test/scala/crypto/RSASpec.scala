package crypto

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import scala.util.Random

class RSASpec extends AnyFlatSpec with should.Matchers {

  behavior of "RSASpec"

  implicit val random: Random = new Random(7)

  // For the following example, see https://en.wikipedia.org/wiki/RSA_(cryptosystem)
  private val wikiRSA: RSA = RSA(Prime(61), Prime(53), (_, _) => 2)
  private val msgPlain = 65
  private val msgCipher = 2790

  it should "encrypt" in {
    wikiRSA.encrypt(msgPlain) shouldBe msgCipher
  }

  it should "decrypt" in {
    wikiRSA.decrypt(msgCipher) shouldBe msgPlain
  }

  it should "apply" in {
    val target = wikiRSA
    target.publicKey shouldBe Key(3233, 17)
    target.privateKey shouldBe Key(3233, 413)
  }

  it should "roundTrip" in {
    val target: RSA = RSA.apply
    target.decrypt(target.encrypt(msgPlain)) shouldBe msgPlain
  }

}
