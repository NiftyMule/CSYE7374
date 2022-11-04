import util.Hex

import java.io.FileInputStream

val z = new FileInputStream("/dev/random")
val n = 32
val bytes = new Array[Byte](n)
val read = z.read(bytes, 0, bytes.length)
Hex.bytesToHexString(bytes)
