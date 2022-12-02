package crypto

import java.math.BigInteger
import org.bouncycastle.crypto.digests.*
import org.bouncycastle.crypto.prng.*

/**
 * Case class to represent the state of a cryptographic random source.
 *
 * @param value the bytes of the current state.
 * @param r     the random generator.
 * @param n     (implicit) number of bytes to return when next is called (also applies to lazyList).
 */
case class CryptoRandomState(value: Array[Byte], r: RandomGenerator)(implicit n: Int) {
    /**
     * Method to yield an array of Byte with the length given by n.
     *
     * @param n the required length.
     * @return an Array[Byte].
     */
    def bytes(n: Int): Array[Byte] = {
        val result = new Array[Byte](n)
        r.nextBytes(result)
        result
    }

    /**
     *
     * @param max
     * @return
     */
    def long(max: Long): Long = {
        var result = 0
        for (b <- value) { // NOTE: need to check there are no more than 8 bytes
            result = (result << 8) + (b & 0xFF)
        }
        if (max == Long.MaxValue)
            result
        else
            (result & 0x7FFFFFFFFFFFFFFFL) % max
    }

    def long(): Long = long(Long.MaxValue)

    def next: CryptoRandomState = {
        val b = bytes(n)
        CryptoRandomState(b, r)
    }

    def lazyList: LazyList[CryptoRandomState] = LazyList.iterate(this)(r => r.next)

}

object CryptoRandomState {
    def apply(seed: Array[Byte]): CryptoRandomState = {
        val x: RandomGenerator = new DigestRandomGenerator(new SHA256Digest)
        x.addSeedMaterial(seed)
        CryptoRandomState(seed, x)
    }

    import cats.effect.unsafe.implicits.global
    import util.DevRandom.standardRandom

    implicit val nBytes: Int = 8

    def apply(): CryptoRandomState = {
        val randomIO = util.DevRandom.getRandom(128)
        apply(randomIO.unsafeRunSync())
    }
}
