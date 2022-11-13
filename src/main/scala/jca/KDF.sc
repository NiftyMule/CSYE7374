import org.bouncycastle.crypto.digests.SHA256Digest
import org.bouncycastle.crypto.generators.KDF1BytesGenerator
import util.DevRandom

val digest = new SHA256Digest()
val triedBytes = DevRandom.getRandom(8)
triedBytes foreach {
    x =>
        digest.update(x, 0, x.length)
        val kdf = new KDF1BytesGenerator(digest)
        val bytes = new Array[Byte](32)
        kdf.generateBytes(bytes, 0, 32)
        util.Hex.bytesToHexString(bytes)
}
