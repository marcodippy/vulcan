/*
 * Copyright 2019 OVO Energy Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package vulcan

import cats.data.{Chain, NonEmptyChain}
import cats.implicits._
import cats.Show

/**
  * Custom properties which can be included in a schema.
  *
  * Use [[Props.one]] to create an instance, and
  * [[Props#add]] to add more properties.
  */
sealed abstract class Props {

  /**
    * Returns a new [[Props]] instance including a
    * property with the specified name and value.
    *
    * The value is encoded using the [[Codec]].
    */
  def add[A](name: String, value: A)(implicit codec: Codec[A]): Props

  /**
    * Returns a `Chain` of name-value pairs, where
    * the value has been encoded with a [[Codec]].
    *
    * If encoding of any value resulted in error,
    * instead returns the first such error.
    */
  def toChain: Either[AvroError, Chain[(String, Any)]]
}

final object Props {
  private[this] final class NonEmptyProps(
    props: NonEmptyChain[(String, Either[AvroError, Any])]
  ) extends Props {
    override final def add[A](name: String, value: A)(implicit codec: Codec[A]): Props =
      new NonEmptyProps(props.append(name -> Codec.encode(value)))

    override final def toChain: Either[AvroError, Chain[(String, Any)]] =
      props.toChain.traverse {
        case (name, value) =>
          value.tupleLeft(name)
      }

    override final def toString: String =
      toChain match {
        case Right(props) =>
          props.toList
            .map { case (name, value) => s"$name -> $value" }
            .mkString("Props(", ", ", ")")

        case Left(error) =>
          error.show
      }
  }

  private[this] final object EmptyProps extends Props {
    override final def add[A](name: String, value: A)(implicit codec: Codec[A]): Props =
      Props.one(name, value)

    override final val toChain: Either[AvroError, Chain[(String, Any)]] =
      Right(Chain.empty)

    override final def toString: String =
      "Props()"
  }

  /**
    * Returns a new [[Props]] instance including a
    * property with the specified name and value.
    *
    * The value is encoded using the [[Codec]].
    */
  final def one[A](name: String, value: A)(implicit codec: Codec[A]): Props =
    new NonEmptyProps(NonEmptyChain.one(name -> Codec.encode(value)))

  /**
    * The [[Props]] instance without any properties.
    */
  final val empty: Props =
    EmptyProps

  implicit final val propsShow: Show[Props] =
    Show.fromToString
}
