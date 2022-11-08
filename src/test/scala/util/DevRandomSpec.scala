package util

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import scala.util.{Success, Try}

class DevRandomSpec extends AnyFlatSpec with should.Matchers {

    behavior of "DevRandomSpec"

    it should "getRandom" in {
        val ry: Try[Array[Byte]] = DevRandom.getRandom(8)
        ry should matchPattern { case Success(_) => }
        ry.get.length shouldBe 8
    }

}
