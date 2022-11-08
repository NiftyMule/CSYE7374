package util

/**
 * Functional Programming methods.
 */
object FP {

    def optional[T](f: T => Boolean)(t: T): Option[T] = Some(t).filter(f)
}
