package com.mosesn.zookey

import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers
import com.twitter.logging.Logger
import com.twitter.util.TimeConversions.intToTimeableNumber
import com.twitter.util.{Duration, Timer, Await}
import com.twitter.finagle.util.DefaultTimer
import com.twitter.zk.ZkClient
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito.verifyNoMoreInteractions

class ConsoleSpec extends FunSpec with ShouldMatchers with MockitoSugar {
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

    it("should echo nicely") {
      Logger.withLoggers(Nil) {
        val mockClient = mock[ZkClient]
        val console = new Console(mockClient)
        Await.result(console.echo("something")) should be ("something")
        verifyNoMoreInteractions(mockClient)
      }
    }
  }
}
