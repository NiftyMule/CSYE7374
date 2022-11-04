import cats.effect.IO
import tsec.hashing.CryptoHash
import tsec.hashing.bouncy.Keccak256
import tsec.hashing.jca.*
import cats.effect.std.Dispatcher
import cats.effect.unsafe.implicits.global
import cats.effect.{Async, IO, Resource, Sync}

import scala.annotation.tailrec
import scala.util.Random

trait MerkleTree() {
  def getHash: IO[CryptoHash[SHA256]]
}

case class MerkleTreeNode(left: MerkleTree, right: MerkleTree) extends MerkleTree {
  def getHash: IO[CryptoHash[SHA256]] = for {
    leftHash <- left.getHash
    rightHash <- right.getHash
    hash <- SHA256.hash[IO](leftHash.bytes ++ rightHash.bytes)
  } yield hash
}

case class MerkleTreeLeaf(content: String) extends MerkleTree {
  def getHash: IO[CryptoHash[SHA256]] = SHA256.hash[IO](content.getBytes)
}

object MerkleTree {
  def apply(seq: Seq[String]): MerkleTree = {
    @tailrec
    def inner(seq: Seq[MerkleTree]): MerkleTree = {
      val newSeq: Seq[MerkleTree] = seq.grouped(2).map(x => if x.length == 1 then x.head else MerkleTreeNode(x.head, x.last)).toSeq
      if newSeq.length > 1 then inner(newSeq) else newSeq.head
    }

    inner(seq.map(MerkleTreeLeaf.apply))
  }
}

object test extends App {
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
  val tree:MerkleTree = MerkleTree(strings)

  tree.getHash.unsafeRunSync().bytes.foreach(x => print("%02X".format(x)))
  println()


  // block chain assignment
  val assignmetTree = LazyList.continually(Seq(
    "00000dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824",
    "Harry pays Robin 1.000",
    "Maharshi pays Harry 1.000",
    "Pranshu pays Maharshi 1.000",
    "Robin pays Pranshu 1.000"
  ))

  var count = 0
  val start = System.nanoTime()
  Random.setSeed(22)
  assignmetTree
    .map(list => list :+ Random.nextBytes(5).foldLeft("")((z:String, x:Byte) => z + "%02X".format(x))) // append nonce
    .find(x => {
      count = count + 1
      val hashBytes = MerkleTree(x).getHash.unsafeRunSync().bytes
      if hashBytes(0) == 0 && hashBytes(1) == 0 && "%02X".format(hashBytes(2)).startsWith("0") then {
        hashBytes.foreach(x => print("%02X".format(x)))
        println()
        println(MerkleTree(x))
        true
      } else false
    })
    .foreach(println)
  val end = System.nanoTime()
  val elapsed = end - start
  println("duration: " + elapsed + "ns")
  println("iteration: " + count)

  // validate
  val tree2:MerkleTree = MerkleTree(assignmetTree.head :+ "C188EB411C")
  tree2.getHash.unsafeRunSync().bytes.foreach(x => print("%02X".format(x)))
  println()
}