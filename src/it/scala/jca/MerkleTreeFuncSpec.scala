package jca

import cats.effect.std.Dispatcher
import cats.effect.unsafe.implicits.global
import cats.effect.{Async, IO, Resource, Sync}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import scala.annotation.tailrec
import scala.util.Random
import tsec.hashing.CryptoHash
import tsec.hashing.bouncy.Keccak256
import tsec.hashing.jca.*
import util.RandomState

class MerkleTreeFuncSpec extends AnyFlatSpec with should.Matchers {

    behavior of "MerkleTreeFunc"

    // This should take less than 8 seconds to run
    it should "apply" in {
        // Method to check if bytes begins with 20 bits.
        def checkBlockHash(bytes: Bytes): Boolean = bytes(0) == 0 && bytes(1) == 0 && (bytes(2) & 0xF0) == 0

        val transactions = Seq(
            "Harry pays Robin 1.000",
            "Maharshi pays Harry 1.000",
            "Pranshu pays Maharshi 1.000",
            "Robin pays Pranshu 1.000"
        )
        val priorBlockHash = "00000dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824".getBytes()
        val result = MerkleTree.mineMerkleTreeBlock(transactions, priorBlockHash, 5)(checkBlockHash)(RandomState(1L)).unsafeRunSync()
        result match {
            case Some(nonce, hash) =>
                util.Hex.bytesToHexString(nonce) shouldBe "30D663C6BC"
                util.Hex.bytesToHexString(hash) shouldBe "000003E064410F12898CDFBBFC2013D06A2CF2ADABA5E560C195B284B3DBDD4D"
            case _ => fail("Unable to find suitable hash")
            case _ => fail("Unable to find suitable hash")
        }
    }

}
