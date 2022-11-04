package jca

import cats.effect.std.Dispatcher
import cats.effect.unsafe.implicits.global
import cats.effect.{Async, IO, Resource, Sync}
import crypto.RandomState
import jca.MerkleTree.Bytes
import jca.test.tree
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import scala.annotation.tailrec
import scala.util.Random
import tsec.hashing.CryptoHash
import tsec.hashing.bouncy.Keccak256
import tsec.hashing.jca.*

class MerkleTreeFuncSpec extends AnyFlatSpec with should.Matchers {

    behavior of "MerkleTreeFunc"

    it should "apply" in {
        val transactions = Seq(
            "Harry pays Robin 1.000",
            "Maharshi pays Harry 1.000",
            "Pranshu pays Maharshi 1.000",
            "Robin pays Pranshu 1.000"
        )
        val priorBlockHash = "00000dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824".getBytes()
        val (nonce, hash) = MerkleTree.mineMerkleTreeBlock(transactions, priorBlockHash, 5)(RandomState(3L))
        util.Hex.bytesToHexString(nonce) shouldBe "3ECEA0ECEE"
        util.Hex.bytesToHexString(hash) shouldBe "000003313A5FB6410B08ACD4C160607EB19B75BBC3E110FAC2EA544D325FDF56"
    }

}
