package exam

import exam.Exam.{hundredPrimes, likelyPrime, satisfiesFermatsTest}
import exam.RandomState
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import scala.util.Random

class ExamSpec extends AnyFlatSpec with should.Matchers {

    behavior of "Exam"

    it should "gcd" in {
        Exam.gcd(10, 2) shouldBe 2L
        Exam.gcd(7, 5) shouldBe 1L
        Exam.gcd(5, 7) shouldBe 1L
    }

    it should "get" in {
        val r = RandomState.intRandomStateBoundedSeeded(100)(0L)
        r.get shouldBe 48
        r.get shouldBe 48
    }

    it should "hundredPrimes.count" in {
        hundredPrimes.count(_ < 100) shouldBe 25
        hundredPrimes.count(_ < 200) shouldBe 46
        hundredPrimes.count(_ < 500) shouldBe 95
        hundredPrimes.count(_ < 1000) shouldBe 100
    }

    it should "intRandomStateBoundedSeeded" in {
        val r: RandomState[Int] = RandomState.intRandomStateBoundedSeeded(100)(0L)
        r.get shouldBe 48
        r.next.get shouldBe 19
        r.next.next.get shouldBe 55
        r.lazyList.take(100).forall(x => 0 <= x && x < 100)
    }

    it should "stream" in {
        val m = 5
        val n: BigInt = 541
        val nPrimes = hundredPrimes.count(_ < n)
        val randomState: RandomState[Int] = RandomState.intRandomStateBoundedSeeded(nPrimes)(0L)
        val candidates: Seq[BigInt] = randomState.lazyList.map(x => x % nPrimes).take(m).map(x => hundredPrimes(x))
        candidates shouldBe List(79, 7, 37, 131, 397)
    }

    it should "satisfiesFermatTests" in {
        val m = 5
        val n: BigInt = 541
        val nPrimes = hundredPrimes.count(_ < n)
        val randomState: RandomState[Int] = RandomState.intRandomStateBoundedSeeded(nPrimes)(0L)
        val candidates: Seq[BigInt] = randomState.lazyList.map(x => x % nPrimes).take(m).map(x => hundredPrimes(x))
        candidates.forall(a => satisfiesFermatsTest(a, n)) shouldBe true
    }

    it should "likelyPrime" in {
        likelyPrime(397) shouldBe true
    }

}
