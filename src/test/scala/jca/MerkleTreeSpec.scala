package jca

import cats.effect.std.Dispatcher
import cats.effect.unsafe.implicits.global
import cats.effect.{Async, IO, Resource, Sync}
import jca.test.tree
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import scala.annotation.tailrec
import scala.util.Random
import tsec.hashing.CryptoHash
import tsec.hashing.bouncy.Keccak256
import tsec.hashing.jca.*

class MerkleTreeSpec extends AnyFlatSpec with should.Matchers {

    behavior of "MerkleTree"

    it should "pad" in {
        MerkleTree.pad(3) shouldBe "      "
    }

    it should "apply" in {
        val strings = Array(
            "When I was one-and-twenty",
            "I heard a wise man say",
            "Give crowns and pounds and guineas",
            "But not your heart away",
            "Give pearls away and rubies",
            "But keep your fancy free",
            "But I was one-and-twenty",
            "No use to talk to me",
            "When I was one-and-twenty",
            "I heard him say again",
            "The heart out of the bosom",
            "Was never given in vain",
            "Tis paid with sighs a plenty",
            "And sold for endless rue",
            "And I am two-and-twenty",
            "And oh tis true tis true"
        )
        val tree: MerkleTree[CryptoHash[SHA256]] = MerkleTree(strings.map(_.getBytes()))
        tree.toString shouldBe
                """MerkleTreeNode:
                  |:left:
                  |  MerkleTreeNode:
                  |:  left:
                  |    MerkleTreeNode:
                  |:    left:
                  |      MerkleTreeNode:
                  |:      left:
                  |        When I was one-and-twenty
                  |:      right:
                  |        I heard a wise man say
                  |
                  |:    right:
                  |      MerkleTreeNode:
                  |:      left:
                  |        Give crowns and pounds and guineas
                  |:      right:
                  |        But not your heart away
                  |
                  |
                  |:  right:
                  |    MerkleTreeNode:
                  |:    left:
                  |      MerkleTreeNode:
                  |:      left:
                  |        Give pearls away and rubies
                  |:      right:
                  |        But keep your fancy free
                  |
                  |:    right:
                  |      MerkleTreeNode:
                  |:      left:
                  |        But I was one-and-twenty
                  |:      right:
                  |        No use to talk to me
                  |
                  |
                  |
                  |:right:
                  |  MerkleTreeNode:
                  |:  left:
                  |    MerkleTreeNode:
                  |:    left:
                  |      MerkleTreeNode:
                  |:      left:
                  |        When I was one-and-twenty
                  |:      right:
                  |        I heard him say again
                  |
                  |:    right:
                  |      MerkleTreeNode:
                  |:      left:
                  |        The heart out of the bosom
                  |:      right:
                  |        Was never given in vain
                  |
                  |
                  |:  right:
                  |    MerkleTreeNode:
                  |:    left:
                  |      MerkleTreeNode:
                  |:      left:
                  |        Tis paid with sighs a plenty
                  |:      right:
                  |        And sold for endless rue
                  |
                  |:    right:
                  |      MerkleTreeNode:
                  |:      left:
                  |        And I am two-and-twenty
                  |:      right:
                  |        And oh tis true tis true
                  |
                  |
                  |
                  |""".stripMargin
        val si = for (x <- tree.getHash; z <- HexEncryption.bytesToHexString(x.bytes)) yield z
        si.unsafeRunSync() shouldBe "B970CD10245D63690368F7EFEA1C0289115DF3A5310A119F53236FA248BAE0FA"
    }

}
