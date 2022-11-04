package crypto

import crypto.RandomState.lazyList
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class RandomStateSpec extends AnyFlatSpec with should.Matchers {

  behavior of "RandomState"

  it should "next" in {
    val r = RandomState(0L).next
    r.value(BigInt(1000)) shouldBe 339
  }

  it should "object lazyList" in {
    val rs = lazyList(0L)
    val xs = rs.map(_.value(BigInt(1000))).take(10).toList
    xs.head shouldBe BigInt(180)
    xs.tail.head shouldBe BigInt(162)
  }

  it should "class lazyList" in {
    val r = RandomState(0L)
    val rs = r.lazyList
    val xs = rs.map(_.value(BigInt(1000))).take(10).toList
    xs.head shouldBe BigInt(180)
    xs.tail.head shouldBe BigInt(162)
  }

}
