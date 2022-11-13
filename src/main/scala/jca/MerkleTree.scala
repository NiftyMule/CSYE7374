package jca

import cats.effect.std.Dispatcher
import cats.effect.unsafe.implicits.global
import cats.effect.{Async, IO, Resource, Sync}
import crypto.Entropy
import java.nio.charset.{Charset, StandardCharsets}
import java.security
import java.security.SecureRandom
import scala.annotation.tailrec
import scala.util.{Random, Try}
import tsec.hashing.CryptoHash
import tsec.hashing.bouncy.Keccak256
import tsec.hashing.jca.*
import util.{DevRandom, RandomState, StreamUtils}

/**
 * Type alias for an array of Byte.
 */
type Bytes = Array[Byte]

/**
 * Type alias for a sequence of tuples, each of Bytes and Boolean.
 * The purpose of the Boolean is to indicate if the Bytes can be decoded into a renderable String.
 */
type Block = Seq[(Bytes, Boolean)]

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
 * @param content        the content of the leaf (an array of Byte).
 * @param defaultCharset true if the content can be decoded with defaultCharset.
 * @tparam H the underlying hash type.
 */
case class MerkleTreeLeaf[H: Hashable](content: Bytes, defaultCharset: Boolean = false) extends MerkleTree[H] {
    def getHash: IO[H] = implicitly[Hashable[H]].hash(content)

    override def render(x: Int): String = {
        val decodedString = s"""("${String(content)}")"""
        MerkleTree.pad(x) + util.Hex.bytesToHexString(content) + (if (defaultCharset) decodedString else "")
    }

    override def toString: String = render(0)
}

object MerkleTreeLeaf {
    /**
     * Method to create a MerkleTreeLeaf with String content.
     *
     * NOTE: in practice, this is never called.
     *
     * @param content a String.
     * @tparam H the underlying hash type.
     * @return a MerkleTreeLeaf[H].
     */
    def apply[H: Hashable](content: String): MerkleTreeLeaf[H] = apply(content.getBytes, true)
}

object MerkleTree {

    import cats.effect.IO
    import cats.effect.unsafe.implicits.global
    import fs2.{Pure, Stream}
    import util.StreamUtils.*

    /**
     * Method to construct a MerkleTree from a sequence of byte arrays (typically corresponding to strings).
     *
     * @param block a sequence of tuples of byte array and boolean.
     * @tparam H the underlying hash type.
     * @return a MerkleTree[H].
     */
    def apply[H: Hashable](block: Block): MerkleTree[H] = {
        @tailrec
        def inner(hms: Seq[MerkleTree[H]]): MerkleTree[H] = {
            val newSeq: Seq[MerkleTree[H]] = hms.grouped(2).map(x => if x.length == 1 then x.head else MerkleTreeNode(x.head, x.last)).toSeq
            if newSeq.length > 1 then inner(newSeq) else newSeq.head
        }

        val f: (Bytes, Boolean) => MerkleTreeLeaf[H] = MerkleTreeLeaf.apply
        inner(block.map(f.tupled))
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
     * @return an IO or an Option of tuple of Array[Byte]: nonce and hash for first nonce that results in 20 zero bits.
     */
    def mineMerkleTreeBlock[H: Hashable](elements: Seq[String], priorBlockHash: Bytes, nBytesNonce: Int)(miningFunction: Bytes => Boolean)(randomState: RandomState): IO[Option[(Bytes, Bytes)]] = {
        val hh = implicitly[Hashable[H]]

        def rejectNonceWithHash(nwh: (Bytes, Bytes)): Boolean = !miningFunction(nwh._2)

        def nonceAndHash(nonce: Bytes, tree: MerkleTree[H]): IO[(Bytes, Bytes)] = for (hash <- tree.getHash) yield nonce -> hh.bytes(hash)

        val randomStream = Stream.iterate[IO, RandomState](randomState)(x => x.next)
        val nonces = randomStream map (x => x.bytes(nBytesNonce))
        val candidates = nonces map (nonce => nonce -> MerkleTree[H](merkleTreeBlock(elements, priorBlockHash, nonce)))
        val bbs = (candidates map nonceAndHash).evalMap(identity).dropWhile(rejectNonceWithHash)
        for (xs <- bbs.take(1).compile.toList) yield xs.headOption
    }

    /**
     * Method to encode a sequence of Strings as a sequence of (Bytes, Boolean) tuples, as required by MerkleTree.apply.
     *
     * @param ws a sequence of String.
     * @return a Block where all the booleans are true.
     */
    def stringsToBlock(ws: Seq[String]): Block = ws map (w => w.getBytes -> true)

    /**
     * Method to encode a sequence of Strings, together with a prior block hash and a nonce, as a sequence of (Bytes, Boolean) tuples, as required by MerkleTree.apply.
     *
     * @param elements       a sequence of String.
     * @param priorBlockHash the hash of the prior block.
     * @param nonce          the nonce to be used.
     * @return a Block.
     */
    def merkleTreeBlock(elements: Seq[String], priorBlockHash: Bytes, nonce: Bytes): Block =
        (priorBlockHash -> false) +: (stringsToBlock(elements) :+ (nonce -> false))

    def pad(x: Int): String = "  " * x
}

object test extends App {

    (for (bs <- Entropy.getEntropy(64); r = new SecureRandom(bs); _ = println(s"random long: ${r.nextLong()}")) yield ()).unsafeRunSync()

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

    val block: Block = MerkleTree.stringsToBlock(strings)
    val tree: MerkleTree[CryptoHash[SHA256]] = MerkleTree(block)
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
    val result = MerkleTree.mineMerkleTreeBlock(transactions, priorBlockHash, 5)(checkBlockHash)(RandomState(1L)).unsafeRunSync()
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
