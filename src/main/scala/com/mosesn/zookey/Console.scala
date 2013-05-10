package com.mosesn.zookey

import com.twitter.zk.ZkClient
import com.twitter.util.Future
import org.jboss.netty.buffer.ChannelBuffer

class Console(client: ZkClient) {
  def ls(path: String): Future[Seq[String]] =
    client(path).getChildren() map (_.children map (child => child.name))

  def mkdir(path: String): Future[Unit] = ???

  def tree(path: String): Future[String] = ???

  def cd(path: String): Future[Unit] = ???

  def pwd(path: String): Future[String] = ???

  def cat(path: String): Future[ChannelBuffer] = ???

  def touch(path: String): Future[Unit] = ???

  def echo(string: String): Future[String] = Future.value(string)
}
