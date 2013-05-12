package com.mosesn.zookey

import org.scalatest.FunSpec
import com.twitter.logging.Logger
import com.twitter.ostrich.admin.ServiceTracker

class DriverSpec extends FunSpec {
  describe("Driver") {
    it("should start and stop properly") {
      Logger.withLoggers(Nil) {
        Driver.main(Array.empty)
        ServiceTracker.shutdown()
        assert(true)
      }
    }
  }
}
