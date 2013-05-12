package com.mosesn.zookey

import com.twitter.util.Future

object Conversions {
  implicit def transformToSendable(fString: Future[String]): Sendable = new Sendable(fString)
}
