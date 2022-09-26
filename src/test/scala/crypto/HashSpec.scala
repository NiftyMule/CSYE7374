package crypto

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

//noinspection DeprecatedAlphanumericInfixCall
class HashSpec extends AnyFlatSpec with should.Matchers {

    import Block.requiredLength

    behavior of "Hash"

    it should "hash" in {
        val iv: Block = Block(LazyList.continually(0xCC) map (_.toByte) take 16)
        val target = new Hash() {
            def hash(message: BlockMessage): Block = {
                import crypto.BlockMessage.Xor
                def compress(as: Block, bs: Block): Block = as xor bs

                message.foldLeft(iv)(compress)
            }
        }
        target.hash(BlockMessage(new Array[Byte](16))).toString shouldBe "cccccccccccccccccccccccccccccccc"
        target.hash(BlockMessage(iv.bytes)).toString shouldBe "00000000000000000000000000000000"
        val xs: Block = Block(LazyList.continually(0xFF) map (_.toByte) take 16)
        target.hash(BlockMessage(xs.bytes)).toString shouldBe "33333333333333333333333333333333"
    }

    behavior of "BlockMessage"

    it should "pad1" in {
        val block = new Array[Byte](8)
        val result = BlockMessage.pad(10, block)
        result.length shouldBe 10
    }

    it should "pad2" in {
        val block = "Hello World!".getBytes
        val result = BlockMessage.pad(20, block)
        result.length shouldBe 20
        result(0) shouldBe 'H'
        result(19) shouldBe 0
    }

    it should "apply" in {
        val b: BlockMessage = BlockMessage.apply("The quick brown fox jumps over the lazy dog")
        b.blocks.length shouldBe 3
    }

    it should "toString" in {
        val bm = BlockMessage("A")(8)
        bm.toString shouldBe "4100000000000000"
    }

    behavior of "BlockHash"

    it should "hash" in {
        import crypto.BlockMessage.Xor
        val target: BlockHash = BlockHash((r, b) => r.xor(b), Block(new Array[Byte](16)))
        val result: Block = target.hash(BlockMessage.apply("The quick brown fox jumps over the lazy dog"))
        result.toString shouldBe "5a623d6c7a7a7d337c6f6a040a054e54"
    }

}
