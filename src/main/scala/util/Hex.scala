package util

object Hex {
    /**
     * Show the given bytes as hexadecimal text.
     *
     * @param bytes an Array[Byte].
     * @return an IO of String.
     */
    def bytesToHexString(bytes: Array[Byte]): String = {
        val sb = new StringBuilder
        for (b <- bytes) yield sb.append(String.format("%02X", b))
        sb.toString()
    }

    /**
     * Decode the given String in hexadecimal as an array of Byte.
     * 
     * TESTME not used.
     *
     * @param hex the hex String.
     * @return Array[Byte].
     */
    def hexStringToBytes(hex: String): Array[Byte] = {
        // CONSIDER getting the byte array a different way that doesn't require the drop.
        val q: Array[Byte] = BigInt(hex, 16).toByteArray
        if (q.length > hex.length / 2) q.drop(1) else q
    }
}
