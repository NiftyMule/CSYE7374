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
import util.Hex

class MerkleTreeSpec extends AnyFlatSpec with should.Matchers {

    behavior of "MerkleTree"

    it should "pad" in {
        MerkleTree.pad(3) shouldBe "      "
    }

    it should "apply 1" in {
        val transactions = Seq(
            "Harry pays Robin 1.000",
            "Maharshi pays Harry 1.000",
            "Pranshu pays Maharshi 1.000",
            "Robin pays Pranshu 1.000"
        )
        val priorBlockHash = Hex.hexStringToBytes("00000dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824")
        val nonce = Hex.hexStringToBytes("3ECEA0ECEE")
        val block = MerkleTree.merkleTreeBlock(transactions, priorBlockHash, nonce)
        val tree = MerkleTree[CryptoHash[SHA256]](block)
        tree.toString shouldBe
                """MerkleTreeNode:
                  |:left:
                  |  MerkleTreeNode:
                  |:  left:
                  |    MerkleTreeNode:
                  |:    left:
                  |      0DBA5FB0A30E26E83B2AC5B9E29E1B161E5C1FA7425E73043362938B9824
                  |:    right:
                  |      4861727279207061797320526F62696E20312E303030("Harry pays Robin 1.000")
                  |
                  |:  right:
                  |    MerkleTreeNode:
                  |:    left:
                  |      4D61686172736869207061797320486172727920312E303030("Maharshi pays Harry 1.000")
                  |:    right:
                  |      5072616E7368752070617973204D6168617273686920312E303030("Pranshu pays Maharshi 1.000")
                  |
                  |
                  |:right:
                  |  MerkleTreeNode:
                  |:  left:
                  |    526F62696E2070617973205072616E73687520312E303030("Robin pays Pranshu 1.000")
                  |:  right:
                  |    3ECEA0ECEE
                  |
                  |""".stripMargin
    }

    it should "apply 2" in {
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
        val block: Seq[(Bytes, Boolean)] = MerkleTree.stringsToBlock(strings)
        val tree: MerkleTree[CryptoHash[SHA256]] = MerkleTree(block)
        tree.toString shouldBe
                """MerkleTreeNode:
                  |:left:
                  |  MerkleTreeNode:
                  |:  left:
                  |    MerkleTreeNode:
                  |:    left:
                  |      MerkleTreeNode:
                  |:      left:
                  |        5768656E204920776173206F6E652D616E642D7477656E7479("When I was one-and-twenty")
                  |:      right:
                  |        4920686561726420612077697365206D616E20736179("I heard a wise man say")
                  |
                  |:    right:
                  |      MerkleTreeNode:
                  |:      left:
                  |        476976652063726F776E7320616E6420706F756E647320616E64206775696E656173("Give crowns and pounds and guineas")
                  |:      right:
                  |        427574206E6F7420796F75722068656172742061776179("But not your heart away")
                  |
                  |
                  |:  right:
                  |    MerkleTreeNode:
                  |:    left:
                  |      MerkleTreeNode:
                  |:      left:
                  |        4769766520706561726C73206177617920616E6420727562696573("Give pearls away and rubies")
                  |:      right:
                  |        427574206B65657020796F75722066616E63792066726565("But keep your fancy free")
                  |
                  |:    right:
                  |      MerkleTreeNode:
                  |:      left:
                  |        427574204920776173206F6E652D616E642D7477656E7479("But I was one-and-twenty")
                  |:      right:
                  |        4E6F2075736520746F2074616C6B20746F206D65("No use to talk to me")
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
                  |        5768656E204920776173206F6E652D616E642D7477656E7479("When I was one-and-twenty")
                  |:      right:
                  |        492068656172642068696D2073617920616761696E("I heard him say again")
                  |
                  |:    right:
                  |      MerkleTreeNode:
                  |:      left:
                  |        546865206865617274206F7574206F662074686520626F736F6D("The heart out of the bosom")
                  |:      right:
                  |        576173206E6576657220676976656E20696E207661696E("Was never given in vain")
                  |
                  |
                  |:  right:
                  |    MerkleTreeNode:
                  |:    left:
                  |      MerkleTreeNode:
                  |:      left:
                  |        54697320706169642077697468207369676873206120706C656E7479("Tis paid with sighs a plenty")
                  |:      right:
                  |        416E6420736F6C6420666F7220656E646C65737320727565("And sold for endless rue")
                  |
                  |:    right:
                  |      MerkleTreeNode:
                  |:      left:
                  |        416E64204920616D2074776F2D616E642D7477656E7479("And I am two-and-twenty")
                  |:      right:
                  |        416E64206F68207469732074727565207469732074727565("And oh tis true tis true")
                  |
                  |
                  |
                  |""".stripMargin
        val si = for (x <- tree.getHash; z <- HexEncryption.bytesToHexString(x.bytes)) yield z
        si.unsafeRunSync() shouldBe "B970CD10245D63690368F7EFEA1C0289115DF3A5310A119F53236FA248BAE0FA"
    }

}
