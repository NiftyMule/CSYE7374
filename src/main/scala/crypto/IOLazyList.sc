import cats.effect.IO

val xs = 0 #:: LazyList.from(1)
val xis: LazyList[IO[Int]] = xs map (x => IO(x))

for (xi <- xis) yield for (x <- xi) yield x*2
//
//def gatherWhile[T](p: T=>Boolean)(tis: LazyList[IO[T]]): IO[LazyList[T]] =
//    {
//        def inner(ri: IO[LazyList[T]], w: LazyList[IO[T]]): IO[LazyList[T]] =
//            w match {
//                case LazyList() => ri
//                case hi #:: t =>
//                    val z: IO[(LazyList[T], LazyList[IO[T]])] = for (h <- hi; r <- ri) yield if (p(h)) (r :+ h, t) else (r, LazyList())
//
//            }
//    }
//    tis.foldLeft(LazyList.empty[T]){(r, ti) =>
//        for (t <- ti) yield if (p(t)) r #:: t else r
//    }
