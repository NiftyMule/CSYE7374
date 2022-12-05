import cats.effect.IO
import cats.effect.unsafe.implicits.global
import fs2.{Pure, Stream}

val fLessThan10: Int => Boolean = _ < 10
val lessThan10_2: IO[Int] => IO[Boolean] = _ map fLessThan10

def dropWhileEval[A](sa: Stream[IO, IO[A]])(p: IO[A] => IO[Boolean]): Stream[IO, IO[A]] =
    sa.evalMap(a => p(a).map(b => (b, a))).dropWhile(_._1).map(_._2)

val zz: Stream[IO, IO[Int]] = Stream.iterate[IO, Int](1)(_ + 1).map(x => IO(x * 2))
val yy: Stream[IO, IO[Int]] = dropWhileEval[Int](zz)(lessThan10_2).take(1)
val result: IO[List[Int]] = yy.evalMap(identity).compile.toList
result.unsafeRunSync()

