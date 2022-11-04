package he

import cats.effect.IO
import jca.BaseHexEncryption
import tsec.cipher.symmetric
import tsec.cipher.symmetric.PlainText
import tsec.cipher.symmetric.jca.{AES128CTR, SecretKey}

/**
 * Trait which defines a pseudo-homomorphic encryption of a record X using symmetric encryption algorithm A.
 *
 * @tparam A the cipher algorithm.
 * @tparam X the type of an individual record.
 */
trait PseudoHomomorphicEncryption[A, X] extends jca.Encryption[A] {

    type ByteArray = Array[Byte]

    val functionRawKey: String
    val protectedRawKey: String

    /**
     * Abstract method to partition the given plaintext into a protected String and a functional String.
     *
     * @param record the record in plain text.
     * @return a map of String=>String.
     */
    def partitionPlaintext(record: X): Map[String, String]

    /**
     * Method to combine the protected and functional partitions into one message.
     *
     * @param partitions a tuple of two ByteArrays.
     * @return a ByteArray.
     */
    def combinePlaintext(partitions: (ByteArray, ByteArray)): ByteArray = partitions._1 ++ partitions._2

    def encrypt(key: SecretKey[A])(plaintext: String): IO[symmetric.CipherText[A]] = ???

    def decrypt(key: SecretKey[A])(cipher: symmetric.CipherText[A]): IO[String] = ???

}


