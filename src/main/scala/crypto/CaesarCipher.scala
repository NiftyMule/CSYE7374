package crypto

import crypto.CaesarCipher.{doShift, preparePlainText}
import crypto.Histogram
import crypto.Histogram.{English, shiftedEnglishHistograms}
import parse.EnglishParser
import scala.annotation.unused
import scala.collection.mutable
import scala.util.Try
import scala.util.matching.Regex

/**
 * Case class to represent a Caesar cipher.
 *
 * TODO consider re-writing this in terms of MultiShiftCipher with one shift only.
 *
 * @param shift the number of places each letter will be shifted.
 */
case class CaesarCipher(shift: Int) extends Cipher {
    def encrypt(w: CharSequence): CharSequence = for (c <- preparePlainText(w.toString)) yield doShift(c, shift)

    def decrypt(w: CharSequence): CharSequence = for (c <- w.toString) yield doShift(c, -shift)
}

object CaesarCipher {

  /**
   * Method to remove white space and force to upper case.
   *
   * @param w the string to be prepared.
   * @return a string consisting only of the characters A...Z.
   */
  def preparePlainText(w: CharSequence): String = {
    val sb = new mutable.StringBuilder()
    val chars = w.toString.toUpperCase
    for (m <- """\w+""".r findAllMatchIn chars; x <- m.group(0)) sb.append(x)
    sb.toString()
  }

  @unused
  /**
   * Method to render cipher text in a consistent way
   *
   * @param w the cipher text
   * @return a String.
   */
  def showCipherText(w: String): String = {
    w // TODO put into blocks of five
  }

  /**
   * Method to guess the best candidates for Caesar Cipher shift that could have produced the given Histogram.
   *
   * @param histogram the histogram (of a cipher text) for which you want the best (Caesar) shifts.
   * @return a sorted list of tbe best shift guesses.
   */
  def bestShifts(histogram: Histogram): Seq[Int] = {
    val (_, shifts) = shiftedEnglishHistograms.sortBy((h, _) => histogram.comparer(h)).unzip
    shifts
  }

  /**
   * Method to guess the value of the Caesar Cipher shift.
   * NOTE: this is only a guess. It isn't any sort of guarantee.
   *
   * @param cipherText the cipher text which you would like to decrypt.
   * @return an optional Int representing the best shift. If None, then we couldn't find any suitable shift.
   */
  def guessShift(cipherText: CharSequence): Option[Int] = {
    bestShifts(Histogram(cipherText)).find(x => decryptAndParse(cipherText, x).isDefined)
  }

  /**
   * Method to shift an upper-case English character by a given number of places.
   *
   * @param x     an
   * @param shift the number of places to shift
   * @return
   */
  def doShift(x: Char, shift: Int): Char = ((x.toInt + shift - A + radix) % radix + A).toChar

  private def decryptAndParse(cipherText: CharSequence, shift: Int): Option[Seq[String]] = {
    val plainText = CaesarCipher(shift).decrypt(cipherText)
    englishParser.parseEnglishWords(plainText).toOption
  }

  private val A = 'A'

  private val radix = 26

  private val englishParser = new EnglishParser
}
