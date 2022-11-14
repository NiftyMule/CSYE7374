package util

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.freespec.AsyncFreeSpec
import org.scalatest.matchers.should
import scala.util.{Success, Try}

class DevRandomSpec extends AsyncFreeSpec with AsyncIOSpec with should.Matchers {

    "DevRandomSpec" - {


        "getRandom" in {
            val ry: IO[Array[Byte]] = DevRandom.getRandom(8)
            ry.asserting(r => r.length shouldBe 8)
        }
    }

}
