package jca

import cats.effect.std.Dispatcher
import cats.effect.unsafe.implicits.global
import cats.effect.{Async, IO, Resource, Sync}
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import jca.MerkleTree.Bytes
import scala.annotation.tailrec
import scala.util.Random
import tsec.hashing.CryptoHash
import tsec.hashing.bouncy.Keccak256
import tsec.hashing.jca.*
import util.RandomState

/**
 * This class was originally contributed by Zhilue Wang (NiftyMule on github).
 *
 * @tparam H the underlying hash type.
 */
trait MerkleTree[H] {
    /**
     * Method to get the hash of this Merkle Tree.
     *
     * @return an IO[H]
     */
    def getHash: IO[H]

    /**
     * Method to render a MerkleTree with indentation given by x.
     *
     * @param x the indentation.
     * @return a String representing the rendering of this MerkleTree.
     */
    def render(x: Int): String
}

/**
 * Case class to define an inner node of a MerkleTree.
 *
 * @param left         the left sub-tree.
 * @param right        the right sub-tree.
 * @tparam H the underlying hash type.
 */
case class MerkleTreeNode[H: Hashable](left: MerkleTree[H], right: MerkleTree[H]) extends MerkleTree[H] {
    private val hh = implicitly[Hashable[H]]

    def getHash: IO[H] = for {
        leftHash <- left.getHash
        rightHash <- right.getHash
        hash <- hh.hash(hh.bytes(leftHash) ++ hh.bytes(rightHash))
    } yield hash

    override def toString: String = render(0)

    def render(x: Int): String = {
        val padding = MerkleTree.pad(x)
        s"${padding}MerkleTreeNode:\n:${padding}left:\n${left.render(x + 1)}\n:${padding}right:\n${right.render(x + 1)}\n"
    }
}

/**
 * Case class to define a leaf node of a MerkleTree.
 *
 * @param content      the content of the leaf (an array of Byte).
 * @tparam H the underlying hash type.
 */
case class MerkleTreeLeaf[H: Hashable](content: Array[Byte]) extends MerkleTree[H] {
    def getHash: IO[H] = implicitly[Hashable[H]].hash(content)

    override def render(x: Int): String = MerkleTree.pad(x) + String(content, StandardCharsets.UTF_8)

    override def toString: String = render(0)
}

object MerkleTreeLeaf {
    /**
     * Method to create a MerkleTreeLeaf with String content.
     *
     * @param content      a String.
     * @tparam H the underlying hash type.
     * @return a MerkleTreeLeaf[H].
     */
    def apply[H: Hashable](content: String): MerkleTreeLeaf[H] = apply(content.getBytes)
}

object MerkleTree {
    type Bytes = Array[Byte]

    /**
     * Method to construct a MerkleTree from a sequence of byte arrays (typically corresponding to strings).
     *
     * @param bas a sequence of byte arrays.
     * @tparam H the underlying hash type.
     * @return a MerkleTree[H].
     */
    def apply[H: Hashable](bas: Seq[Bytes]): MerkleTree[H] = {
        //        @tailrec
//        def myInner(r: MerkleTree[H], hms: Seq[MerkleTree[H]]): MerkleTree[H] = hms match {
//            case Nil => r
//            case hm :: Nil => hm
//            case
//        }
@tailrec
def inner(hms: Seq[MerkleTree[H]]): MerkleTree[H] = {
    val newSeq: Seq[MerkleTree[H]] = hms.grouped(2).map(x => if x.length == 1 then x.head else MerkleTreeNode(x.head, x.last)).toSeq
    if newSeq.length > 1 then inner(newSeq) else newSeq.head
}

        inner(bas.map(MerkleTreeLeaf.apply))
    }

    /**
     * Method to "mine" a block such that its hash starts with 20 zero bits.
     *
     * @param elements       a sequence of Strings to be encoded as a Merkle Tree in this block.
     * @param priorBlockHash the hash of the prior block.
     * @param nBytesNonce    The number of bytes to be used for the nonce.
     * @param miningFunction a function which takes a byte array representing a hash, and returns true if the hash conforms.
     * @param randomState    an instance of RandomState.
     * @tparam H the underlying hash type.
     * @return an Option of tuple of Array[Byte]: nonce and hash for first nonce that results in 20 zero bits.
     */
    def mineMerkleTreeBlock[H: Hashable](elements: Seq[String], priorBlockHash: Bytes, nBytesNonce: Int)(miningFunction: Bytes => Boolean)(randomState: RandomState): Option[(Bytes, Bytes)] = {
        val hh = implicitly[Hashable[H]]

        def rejectNonceWithHash(nwh: (Bytes, Bytes)): Boolean = !miningFunction(nwh._2)

        def nonceAndHash(nonce: Bytes, tree: MerkleTree[H]): (Bytes, Bytes) = nonce -> hh.bytes(tree.getHash.unsafeRunSync())

        val block = priorBlockHash +: (elements map (_.getBytes))
        val nonces = randomState.lazyList map (r => r.bytes(nBytesNonce))
        val candidates = nonces map (nonce => nonce -> MerkleTree[H](block :+ nonce))
        (candidates map nonceAndHash dropWhile rejectNonceWithHash take 1).headOption
    }

    def pad(x: Int): String = "  " * x
}

object test extends App {


//    val file = new File("/dev/random")
//
//    val source: Any = Source.fromFile(file)

    val z: SecureRandom = new SecureRandom()

    println(z.nextLong())

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
    tree.getHash.unsafeRunSync().bytes.foreach(x => print("%02X".format(x)))
    println()

    // Method to check if bytes begins with 20 bits.
    def checkBlockHash(bytes: Bytes): Boolean = bytes(0) == 0 && bytes(1) == 0 && (bytes(2) & 0xF0) == 0

    // block chain assignment
    val transactions = Seq(
        "Harry pays Robin 1.000",
        "Maharshi pays Harry 1.000",
        "Pranshu pays Maharshi 1.000",
        "Robin pays Pranshu 1.000"
    )

    val start = System.nanoTime()
    val priorBlockHash = "00000dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824".getBytes()
    val result = MerkleTree.mineMerkleTreeBlock(transactions, priorBlockHash, 5)(checkBlockHash)(RandomState(3L))
    val end = System.nanoTime()
    val elapsed = end - start
    println("duration: " + elapsed + "ns")
    result match {
        case Some(nonce, hash) =>
            println(util.Hex.bytesToHexString(nonce))
            println(util.Hex.bytesToHexString(hash))
        case _ => System.err.println("Failed to find a suitable hash")
    }
}