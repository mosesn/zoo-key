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

class ConsoleSpec extends FunSpec with ShouldMatchers with MockitoSugar {

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
      server.close()
    }
  }


  describe("Console") {
    it("should mkdir and then ls with an absolute path") { console =>
      Await.result(console.mkdir("/bleh"))
      Await.result(console.ls("/bleh")) should be (Seq.empty)
    }

    it("should mkdir and then ls with a relative path") { console =>
      Await.result(console.mkdir("bleh"))
      Await.result(console.ls("bleh")) should be (Seq.empty)
    }

    it("should cd with an absolute path") { console =>
      Await.result(console.mkdir("/foo"))
      Await.result(console.cd("/foo"))
      Await.result(console.pwd) should be ("/foo")
      Await.result(console.ls("")) should be (Seq.empty)
    }

    it("should cd with a relative path") { console =>
      Await.result(console.mkdir("/foo"))
      Await.result(console.cd("foo"))
      Await.result(console.pwd) should be ("/foo")
      Await.result(console.ls("")) should be (Seq.empty)
    }

    it("should start from /") { console =>
      Await.result(console.pwd) should be ("/")
    }

    it("should echo nicely") { console =>
      Await.result(console.echo("something")) should be ("something")
    }

    it("should write and read data from a node") { console =>
      implicit val tmp = console
      val charset = Charset.forName("UTF-8")
      Await.result(console.echo("something") > "/bleh")
      new String(Await.result(console.cat("/bleh")), charset) should be ("something")
    }

    it("should write and read data from an existing node") { console =>
      implicit val tmp = console
      val charset = Charset.forName("UTF-8")
      Await.result(console.mkdir("something"))
      Await.result(console.echo("something") > "/bleh")
      new String(Await.result(console.cat("/bleh")), charset) should be ("something")
    }

    it("should append data to an empty node") { console =>
      implicit val tmp = console
      val charset = Charset.forName("UTF-8")
      Await.result(console.echo("something") >> "/bleh")
      new String(Await.result(console.cat("/bleh")), charset) should be ("something")
    }

    it("should append data to an existing node") { console =>
      implicit val tmp = console
      val charset = Charset.forName("UTF-8")
      Await.result(console.echo("something") > "/bleh")
      Await.result(console.echo(" new") >> "/bleh")
      new String(Await.result(console.cat("/bleh")), charset) should be ("something new")
    }

    it("should tree correctly") { console =>
      val tmp = new Console(console.client.withMode(CreateMode.PERSISTENT))
      Await.result(tmp.mkdir("/foo"))
      Await.result(tmp.mkdir("/foo/bar"))
      Await.result(tmp.mkdir("/foo/bar/baz"))
      Await.result(tmp.mkdir("/foo/bar/qux"))
      Await.result(tmp.mkdir("/foo/quux"))
      val tree = Await.result(tmp.tree("/"))
      Await.result(tmp.rmdir("/foo/bar/baz"))
      Await.result(tmp.rmdir("/foo/bar/qux"))
      Await.result(tmp.rmdir("/foo/bar"))
      Await.result(tmp.rmdir("/foo/quux"))
      Await.result(tmp.rmdir("/foo"))
      tree should startWith ("/\n|-- ")
    }
  }
}
