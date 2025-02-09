---
id: overview
title: Overview
---

Vulcan provides Avro schemas, encoders, and decoders between Scala types and types used by the official Apache Avro library. The aims are reduced boilerplate code and improved type safety, compared to when directly using the Apache Avro library. In particular, the following features are supported.

- Schemas, encoders, and decoders for many standard library types.

- Ability to easily create schemas, encoders, and decoders for custom types.

- Derivation of schemas, encoders, and decoders for `case class`es and `sealed trait`s.

Documentation is kept up-to-date with new releases, currently documenting v@LATEST_VERSION@ on Scala @DOCS_SCALA_MINOR_VERSION@.

## Getting Started

To get started with [sbt](https://scala-sbt.org), simply add the following line to your `build.sbt` file.

```scala
libraryDependencies += "@ORGANIZATION@" %% "@CORE_MODULE_NAME@" % "@LATEST_VERSION@"
```

Published for Scala @SCALA_PUBLISH_VERSIONS@. For changes, refer to the [release notes](https://github.com/ovotech/vulcan/releases).

For Scala 2.12, enable partial unification by adding the following line to `build.sbt`.

```scala
scalacOptions += "-Ypartial-unification"
```

### Modules

Following are additional provided modules.

#### Enumeratum

For [enumeratum](https://github.com/lloydmeta/enumeratum) support, add the following line to your `build.sbt` file.

```scala
libraryDependencies += "@ORGANIZATION@" %% "@ENUMERATUM_MODULE_NAME@" % "@LATEST_VERSION@"
```

For information on how to use the module, refer to the [documentation](modules.md#enumeratum).

#### Generic

For generic derivation support, add the following line to your `build.sbt` file.

```scala
libraryDependencies += "@ORGANIZATION@" %% "@GENERIC_MODULE_NAME@" % "@LATEST_VERSION@"
```

For information on how to use the module, refer to the [documentation](modules.md#generic).

#### Refined

For [refined](https://github.com/fthomas/refined) support, add the following line to your `build.sbt` file.

```scala
libraryDependencies += "@ORGANIZATION@" %% "@REFINED_MODULE_NAME@" % "@LATEST_VERSION@"
```

For information on how to use the module, refer to the [documentation](modules.md#refined).

#### External Modules

Following is an incomplete list of third-party integrations.

- [fs2-kafka-vulcan](https://ovotech.github.io/fs2-kafka)

### Compatibility

Backwards binary-compatibility for the library is guaranteed between patch versions.<br>
For example, `@LATEST_MINOR_VERSION@.x` is backwards binary-compatible with `@LATEST_MINOR_VERSION@.y` for any `x > y`.

Please note binary-compatibility is not guaranteed between milestone releases.

## Dependencies

Refer to the table below for dependencies and version support across modules.

| Module                     | Dependencies                                                                                                                                    | Scala                                   |
| -------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------- |
| `@CORE_MODULE_NAME@`       | [Apache Avro @AVRO_VERSION@](https://github.com/apache/avro), [Cats @CATS_VERSION@](https://github.com/typelevel/cats)                          | Scala @CORE_CROSS_SCALA_VERSIONS@       |
| `@ENUMERATUM_MODULE_NAME@` | [Enumeratum @ENUMERATUM_VERSION@](https://github.com/lloydmeta/enumeratum)                                                                      | Scala @ENUMERATUM_CROSS_SCALA_VERSIONS@ |
| `@GENERIC_MODULE_NAME@`    | [Magnolia @MAGNOLIA_VERSION@](https://github.com/propensive/magnolia), [Shapeless @SHAPELESS_VERSION@](https://github.com/milessabin/shapeless) | Scala @GENERIC_CROSS_SCALA_VERSIONS@    |
| `@REFINED_MODULE_NAME@`    | [Refined @REFINED_VERSION@](https://github.com/fthomas/refined)                                                                                 | Scala @REFINED_CROSS_SCALA_VERSIONS@    |

## Inspiration

Library is heavily inspired by ideas from [avro4s](https://github.com/sksamuel/avro4s).

## License

Licensed under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0.html). Refer to the [license file](https://github.com/ovotech/vulcan/blob/master/license.txt).
