package assignments

import jca.{EncryptionUTF8AES128CTR, HexEncryption}

import java.security.{KeyPair, PrivateKey, PublicKey, Security, Signature}
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo
import org.bouncycastle.crypto.engines.RSAEngine
import org.bouncycastle.openssl.jcajce.{JcaPEMKeyConverter, JcePEMDecryptorProviderBuilder}
import org.bouncycastle.openssl.{PEMKeyPair, PEMParser}

import java.io.{File, FileWriter}
import java.nio.file.{Files, Paths}
import javax.crypto.Cipher
import scala.io.Source

def loadFile(relPath:String): String = {
  val path = getClass.getClassLoader.getResource(relPath).getPath
  val file = Source.fromFile(path)
  val result = file.getLines().mkString
  file.close()
  result
}

def writeToFile(relPath:String, content:Array[Byte]): Unit = {
  val path = Paths.get(relPath)
  if !Files.exists(path) then {
    Files.createDirectories(path.getParent)
    Files.createFile(path)
  }
  Files.write(path, content)
}

// This will load both private/public keys
//   as their info(e.g. prime factors, modulo) are all stored in private key .pem file
def loadKeyPair(privateKeyPath:String): KeyPair = {
  val path = getClass.getClassLoader.getResource(privateKeyPath).getPath
  val pemParser = new PEMParser(Source.fromFile(path).bufferedReader())
  val keyPair = pemParser.readObject().asInstanceOf[PEMKeyPair]

  val converter = new JcaPEMKeyConverter().setProvider("BC")
  converter.getKeyPair(keyPair)
}

def loadPublicKey(keyPath:String): PublicKey = {
  val path = getClass.getClassLoader.getResource(keyPath).getPath
  val pemParser = new PEMParser(Source.fromFile(path).bufferedReader())
  val publicKeyInfo = pemParser.readObject().asInstanceOf[SubjectPublicKeyInfo]

  val converter = new JcaPEMKeyConverter().setProvider("BC")
  converter.getPublicKey(publicKeyInfo)
}

def signData(data:Array[Byte], key:PrivateKey): Array[Byte] = {
  val signer = Signature.getInstance("SHA256WithRSA", "BC")
  signer.initSign(key)
  signer.update(data)
  signer.sign()
}

def printHex(data: Array[Byte]): Unit = {
  data.foreach(x => print("%02X".format(x)))
  println()
}

object assignment6 extends App {
  val content = loadFile("crypto/assignment6msg")


  // digitally sign a document
  Security.addProvider(new BouncyCastleProvider)
  val privateKey = loadKeyPair("crypto/private_key.pem").getPrivate
  val signature = signData(content.getBytes, privateKey)
  printHex(signature)
  writeToFile("src/main/resources/crypto/sign.sha256", signature)

  val publicKey = loadPublicKey("crypto/public_key.pem")
  assert(loadKeyPair("crypto/private_key.pem").getPublic == publicKey)


  // hybrid encryption
  import cats.effect.unsafe.implicits.global

  val encryptor = EncryptionUTF8AES128CTR

  val result = for {
    tuple <- encryptor.encryptWithRandomKey(content)
    key = tuple._1
    hex = tuple._2
  } yield (key, hex)

  val (key, hex) = result.unsafeRunSync()
  println(key -> hex)
  println(encryptor.decryptHex(key, hex).unsafeRunSync())

  // write cipher text to file
  writeToFile("src/main/resources/crypto/cipher", hex.getBytes())

  // encrypt symmetric key with prof's public key
  val profPublicKey = loadPublicKey("crypto/prof_key.pem")
  val rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
  rsaCipher.init(Cipher.ENCRYPT_MODE, profPublicKey)
  val encryptedKey = rsaCipher.doFinal(key.getBytes())
  
  printHex(encryptedKey)
  writeToFile("src/main/resources/crypto/encryptedRawKey", encryptedKey)
}
