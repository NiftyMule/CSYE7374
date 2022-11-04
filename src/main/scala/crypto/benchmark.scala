package crypto

import crypto.{MillerRabin, Primes}
import util.Benchmark

import java.math.BigInteger
import java.util.Random
import java.util.function.{Consumer, Supplier}
import scala.util

def discreteLog(a:BigInt, b:BigInt, m:BigInt): Option[BigInt] = {
  var r:BigInt = BigInt(1)
  var i:BigInt = BigInt(0)
  while (i != m) {
    if (r == b) return Some(i) else {
      r = (r * a) mod m
      i = i + 1
    }
  }
  None
}

object benchmark extends App {
  val bitsArray = Seq(10, 20, 40, 80, 160)

  // eSieve
  for (bits <- Seq(10, 20)) {
    val rnd = new Random()
    val numSupplier = new BigInteger(bits, rnd)

    val benchmark: Benchmark[BigInteger] = new Benchmark[BigInteger]("Eratosthenes", null, (t: BigInteger) => Primes.eSieve(t.intValue()), null)
    val time: Double = benchmark.run(numSupplier, 1000)
    println(s"\nEratosthenes Sieve for a $bits bits number on average takes $time millisecs")
  }

  // MillerRabin
  for (bits <- bitsArray) {
    val rnd = new Random()
    val numSupplier = new BigInteger(bits, rnd)

    val benchmark: Benchmark[BigInteger] = new Benchmark[BigInteger]("MillerRabin", null, (t: BigInteger) => MillerRabin.isProbablePrime(t), null)
    val time: Double = benchmark.run(numSupplier, 1000000)
    println(s"\nMiller Rabin method for a $bits bits number on average takes $time millisecs")
  }

  // BigInteger isProbablePrime()
  val certainties = Seq(10, 20, 30, 40)
  for (bits <- bitsArray; certainty <- certainties) {
    val rnd = new Random()
    val numSupplier = new BigInteger(bits, rnd)

    val benchmark: Benchmark[BigInteger] = new Benchmark[BigInteger]("BigInteger", null, (t: BigInteger) => t.isProbablePrime(certainty), null)
    val time: Double = benchmark.run(numSupplier, 1000000)
    println(s"\nBigInteger's isProbablePrime() method with certainty $certainty for a $bits bits number on average takes $time millisecs")
  }

  // unit tests from https://www.hackerrank.com/contests/infinitum11/challenges/discrete-logarithm
  assert(discreteLog(2, 3, 5).contains(3), "Discrete logarithm should be 3")
  assert(discreteLog(3, 2, 5).contains(3), "Discrete logarithm should be 3")
  assert(discreteLog(2, 5, 11).contains(4), "Discrete logarithm should be 4")
  assert(discreteLog(233529, 184091, 329746).contains(57897), "Discrete logarithm should be 57897")
  assert(discreteLog(26161, 23893, 62356).contains(223), "Discrete logarithm should be 223")
  assert(discreteLog(126995, 142647, 270599).contains(204), "Discrete logarithm should be 204")

  // discrete logarithm
  for (bits <- Seq(5, 10, 15, 20, 25, 30)) {
    val rnd = new util.Random()
    val m = BigInt.probablePrime(bits, rnd)
    val a = Prime(m).primitiveRoot

    val bm1 = new Benchmark[(BigInt, BigInt)]("modPow", null, (a:BigInt, m:BigInt) => a.modPow(m - 3, m), null)
    val time1: Double = bm1.run(() => (a, m), 100)
    println(s"\nmodPow() method for a $bits bits number on average takes $time1 millisecs")

    val r = a.modPow(m - 3, m)

    val bm2 = new Benchmark[(BigInt, BigInt, BigInt)]("modPow", null, (a: BigInt, b: BigInt, m: BigInt) => discreteLog(a, b, m), null)
    val time2: Double = bm2.run(() => (a, r, m), 100)
    println(s"\ndiscrete logarithm method for a $bits bits number on average takes $time2 millisecs")
//    println(a)
//    println(r)
//    println(m)
//    println(discreteLog(a, r, m))

    assert(discreteLog(a, r, m).contains(m - 3))
  }
}
