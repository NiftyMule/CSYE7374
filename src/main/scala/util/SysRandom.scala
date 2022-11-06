package util

import java.nio.file.{Files, Paths}

object SysRandom {
  def get(numOfBytes: Int): Array[Byte] = {
    val stream = Files.newInputStream(Paths.get("/dev/random"))
    stream.readNBytes(numOfBytes)
  }
}
