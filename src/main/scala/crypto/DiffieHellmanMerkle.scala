package crypto

import scala.util.{Failure, Success, Try}

/**
 * This defines a DiffieHellmanMerkle key exchange.
 *
 * For more information about primitive roots, see https://en.wikipedia.org/wiki/Primitive_root_modulo_n
 *
 * @param modulus   a prime number.
 * @param generator a primitive root of modulus.
 */
case class DiffieHellmanMerkle(modulus: Prime, generator: BigInt) {
  // NOTE we should probably replace validate with isProbablyPrime for performance reasons.
  require(modulus.validate, s"$modulus is not prime")
  require(modulus.testPrimitiveRoot(generator), s"$generator is not a primitive root of $modulus")

  def encrypt(plain: BigInt)(secret: BigInt): BigInt = modPow(plain)(secret)

  def decrypt(cipher: BigInt)(secret: BigInt): BigInt = modPow(cipher)(multiplicativeInverse(secret))

  def keyExchange(privateKey: BigInt): BigInt = modPow(generator)(privateKey.mod(modulus.toBigInt))

  def secret(a: BigInt, b: BigInt): Try[BigInt] = Try {
    assert(a > 0 && b > 0)
    val ab = getSecret(keyExchange(a))(b)
    val ba = getSecret(keyExchange(b))(a)
    if (ab == ba)
        ab
    else
      throw PrimeException(s"secret: ab doesn't match ba")
  }

  /**
   * Get the modular multiplicative inverse of a number with respect to a prime number.
   *
   * For more information about the multiplicative inverse, see https://en.wikipedia.org/wiki/Modular_multiplicative_inverse
   *
   * @param a the number for which we require the multiplicative inverse with respect to the prime number of this DiffieHellmanMerkle.
   * @return modPow(a)(modulus - 2)
   */
  def multiplicativeInverse(a: BigInt): BigInt = modPow(a)(modulus.toBigInt - 2)

    private def getSecret(receivedKey: BigInt)(privateKey: BigInt): BigInt = modPow(receivedKey)(privateKey)

  /**
   * Method to determine base raised to the power with result modulo modulus (the prime of this DiffieHellmanMerkle).
   *
   * @param base  the base.
   * @param power the power.
   * @return the value of base raised to power, modulo modulus.
   */
  private def modPow(base: BigInt)(power: BigInt) = modulus.modPow(base, power)
}

object DiffieHellmanMerkle
