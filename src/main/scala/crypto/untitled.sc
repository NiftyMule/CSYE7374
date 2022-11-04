import crypto.{MillerRabin, Primes}
import util.Benchmark

import java.math.BigInteger
import java.util.Random
import java.util.function.{Consumer, Supplier}

val bitsArray = Seq(10, 20, 40, 80, 160)
val rnd = new Random()

// eSieve
for (bits <- Seq(10, 20)) {
  val rnd = new Random()
  val numSupplier = new BigInteger(bits, rnd)

  val benchmark: Benchmark[BigInteger] = new Benchmark[BigInteger]("Eratosthenes", null, (t: BigInteger) => Primes.eSieve(t.intValue()), null)
  val time: Double = benchmark.run(numSupplier, 300)
  println(s"\nEratosthenes Sieve for a $bits bits number on average takes $time millisecs")
}

// MillerRabin
for (bits <- bitsArray) {
  val rnd = new Random()
  val numSupplier = new BigInteger(bits, rnd)

  val benchmark: Benchmark[BigInteger] = new Benchmark[BigInteger]("MillerRabin", null, (t: BigInteger) => MillerRabin.isProbablePrime(t.intValue()), null)
  val time: Double = benchmark.run(numSupplier, 1000)
  println(s"\nMiller Rabin method for a $bits bits number on average takes $time millisecs")
}

// BigInteger isProbablePrime()
for (bits <- bitsArray) {

}