package com.mosesn.zookey

import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers
import com.twitter.logging.Logger
import com.twitter.util.TimeConversions.intToTimeableNumber
import com.twitter.util.{Duration, Timer, Await}
import com.twitter.finagle.util.DefaultTimer
import com.twitter.zk.ZkClient

class ConsoleSpec extends FunSpec with ShouldMatchers {
  val connectorString: String = "localhost:2181"
  val sessionDuration: Duration = 5.seconds
  implicit val timer: Timer = DefaultTimer.twitter

  describe("Console") {
    it("should ls the client") {
      Logger.withLoggers(Nil) {
        val client = ZkClient(connectorString, sessionDuration)(timer)
        val console = new Console(client)
        Await.result(console.ls("/ok")) should be (Seq.empty)
        client.release()
      }
    }
  }
}
