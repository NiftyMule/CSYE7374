import org.bouncycastle.crypto.digests.SHA256Digest
import org.bouncycastle.crypto.generators.KDF1BytesGenerator

val digest = new SHA256Digest()
val kdf = new KDF1BytesGenerator(digest)
val bytes = new Array[Byte](32)
kdf.generateBytes(bytes, 0, 32)
util.Hex.bytesToHexString(bytes)