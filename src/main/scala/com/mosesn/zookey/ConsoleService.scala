package com.mosesn.zookey

import com.twitter.ostrich.admin.Service
import com.twitter.finagle.util.DefaultTimer
import com.twitter.util.TimeConversions.intToTimeableNumber
import com.twitter.zk.ZkClient
import com.twitter.util.Await
import com.twitter.logging.Logger
import org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE
import scala.collection.JavaConverters._

class ConsoleService extends Service {
  val log = Logger.get(getClass.getName)
  var client: Option[ZkClient] = None
  override def start() {
    log.warning("starting up zookeeper client")
    client = Some(ZkClient("localhost:2181", 5.seconds, 5.seconds)(DefaultTimer.twitter).withAcl(OPEN_ACL_UNSAFE.asScala))
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
