package com.mosesn.zookey

import com.twitter.zk.{ZkClient, ZNode}
import com.twitter.util.Future

class Console(val client: ZkClient) {
  var cwd = "/"

  private[this] def absolute(path: String): String =
    if (path.startsWith("/")) path else cwd ++ path

  def ls(path: String): Future[Seq[String]] =
    client(absolute(path)).getChildren() map (_.children map (child => child.name))

  def mkdir(path: String): Future[Unit] = client(absolute(path)).create().unit

  def rmdir(path: String): Future[Unit] = client(absolute(path)).delete(-1).unit

  def tree(path: String): Future[String] = tree(client(absolute(path)), 0, "")

  private[this] def tree(node: ZNode, depth: Int, prefix: String): Future[String] = node.getChildren() flatMap { children =>
    Future.collect(children.children map { child =>
      tree(child, depth + 1, " " * (4 * depth) + "|-- ")
    }) map { strings =>
      ((prefix + node.path) +: strings).mkString("\n")
    }
  }

  def cd(path: String): Future[Unit] = Future({
    cwd = absolute(path)
  })

  def pwd: Future[String] = Future(cwd)

  def cat(path: String): Future[Array[Byte]] = client(absolute(path)).getData() map (_.bytes)

  def touch(path: String): Future[Unit] = ZkHelpers.createIfNonexistent(client(absolute(path)))

  def echo(string: String): Future[String] = Future.value(string)
}
