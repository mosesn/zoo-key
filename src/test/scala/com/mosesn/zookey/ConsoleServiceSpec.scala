package com.mosesn.zookey

import org.scalatest.FunSpec
import com.twitter.logging.Logger
import org.scalatest.matchers.ShouldMatchers

class ConsoleServiceSpec extends FunSpec with ShouldMatchers {
  describe("ConsoleService") {
    it("should start and shutdown cleanly") {
      Logger.withLoggers(Nil) {
        val console = new ConsoleService()
        console.start()
        console.shutdown()
      }
    }

    it("should have a proper lifecycle") {
      Logger.withLoggers(Nil) {
        val console = new ConsoleService()
        console.client should be (None)
        console.start()
        console.client should not be (None)
        console.shutdown()
      }
    }
  }
}
