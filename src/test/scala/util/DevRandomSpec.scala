package util

import cats.effect.IO
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import scala.util.{Success, Try}

class DevRandomSpec extends AnyFlatSpec with should.Matchers {

    behavior of "DevRandomSpec"

    it should "getRandom" in {
        val ry: IO[Array[Byte]] = DevRandom.getRandom(8)
        import cats.effect.unsafe.implicits.global
        ry.unsafeRunSync().length shouldBe 8
    }

}
