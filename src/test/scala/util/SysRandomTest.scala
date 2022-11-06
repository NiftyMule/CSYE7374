package util;

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class SysRandomTest extends AnyFlatSpec with should.Matchers
{
    behavior of "SysRandom"

    it should "get specific number of bytes from random file (mac)" in {
        SysRandom.get(5).length shouldBe 5
    }

    it should "return different result" in {
        val result1 = SysRandom.get(3)
        val result2 = SysRandom.get(3)
        println(result1.mkString("Array(", ", ", ")"))
        println(result2.mkString("Array(", ", ", ")"))

        result1 should not be result2
    }
}