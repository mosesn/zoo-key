package com.mosesn.zookey

import com.twitter.logging.Logger
import com.twitter.ostrich.admin.{AdminServiceFactory, ServiceTracker, RuntimeEnvironment}

object Driver {
  val log = Logger.get(getClass.getName)
  def main(args: Array[String]) {
    try {
      val env = new RuntimeEnvironment(this)
      startAdmin(env)
      startConsoleService()
    } catch {
      case e: Throwable => handleError(e)
    }
  }

  def startAdmin(env: RuntimeEnvironment) {
    log.warning("starting admin service")
    val admin = AdminServiceFactory(9900)(env)
    ServiceTracker.register(admin)
    log.warning("started admin service")
  }

  def startConsoleService() {
    log.warning("starting console")
    val console = new ConsoleService()
    ServiceTracker.register(console)
    log.warning("started console")
  }

  def handleError(e: Throwable) {
    log.error(e, "there was an exception starting up")
    log.warning("shutting down services")
    ServiceTracker.shutdown()
    log.warning("shut down services")
  }
}
