package com.mosesn.zookey

import com.twitter.util.Future
import java.nio.charset.Charset

class Sendable(fString: Future[String]) {
  def >[A](path: String)(implicit console: Console): Future[Unit] = fString flatMap { string =>
    ZkHelpers.createOrElseUpdate(console.client(path), string.getBytes(Charset.forName("UTF-8")), { node =>
      { bytes =>
        node.setData(bytes, -1)
      }
    }).unit
  }

  def >>[A](path: String)(implicit console: Console): Future[Unit] = fString flatMap { string =>
    ZkHelpers.createOrElseUpdate(console.client(path), string.getBytes(Charset.forName("UTF-8")), { node =>
      { bytes =>
        node.getData() flatMap { data =>
          node.setData(data.bytes ++ bytes, -1)
        }
      }
    }).unit
  }
}
