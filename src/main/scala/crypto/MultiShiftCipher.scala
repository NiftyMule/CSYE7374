package crypto

import crypto.CaesarCipher.{doShift, preparePlainText}
import crypto.Histogram
import crypto.Histogram.{English, shiftedEnglishHistograms}
import parse.EnglishParser

import scala.util.matching.Regex
import scala.util.{Random, Try}

/**
 * Case class to represent a Substitution cipher with multiple shifts.
 *
 * @param shifts the shift for each character in the key.
 */
case class MultiShiftCipher(shifts: Seq[Int]) extends Cipher {
  val keyLength: Int = shifts.length

    def encrypt(w: CharSequence): CharSequence = {
        val s = preparePlainText(w)
        (for (i <- 0 until s.length()) yield doShift(s.charAt(i), shifts(i % keyLength))).mkString
    }

    def decrypt(w: CharSequence): CharSequence =
        (for (i <- 0 until w.length()) yield doShift(w.charAt(i), -shifts(i % keyLength))).mkString
}

object MultiShiftCipher {
  def VigenereCipher(key: String): MultiShiftCipher = MultiShiftCipher(for (x <- key.toUpperCase) yield x - 'A')

  def OneTimePad(random: Random, n: Int): MultiShiftCipher =
    MultiShiftCipher(LazyList.continually(random.nextInt(26)).take(n).toList)
}

