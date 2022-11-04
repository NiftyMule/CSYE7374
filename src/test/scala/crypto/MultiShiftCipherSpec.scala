package crypto

import crypto.MultiShiftCipher.*
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

import scala.util.Random

class MultiShiftCipherSpec extends AnyFlatSpec with should.Matchers {

  private val cipher = VigenereCipher("ABCD")
  private val cipherText = "AAAAAAA"

  behavior of "MultiShiftCipher"

  it should "encrypt" in {
    //noinspection SpellCheckingInspection
    cipher.encrypt("Hello World") shouldBe "HFNOOXQULE"
  }

  it should "decrypt" in {
    //noinspection SpellCheckingInspection
    cipher.decrypt("HFNOOXQULE") shouldBe "HELLOWORLD"
  }

  it should "VigenereCipher" in {
    VigenereCipher("ABCD") shouldBe new MultiShiftCipher(Seq(0, 1, 2, 3))
  }

  it should "OneTimePad" in {
    val oneTimePad = OneTimePad(new Random(), 20)
    oneTimePad.shifts.length shouldBe 20
  }

    it should "DecodeCipherText" in {
        val bestShifts = for {
            i <- 0 until 6
            s = cipherText.drop(i).grouped(6).map(_.head).mkString
        } yield {
            val shifts = CaesarCipher.bestShifts(Histogram(s)).take(15)
            println(shifts)
            shifts
        }

        println(bestShifts)
        var set = scala.collection.mutable.Set[(Seq[Int], Seq[String])]()

//        for {
//            s1 <- bestShifts(0)
//            s2 <- bestShifts(1)
//            s3 <- bestShifts(2)
//        } {
//            val key = List(s1, s2, s3)
//            val cipher = MultiShiftCipher(key)
//
//            val parser = new EnglishParser
//            val parsedResult2 = parser.parseEnglishWords(cipher.decrypt("GHX"))
//            if parsedResult2.isSuccess &&
//              parsedResult2.get.length == 1
//            then {
//                set += (Seq(s1, s2, s3), parsedResult2.get)
//            }
//        }
//
//        set.foreach(println)

//        for {
//            ss4 <- 0 until 26
//            ss5 <- 0 until 26
//            ss6 <- 0 until 26
//        } {
//            val key = List(13, 0, 19, ss4, ss5, ss6)
//            val cipher = MultiShiftCipher(key)
//            val decrypted = cipher.decrypt(cipherText)
//
//            val parser = new EnglishParser
//            val parsed = parser.parseEnglishWords(decrypted)
//            print(key)
//            println(parsed)
//        }

        val key = List(13, 0, 19, 8, 21, 4).map(_ + 'A').map(_.toChar).mkString
        println(key)
        val cipher = VigenereCipher(key)
        println(cipher.decrypt(cipherText))
    }

}
