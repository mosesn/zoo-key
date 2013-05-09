package com.mosesn.zookey

import com.twitter.ostrich.admin.Service
import com.twitter.finagle.util.DefaultTimer
import com.twitter.util.TimeConversions.intToTimeableNumber
import com.twitter.zk.ZkClient
import com.twitter.util.Await
import com.twitter.logging.Logger

class ConsoleService extends Service {
  val log = Logger.get(getClass.getName)
  var client: Option[ZkClient] = None
  override def start() {
    log.warning("starting up zookeeper client")
    client = Some(ZkClient("localhost:2181", 5.seconds, 5.seconds)(DefaultTimer.twitter))
    log.warning("started up zookeeper client")
  }

  override def shutdown() {
    client match {
      case Some(zkClient) => {
        log.warning("shutting down zookeeper client")
        Await.result(zkClient.release())
        log.warning("shut down zookeeper client")
      }
      case None => {
        log.warning("no client to shut down")
      }
    }
    for (underlying <- client) {
      underlying.release()
    }
  }
}
