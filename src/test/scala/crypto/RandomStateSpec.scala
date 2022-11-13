package crypto

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import fs2.Stream
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should
import util.RandomState

class RandomStateSpec extends AsyncFreeSpec with AsyncIOSpec with should.Matchers {

    "RandomState" - {
        "next" in {
            val r = RandomState(0L).next
            r.value(BigInt(1000)) shouldBe 339
        }

        "object lazyList" in {
            val rs = util.RandomState.lazyList(0L)
            val xs = rs.map(_.value(BigInt(1000))).take(10).toList
            xs.head shouldBe BigInt(180)
            xs.tail.head shouldBe BigInt(162)
        }

        "class lazyList" in {
            val r = RandomState(0L)
            val rs = r.lazyList
            val xs = rs.map(_.value(BigInt(1000))).take(10).toList
            xs.head shouldBe BigInt(180)
            xs.tail.head shouldBe BigInt(162)
        }

        "random stream" in {
            import cats.effect.unsafe.implicits.global
            val r = RandomState(0L)
            import util.StreamUtils.*
            val randomStream: Stream[IO, RandomState] = Stream.iterate[IO, RandomState](r)(x => x.next)
            val result: IO[List[Long]] = randomStream.map(r => r.long(100L)).take(20).compile.toList
            result.asserting { ls =>
                ls.length shouldBe 20
                ls.head shouldBe 29
            }
        }
    }

}
