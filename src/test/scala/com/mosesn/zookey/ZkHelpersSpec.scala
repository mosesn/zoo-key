package com.mosesn.zookey

import org.scalatest.fixture.FunSpec
import org.scalatest.matchers.ShouldMatchers
import com.twitter.logging.Logger
import com.twitter.util.TimeConversions.intToTimeableNumber
import com.twitter.util.{Duration, Timer, Await, Future}
import com.twitter.finagle.util.DefaultTimer
import com.twitter.zk.ZkClient
import org.scalatest.mock.MockitoSugar
import java.nio.charset.Charset
import org.mockito.Mockito.verifyNoMoreInteractions
import org.apache.zookeeper.{CreateMode, ZooKeeper}
import org.apache.zookeeper.ZooDefs.Ids.OPEN_ACL_UNSAFE
import scala.collection.JavaConverters._
import Conversions._
import org.apache.curator.test.TestingServer

class ZkHelpersSpec extends FunSpec with ShouldMatchers with MockitoSugar {

  type FixtureParam = Console

  override def withFixture(test: OneArgTest) {
    val server = new TestingServer()
    val sessionDuration: Duration = 5.seconds
    implicit val timer: Timer = DefaultTimer.twitter
    val client = ZkClient(server.getConnectString, sessionDuration)(timer).withAcl(OPEN_ACL_UNSAFE.asScala).withMode(CreateMode.EPHEMERAL)
    val console = new Console(client)
    try {
      Logger.withLoggers(Nil) {
        test(console)
      }
    } finally {
      client.release()
      server.stop()
    }
  }


  describe("ZkHelpers") {
    it("should exist properly") { console =>
      val node = console.client("/bleh")
      Await.result(ZkHelpers.exists(node)) should be (None)
      Await.result(console.mkdir("/bleh"))
      Await.result(ZkHelpers.exists(node)).isDefined should be (true)
    }
  }
}
