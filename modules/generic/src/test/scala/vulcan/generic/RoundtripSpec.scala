package vulcan.generic

import cats.Eq
import cats.implicits._
import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import org.apache.avro.generic.{GenericData, GenericDatumReader, GenericDatumWriter}
import org.apache.avro.io.{DecoderFactory, EncoderFactory}
import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.Assertion
import org.scalatest.funspec.AnyFunSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import shapeless.{:+:, CNil, Coproduct}
import vulcan._
import vulcan.examples._

final class RoundtripSpec extends AnyFunSpec with ScalaCheckPropertyChecks with EitherValues {
  describe("coproduct") {
    it("roundtrip") {
      type Types = CaseClassField :+: Int :+: CaseClassAvroDoc :+: CNil

      implicit val arbitraryTypes: Arbitrary[Types] =
        Arbitrary {
          Gen.oneOf(
            arbitrary[Int].map(n => Coproduct[Types](CaseClassField(n))),
            arbitrary[Int].map(n => Coproduct[Types](n)),
            arbitrary[Option[String]].map(os => Coproduct[Types](CaseClassAvroDoc(os)))
          )
        }

      implicit val eqTypes: Eq[Types] =
        Eq.fromUniversalEquals

      roundtrip[Types]
    }
  }

  describe("derive") {
    it("CaseClassAvroNamespace") { roundtrip[CaseClassAvroNamespace] }
    it("CaseClassField") { roundtrip[CaseClassField] }
    it("SealedTraitCaseClassAvroNamespace") { roundtrip[SealedTraitCaseClassAvroNamespace] }
    it("SealedTraitCaseClassCustom") { roundtrip[SealedTraitCaseClassCustom] }
  }

  def roundtrip[A](
    implicit codec: Codec[A],
    arbitrary: Arbitrary[A],
    eq: Eq[A]
  ): Assertion = {
    forAll { a: A =>
      roundtrip(a)
      binaryRoundtrip(a)
    }
  }

  def roundtrip[A](a: A)(
    implicit codec: Codec[A],
    eq: Eq[A]
  ): Assertion = {
    val avroSchema = codec.schema
    assert(avroSchema.isRight)

    val encoded = codec.encode(a, avroSchema.value)
    assert(encoded.isRight)

    val decoded = codec.decode(encoded.value, avroSchema.value)
    assert(decoded === Right(a))
  }

  def binaryRoundtrip[A](a: A)(
    implicit codec: Codec[A],
    eq: Eq[A]
  ): Assertion = {
    val binary = toBinary(a)
    assert(binary.isRight)

    val decoded = fromBinary[A](binary.value)
    assert(decoded === Right(a))
  }

  def toBinary[A](a: A)(
    implicit codec: Codec[A]
  ): Either[AvroError, Array[Byte]] =
    codec.schema.flatMap { schema =>
      codec.encode(a, schema).map { encoded =>
        val baos = new ByteArrayOutputStream()
        val serializer = EncoderFactory.get().binaryEncoder(baos, null)
        new GenericDatumWriter[Any](schema)
          .write(encoded, serializer)
        serializer.flush()
        baos.toByteArray()
      }
    }

  def fromBinary[A](bytes: Array[Byte])(
    implicit codec: Codec[A]
  ): Either[AvroError, A] =
    codec.schema.flatMap { schema =>
      val bais = new ByteArrayInputStream(bytes)
      val deserializer = DecoderFactory.get().binaryDecoder(bais, null)
      val read =
        new GenericDatumReader[Any](
          schema,
          schema,
          new GenericData
        ).read(null, deserializer)

      codec.decode(read, schema)
    }
}
