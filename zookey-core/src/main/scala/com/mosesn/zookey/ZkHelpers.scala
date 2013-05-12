package com.mosesn.zookey

import com.twitter.zk.ZNode
import com.twitter.util.Future
import org.apache.zookeeper.data.Stat

object ZkHelpers {
  def createOrElseUpdate(node: ZNode, buffer: Array[Byte], update: ZNode => Array[Byte] => Future[ZNode]): Future[ZNode] = {
    exists(node) flatMap {
      case None => node.create(data = buffer)
      case _ => update(node)(buffer)
    }
  }

  def exists(node: ZNode): Future[Option[Stat]] = node.exists() map { exist =>
    Some(exist.stat)
  } rescue {
    case e: Throwable =>
      Future.value(None)
  }

  def createIfNonexistent(node: ZNode): Future[Unit] = {
    exists(node) map {
      case None => node.create()
      case _ => ()
    }
  }
}
