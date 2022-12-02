package crypto

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import fs2.Stream
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should

class CryptoRandomStateSpec extends AsyncFreeSpec with AsyncIOSpec with should.Matchers {

    "RandomState" - {
        "next/long" in {
            val r = CryptoRandomState().next
            r.long() shouldNot be(r.next.long())
            r.long() shouldNot be(r.next.long())
            r.long() shouldNot be(r.next.next.long())
            r.long() shouldNot be(r.next.next.next.long())
        }
        "next/long2" in {
            val r = CryptoRandomState().next
            val l1 = r.long(Integer.MAX_VALUE)
            l1 >= 0 && l1 <= Integer.MAX_VALUE shouldBe true
            val l2 = r.long(Integer.MAX_VALUE)
            l2 >= 0 && l2 <= Integer.MAX_VALUE shouldBe true
            val l3 = r.long(Integer.MAX_VALUE)
            l3 >= 0 && l3 <= Integer.MAX_VALUE shouldBe true
        }

        "class lazyList" in {
            val rs: LazyList[CryptoRandomState] = CryptoRandomState().lazyList
            val xs: List[Long] = rs.map(_.long()).take(100).toList
            xs.distinct.size shouldBe xs.size
        }
    }
}
