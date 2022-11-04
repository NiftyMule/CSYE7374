package crypto

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import java.math.BigInteger
import scala.util.Success

class DiffieHellmanMerkleModifiedSpec extends AnyFlatSpec with should.Matchers {

  behavior of "DiffieHellmanMerkle"

  private val prime: Prime = Prime(31)
  private val g: BigInt = 11 // a primitive root of 31
  private val aliceKey: BigInt = 4
  private val bobKey: BigInt = 3
  private val secret: BigInt = 16
  private val plainText: BigInt = 17
  private val cipherText: BigInt = 14


  it should "construct" in {
    val target = DiffieHellmanMerkle(prime, g)
    target.modulus shouldBe prime
    target.generator shouldBe g
    target.toString shouldBe s"DiffieHellmanMerkle($prime,$g)"
  }

  it should "not construct" in {
    a[java.lang.IllegalArgumentException] should be thrownBy DiffieHellmanMerkle(Prime(4), 0)
    a[java.lang.IllegalArgumentException] should be thrownBy DiffieHellmanMerkle(prime, 4)
  }

  it should "keyExchange" in {
    val target = DiffieHellmanMerkle(prime, g)
    target.keyExchange(aliceKey) shouldBe 9 // 11^4 mod 31 -> 9
    target.keyExchange(bobKey) shouldBe 29 // 11^3 mod 31 -> 29
  }

  it should "secret" in {
    val target = DiffieHellmanMerkle(prime, g)
    // 29^4 mod 31 -> 16
    // 9^3  mod 31 -> 16
    target.secret(aliceKey, bobKey) shouldBe Success(secret)
  }

  it should "get the multiplicative inverse" in {
    val target = DiffieHellmanMerkle(prime, g)
    val sy = target.secret(aliceKey, bobKey) // 16
    sy shouldBe Success(secret)
    val multiplicativeInverse = target.multiplicativeInverse(sy.get) // LFT: 16^29 mod 31 -> 2
    multiplicativeInverse shouldBe 2
    val product: BigInt = sy.get * multiplicativeInverse
    product.mod(prime.toBigInt) shouldBe 1
    product shouldBe 32
    // ignored
    // product shouldBe (prime.toBigInt * 7 + 1)
  }

  it should "modPow" in {
    val z = prime.modPow(plainText, secret)
    z shouldBe plainText.pow(secret.toInt).mod(prime.toBigInt)
    z shouldBe cipherText
  }

  it should "encrypt" in {
    val target = DiffieHellmanMerkle(prime, g)
    val sy = target.secret(aliceKey, bobKey)
    sy shouldBe Success(secret)
    target.encrypt(plainText)(sy.get) shouldBe cipherText
    val ciphers = for (i <- 1 to 22) yield target.encrypt(i)(sy.get)
    println(ciphers)
  }
}
