package org.scalajs.actors
package webworkers

import scala.scalajs.js
import org.scalajs.spickling.PicklerRegistry

import sysmsg.SystemMessage

private[actors] class WorkerActorRef(
    system: WebWorkersActorSystem,
    val path: ActorPath) extends InternalActorRef {

  private[this] val pickledPath = PicklerRegistry.pickle(path)

  private[this] lazy val parent =
    if (path.isInstanceOf[RootActorPath]) Nobody
    else new WorkerActorRef(system, path.parent)

  def !(msg: Any)(implicit sender: ActorRef): Unit = {
    val messagePickle: js.Any = PicklerRegistry.pickle(msg)
    val senderID = sender match {
      case Actor.noSender => null
      case _ => system.globalizePath(sender.path)
    }
    val senderIDPickle: js.Any = PicklerRegistry.pickle(senderID)
    val data = js.Dynamic.literal(
        kind = "bang",
        sender = senderIDPickle,
        receiver = pickledPath,
        message = messagePickle)
    WebWorkerRouter.postMessageTo(path.address, data)
  }

  // InternalActorRef API

  // TODO
  def start(): Unit = ()
  def resume(causedByFailure: Throwable): Unit = ()
  def suspend(): Unit = ()
  def restart(cause: Throwable): Unit = ()
  def stop(): Unit = ()
  def sendSystemMessage(message: SystemMessage): Unit = ()

  def getParent: InternalActorRef = parent

  def getChild(name: Iterator[String]): InternalActorRef =
    Actor.noSender

  def isLocal: Boolean = true
}
