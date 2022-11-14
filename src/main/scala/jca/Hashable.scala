package jca

import cats.effect.std.Dispatcher
import cats.effect.{Async, IO, Resource, Sync}
import scala.annotation.tailrec
import scala.util.Random
import tsec.hashing.CryptoHash
import tsec.hashing.bouncy.Keccak256
import tsec.hashing.jca.*

/**
 * Typeclass to describe the behavior of a Hashable object.
 *
 * @tparam H the underlying hash type.
 */
trait Hashable[H] {
    /**
     * Method to hash an array of bytes as a H, wrapped inside IO.
     *
     * @param plaintext the byte array to hash.
     * @return an IO[H].
     */
    def hash(plaintext: Array[Byte]): IO[H]

    /**
     * Method to extract a byte array from a hash of type H.
     *
     * @param hash the hash.
     * @return an Array[Byte].
     */
    def bytes(hash: H): Array[Byte]
}

object Hashable {

    /**
     * Trait used to declare implicit Hashable of CryptoHash[SHA256].
     */
    trait CryptoHashSHA256Hashable extends Hashable[CryptoHash[SHA256]] {
        def hash(plaintext: Array[Byte]): IO[CryptoHash[SHA256]] = SHA256.hash[IO](plaintext)

        def bytes(hash: CryptoHash[SHA256]): Array[Byte] = hash.bytes
    }

    /**
     * Implicit Hashable of CryptoHash[SHA256].
     */
    implicit object CryptoHashSHA256Hashable extends CryptoHashSHA256Hashable

}
