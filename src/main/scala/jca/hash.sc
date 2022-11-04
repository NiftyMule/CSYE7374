import org.bouncycastle.crypto
import org.bouncycastle.crypto.digests.SHA256Digest
import org.bouncycastle.jce.provider.BouncyCastleProvider

import java.security.Security

Security.addProvider(new BouncyCastleProvider())



val messageDigest = new SHA256Digest()

val plaintext = "hello world"

var resBuf = new Array[Byte](messageDigest.getDigestSize)

messageDigest.update(plaintext.getBytes, 0, plaintext.getBytes.length)
messageDigest.doFinal(resBuf, 0)

resBuf.foreach(x => print("%02X" format x))
