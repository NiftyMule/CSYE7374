package util

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import fs2.{Pure, Stream}


object StreamUtils {

    implicit class IODebugOps[A](ai: IO[A]) {
        def debug: IO[A] = ai.map {
            a =>
                System.err.println(s"debug: $a")
                a
        }
    }

    implicit class StreamDebugOps[A](as: Stream[IO, A]) {
        def debugStream: Stream[IO, A] = {
            as.take(10).evalMap(a => IO.println(s"debugStream: $a")).compile.drain.unsafeRunSync()
            as
        }
    }

    def lift[A, B](f: A => B): IO[A] => IO[B] = _ map f

    def dropWhileEval[A](sa: Stream[IO, IO[A]])(p: IO[A] => IO[Boolean]): Stream[IO, IO[A]] =
        sa.evalMap(a => p(a).map(b => (b, a))).dropWhile(_._1).map(_._2)

}