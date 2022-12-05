package crypto

import scala.util.{Try, Using}

object CryptoTools:

    //    private val dictionaryFile = "/usr/share/dict/web2"
    private val dictionaryFile = "crypto/english"
    // NOTE: this is only expected to work for Macs.
    val triedDictionary: Try[Set[String]] = getDictionary(dictionaryFile)

//  assert(triedDictionary.isSuccess, s"Dictionary file could not be opened because of $triedDictionary")

    //    private def getDictionary(file: String): Try[Set[String]] = Using(scala.io.Source.fromFile(file)) { s => s.getLines.toSet }
    private def getDictionary(file: String): Try[Set[String]] = Using(scala.io.Source.fromResource(file)) { s => s.getLines.toSet }

    def isEnglishWordAnyCase(w: String): Boolean = isEnglishWordExactCase(w.toLowerCase)

    def isEnglishWordExactCase(w: String): Boolean = triedDictionary.map(d => d.contains(w) || d.contains(nameify(w))).getOrElse(false)

    def parseEnglish(w: String): Option[Int] =
        val min2 = math.min(w.length, 15)
        val min1 = math.min(w.length, 3)
        val lowerCase = w.toLowerCase
        val result: Seq[Int] = for (prefixLength <- min1 to min2; if isEnglishWordAnyCase(lowerCase.substring(0, prefixLength))) yield prefixLength
        result.headOption

    def nameify(w: String): String = w.head.toUpper + w.tail

