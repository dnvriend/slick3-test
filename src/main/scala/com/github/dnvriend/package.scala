package com.github

import org.reactivestreams.Publisher
import rx.RxReactiveStreams

package object dnvriend {
  implicit class PublisherToRxObservable[T](publisher: Publisher[T]) {
    def toObservable= RxReactiveStreams.toObservable(publisher)
  }
}
