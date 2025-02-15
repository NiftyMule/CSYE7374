package crypto

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import scala.language.postfixOps

class PrimesFunctionalSpec extends AnyFlatSpec with should.Matchers {

  behavior of "Prime"

  it should "validate random primes" in {
    val bits = 60
    val prime = Primes.randomPrime(bits)
    prime.bits shouldBe bits
    println(prime)
    prime.validate shouldBe true
  }

  // NOTE: This takes much too long.
  it should "validate" in {
        (Prime.create("35742549198872617291353508656626642567") map (_.validate)) shouldBe Some(true)
  }

  it should "create Mersenne prime" in {
    Prime.createMersennePrime(8) map (_.validate) shouldBe None
    Prime.createMersennePrime(9) map (_.validate) shouldBe None
    Prime.createMersennePrime(10) map (_.validate) shouldBe Some(true)
    Prime.createMersennePrime(11) map (_.validate) shouldBe None
    Prime.createMersennePrime(12) map (_.validate) shouldBe None
    Prime.createMersennePrime(13) map (_.validate) shouldBe None
    Prime.createMersennePrime(14) map (_.validate) shouldBe None
    Prime.createMersennePrime(15) map (_.validate) shouldBe None
    Prime.createMersennePrime(16) map (_.validate) shouldBe None
    Prime.createMersennePrime(17) map (_.validate) shouldBe Some(true)
  }
}
