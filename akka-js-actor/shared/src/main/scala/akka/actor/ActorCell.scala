/**
 * Copyright (C) 2009-2014 Typesafe Inc. <http://www.typesafe.com>
 */

package akka.actor

import akka.actor.dungeon.ChildrenContainer
import akka.dispatch.Envelope
import akka.dispatch.sysmsg._
import akka.event.Logging.{ LogEvent, Debug, Error }
/**
 * @note IMPLEMENT IN SCALA.JS
 * No Java
 * import akka.japi.Procedure
 * import java.io.{ ObjectOutputStream, NotSerializableException }
 */
import scala.annotation.{ switch, tailrec }
import scala.collection.immutable
import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.Duration
/**
 * @note IMPLEMENT IN SCALA.JS
 * No threads in JS, so it's useless
 *
 * import scala.concurrent.forkjoin.ThreadLocalRandom
 */
import scala.util.control.NonFatal
import akka.dispatch.MessageDispatcher

/**
 * The actor context - the view of the actor cell from the actor.
 * Exposes contextual information for the actor and the current message.
 *
 * There are several possibilities for creating actors (see [[akka.actor.Props]]
 * for details on `props`):
 *
 * {{{
 * // Java or Scala
 * context.actorOf(props, "name")
 * context.actorOf(props)
 *
 * // Scala
 * context.actorOf(Props[MyActor])
 * context.actorOf(Props(classOf[MyActor], arg1, arg2), "name")
 *
 * // Java
 * getContext().actorOf(Props.create(MyActor.class));
 * getContext().actorOf(Props.create(MyActor.class, arg1, arg2), "name");
 * }}}
 *
 * Where no name is given explicitly, one will be automatically generated.
 */
trait ActorContext extends ActorRefFactory {

  def self: ActorRef

  /**
   * Retrieve the Props which were used to create this actor.
   */
  def props: Props

  /**
   * Gets the current receive timeout.
   * When specified, the receive method should be able to handle a [[akka.actor.ReceiveTimeout]] message.
   */
  def receiveTimeout: Duration

  /**
   * Defines the inactivity timeout after which the sending of a [[akka.actor.ReceiveTimeout]] message is triggered.
   * When specified, the receive function should be able to handle a [[akka.actor.ReceiveTimeout]] message.
   * 1 millisecond is the minimum supported timeout.
   *
   * Please note that the receive timeout might fire and enqueue the `ReceiveTimeout` message right after
   * another message was enqueued; hence it is '''not guaranteed''' that upon reception of the receive
   * timeout there must have been an idle period beforehand as configured via this method.
   *
   * Once set, the receive timeout stays in effect (i.e. continues firing repeatedly after inactivity
   * periods). Pass in `Duration.Undefined` to switch off this feature.
   */
  def setReceiveTimeout(timeout: Duration): Unit

  /**
   * Changes the Actor's behavior to become the new 'Receive' (PartialFunction[Any, Unit]) handler.
   * Replaces the current behavior on the top of the behavior stack.
   */
  def become(behavior: Actor.Receive): Unit = become(behavior, discardOld = true)

  /**
   * Changes the Actor's behavior to become the new 'Receive' (PartialFunction[Any, Unit]) handler.
   * This method acts upon the behavior stack as follows:
   *
   *  - if `discardOld = true` it will replace the top element (i.e. the current behavior)
   *  - if `discardOld = false` it will keep the current behavior and push the given one atop
   *
   * The default of replacing the current behavior on the stack has been chosen to avoid memory
   * leaks in case client code is written without consulting this documentation first (i.e.
   * always pushing new behaviors and never issuing an `unbecome()`)
   */
  def become(behavior: Actor.Receive, discardOld: Boolean): Unit

  /**
   * Reverts the Actor behavior to the previous one on the behavior stack.
   */
  def unbecome(): Unit

  /**
   * Returns the sender 'ActorRef' of the current message.
   */
  def sender(): ActorRef

  /**
   * Returns all supervised children; this method returns a view (i.e. a lazy
   * collection) onto the internal collection of children. Targeted lookups
   * should be using `child` instead for performance reasons:
   *
   * {{{
   * val badLookup = context.children find (_.path.name == "kid")
   * // should better be expressed as:
   * val goodLookup = context.child("kid")
   * }}}
   */
  def children: immutable.Iterable[ActorRef]

  /**
   * Get the child with the given name if it exists.
   */
  def child(name: String): Option[ActorRef]

  /**
   * Returns the dispatcher (MessageDispatcher) that is used for this Actor.
   * Importing this member will place an implicit ExecutionContext in scope.
   */
  /**
   * @note IMPLEMENT IN SCALA.JS
   *
   implicit def dispatcher: ExecutionContextExecutor
   */
  implicit def dispatcher: MessageDispatcher

  /**
   * The system that the actor belongs to.
   * Importing this member will place an implicit ActorSystem in scope.
   */
  implicit def system: ActorSystem

  /**
   * Returns the supervising parent ActorRef.
   */
  def parent: ActorRef

  /**
   * Registers this actor as a Monitor for the provided ActorRef.
   * This actor will receive a Terminated(subject) message when watched
   * actor is terminated.
   * @return the provided ActorRef
   */
  def watch(subject: ActorRef): ActorRef

  /**
   * Unregisters this actor as Monitor for the provided ActorRef.
   * @return the provided ActorRef
   */
  def unwatch(subject: ActorRef): ActorRef

  /**
   * ActorContexts shouldn't be Serializable
   */
  /**
   * @note IMPLEMENT IN SCALA.JS (how?)
   *
   *  final protected def writeObject(o: ObjectOutputStream): Unit =
   *    throw new NotSerializableException("ActorContext is not serializable!")
   */
}

/**
 * AbstractActorContext is the AbstractActor equivalent of ActorContext,
 * containing the Java API
 */
/**
 * @note IMPLEMENT IN SCALA.JS
 * We shouldn't really need this, it's just Java API
 *
 * trait AbstractActorContext extends ActorContext {
 *
 *  /**
 *   * Returns an unmodifiable Java Collection containing the linked actors,
 *   * please note that the backing map is thread-safe but not immutable
 *   */
 *  def getChildren(): java.lang.Iterable[ActorRef]
 *
 *  /**
 *   * Returns a reference to the named child or null if no child with
 *   * that name exists.
 *   */
 *  def getChild(name: String): ActorRef
 * }
 */

/**
 * UntypedActorContext is the UntypedActor equivalent of ActorContext,
 * containing the Java API
 */
/**
 * @note IMPLEMENT IN SCALA.JS
 * Same as above
 *
 * trait UntypedActorContext extends ActorContext {
 *
 *  /**
 *   * Returns an unmodifiable Java Collection containing the linked actors,
 *   * please note that the backing map is thread-safe but not immutable
 *   */
 *  def getChildren(): java.lang.Iterable[ActorRef]
 *
 *  /**
 *   * Returns a reference to the named child or null if no child with
 *   * that name exists.
 *   */
 *  def getChild(name: String): ActorRef
 *
 *  /**
 *   * Changes the Actor's behavior to become the new 'Procedure' handler.
 *   * Replaces the current behavior on the top of the behavior stack.
 *   */
 *  def become(behavior: Procedure[Any]): Unit
 *
 *  /**
 *   * Changes the Actor's behavior to become the new 'Procedure' handler.
 *   * This method acts upon the behavior stack as follows:
 *   *
 *   *  - if `discardOld = true` it will replace the top element (i.e. the current behavior)
 *   *  - if `discardOld = false` it will keep the current behavior and push the given one atop
 *   *
 *   * The default of replacing the current behavior on the stack has been chosen to avoid memory
 *   * leaks in case client code is written without consulting this documentation first (i.e.
 *   * always pushing new behaviors and never issuing an `unbecome()`)
 *   */
 *  def become(behavior: Procedure[Any], discardOld: Boolean): Unit
 * }
 */


/**
 * INTERNAL API
 */
private[akka] trait Cell {
  /**
   * The “self” reference which this Cell is attached to.
   */
  def self: ActorRef
  /**
   * The system within which this Cell lives.
   */
  def system: ActorSystem
  /**
   * The system internals where this Cell lives.
   */
  def systemImpl: ActorSystemImpl
  /**
   * Start the cell: enqueued message must not be processed before this has
   * been called. The usual action is to attach the mailbox to a dispatcher.
   */
  def start(): this.type
  /**
   * Recursively suspend this actor and all its children. Is only allowed to throw Fatal Throwables.
   */
  def suspend(): Unit
  /**
   * Recursively resume this actor and all its children. Is only allowed to throw Fatal Throwables.
   */
  def resume(causedByFailure: Throwable): Unit
  /**
   * Restart this actor (will recursively restart or stop all children). Is only allowed to throw Fatal Throwables.
   */
  def restart(cause: Throwable): Unit
  /**
   * Recursively terminate this actor and all its children. Is only allowed to throw Fatal Throwables.
   */
  def stop(): Unit
  /**
   * Returns “true” if the actor is locally known to be terminated, “false” if
   * alive or uncertain.
   */
  def isTerminated: Boolean
  /**
   * The supervisor of this actor.
   */
  def parent: InternalActorRef
  /**
   * All children of this actor, including only reserved-names.
   */
  def childrenRefs: ChildrenContainer
  /**
   * Get the stats for the named child, if that exists.
   */
  def getChildByName(name: String): Option[ChildStats]

  /**
   * Method for looking up a single child beneath this actor.
   * It is racy if called from the outside.
   */
  def getSingleChild(name: String): InternalActorRef

  /**
   * Enqueue a message to be sent to the actor; may or may not actually
   * schedule the actor to run, depending on which type of cell it is.
   * Is only allowed to throw Fatal Throwables.
   */
  def sendMessage(msg: Envelope): Unit

  /**
   * Enqueue a message to be sent to the actor; may or may not actually
   * schedule the actor to run, depending on which type of cell it is.
   * Is only allowed to throw Fatal Throwables.
   */
  final def sendMessage(message: Any, sender: ActorRef): Unit = 
    sendMessage(Envelope(message, sender, system))

  /**
   * Enqueue a message to be sent to the actor; may or may not actually
   * schedule the actor to run, depending on which type of cell it is.
   * Is only allowed to throw Fatal Throwables.
   */
  def sendSystemMessage(msg: SystemMessage): Unit
  /**
   * Returns true if the actor is local, i.e. if it is actually scheduled
   * on a Thread in the current JVM when run.
   */
  def isLocal: Boolean
  /**
   * If the actor isLocal, returns whether "user messages" are currently queued,
   * “false” otherwise.
   */
  def hasMessages: Boolean
  /**
   * If the actor isLocal, returns the number of "user messages" currently queued,
   * which may be a costly operation, 0 otherwise.
   */
  def numberOfMessages: Int
  /**
   * The props for this actor cell.
   */
  def props: Props
}

/**
 * Everything in here is completely Akka PRIVATE. You will not find any
 * supported APIs in this place. This is not the API you were looking
 * for! (waves hand)
 */
private[akka] object ActorCell {
 /**
  * @note IMPLEMENT IN SCALA.JS
  * JS is single threaded, no ThreadLocal
  *
  * val contextStack = new ThreadLocal[List[ActorContext]] {
  *   override def initialValue: List[ActorContext] = Nil
  * }
  */
  var contextStack: List[ActorContext] = Nil

  final val emptyCancellable: Cancellable = new Cancellable {
    def isCancelled: Boolean = false
    def cancel(): Boolean = false
  }

  final val emptyBehaviorStack: List[Actor.Receive] = Nil

  final val emptyActorRefSet: Set[ActorRef] = immutable.HashSet.empty

  final val terminatedProps: Props = Props((throw new IllegalActorStateException("This Actor has been terminated")): Actor)

  final val undefinedUid = 0

  @tailrec final def newUid(): Int = {
    // Note that this uid is also used as hashCode in ActorRef, so be careful
    // to not break hashing if you change the way uid is generated
   /**
    * @note IMPLEMENT IN SCALA.JS
    * Again, no need for ThreadLocal
    *
    * val uid = ThreadLocalRandom.current.nextInt()
    */
    val uid = scala.util.Random.nextInt()
    if (uid == undefinedUid) newUid
    else uid
  }

  final def splitNameAndUid(name: String): (String, Int) = {
    val i = name.indexOf('#')
    if (i < 0) (name, undefinedUid)
    else (name.substring(0, i), Integer.valueOf(name.substring(i + 1)))
  }

  final val DefaultState = 0
  final val SuspendedState = 1
  final val SuspendedWaitForChildrenState = 2
}

//ACTORCELL IS 64bytes and should stay that way unless very good reason not to (machine sympathy, cache line fit)
//vars don't need volatile since it's protected with the mailbox status
//Make sure that they are not read/written outside of a message processing (systemInvoke/invoke)
/**
 * Everything in here is completely Akka PRIVATE. You will not find any
 * supported APIs in this place. This is not the API you were looking
 * for! (waves hand)
 */
private[akka] class ActorCell(
                               val system: ActorSystemImpl,
                               val self: InternalActorRef,
                               final val props: Props, // Must be final so that it can be properly cleared in clearActorCellFields
                               val dispatcher: MessageDispatcher,
                               val parent: InternalActorRef)
  /**
   * @note IMPLEMENT IN SCALA.JS
   * We don't need Java API so we're just extending vanilla ActorContext
   *
   * extends UntypedActorContext with AbstractActorContext with Cell
   */
  extends ActorContext with Cell
  with dungeon.ReceiveTimeout
  with dungeon.Children
  with dungeon.Dispatch
  with dungeon.DeathWatch
  with dungeon.FaultHandling {

  import ActorCell._

  final def isLocal = true

  final def systemImpl = system
  protected final def guardian = self
  protected final def lookupRoot = self
  final def provider = system.provider

  protected def uid: Int = self.path.uid
  private[this] var _actor: Actor = _
  def actor: Actor = _actor
  protected def actor_=(a: Actor): Unit = _actor = a
  var currentMessage: Envelope = _
  private var behaviorStack: List[Actor.Receive] = emptyBehaviorStack
  /**
   * @note IMPLEMENT IN SCALA.JS
   *
   private[this] var sysmsgStash: LatestFirstSystemMessageList = SystemMessageList.LNil
   */

  private[this] var sysmsgStashLatestFirst: List[SystemMessage] = Nil


  protected def stash(msg: SystemMessage): Unit = {
    /**
     * @note IMPLEMENT IN SCALA.JS
     *
      assert(msg.unlinked)
      sysmsgStash ::= msg
     */
    sysmsgStashLatestFirst ::= msg
  }

  private def unstashAllLatestFirst(): List[SystemMessage] = {
    /**
     * @note IMPLEMENT IN SCALA.JS
     *
     *   private def unstashAll(): LatestFirstSystemMessageList = {
             val unstashed = sysmsgStash
     sysmsgStash = SystemMessageList.LNil
     unstashed
     */
    val unstashed = sysmsgStashLatestFirst
    sysmsgStashLatestFirst = Nil
    unstashed
  }

  /*
   * MESSAGE PROCESSING
   */
  //Memory consistency is handled by the Mailbox (reading mailbox status then processing messages, then writing mailbox status
  final def systemInvoke(message: SystemMessage): Unit = {
    /*
     * When recreate/suspend/resume are received while restarting (i.e. between
     * preRestart and postRestart, waiting for children to terminate), these
     * must not be executed immediately, but instead queued and released after
     * finishRecreate returns. This can only ever be triggered by
     * ChildTerminated, and ChildTerminated is not one of the queued message
     * types (hence the overwrite further down). Mailbox sets message.next=null
     * before systemInvoke, so this will only be non-null during such a replay.
     */

    def calculateState: Int =
      if (waitingForChildrenOrNull ne null) SuspendedWaitForChildrenState
      else if (mailbox.isSuspended) SuspendedState
      else DefaultState

    /**
     * @note IMPLEMENT IN SCALA.JS
     *
    @tailrec def sendAllToDeadLetters(messages: EarliestFirstSystemMessageList): Unit =
     */
    @tailrec def sendAllToDeadLetters(messages: List[SystemMessage]): Unit =
      if (messages.nonEmpty) {
        val tail = messages.tail
        val msg = messages.head
        msg.unlink()
        provider.deadLetters ! msg
        sendAllToDeadLetters(tail)
      }

    def shouldStash(m: SystemMessage, state: Int): Boolean =
      (state: @switch) match {
        case DefaultState                  ⇒ false
        case SuspendedState                ⇒ m.isInstanceOf[StashWhenFailed]
        case SuspendedWaitForChildrenState ⇒ m.isInstanceOf[StashWhenWaitingForChildren]
      }

    @tailrec
/**
 * @note IMPLEMENT IN SCALA.JS
 *
     def invokeAll(messages: EarliestFirstSystemMessageList, currentState: Int): Unit = {
 */
    def invokeAll(messages: List[SystemMessage], currentState: Int): Unit = {
      val rest = messages.tail
      val message = messages.head
      message.unlink()
      try {
        message match {
          case message: SystemMessage if shouldStash(message, currentState) ⇒ stash(message)
          case f: Failed ⇒ handleFailure(f)
          case DeathWatchNotification(a, ec, at) ⇒ watchedActorTerminated(a, ec, at)
          case Create(failure) ⇒ create(failure)
          case Watch(watchee, watcher) ⇒ addWatcher(watchee, watcher)
          case Unwatch(watchee, watcher) ⇒ remWatcher(watchee, watcher)
          case Recreate(cause) ⇒ faultRecreate(cause)
          case Suspend() ⇒ faultSuspend()
          case Resume(inRespToFailure) ⇒ faultResume(inRespToFailure)
          case Terminate() ⇒ terminate()
          case Supervise(child, async) ⇒ supervise(child, async)
          case NoMessage ⇒ // only here to suppress warning
        }
      } catch handleNonFatalOrInterruptedException { e ⇒
        handleInvokeFailure(Nil, e)
      }
      val newState = calculateState
      // As each state accepts a strict subset of another state, it is enough to unstash if we "walk up" the state
      // chain
      /**
       * @note IMPLEMENT IN SCALA.JS
       *
         val todo = if (newState < currentState) unstashAll() reverse_::: rest else rest
       */
      val todo =
        if (newState < currentState) { unstashAllLatestFirst() reverse_::: rest }
        else rest

      if (isTerminated) sendAllToDeadLetters(todo)
      else if (todo.nonEmpty) invokeAll(todo, newState)
    }

/**
 * @note IMPLEMENT IN SCALA.JS
 *
     invokeAll(new EarliestFirstSystemMessageList(message), calculateState)
 */
    invokeAll(List(message), calculateState)
  }

  //Memory consistency is handled by the Mailbox (reading mailbox status then processing messages, then writing mailbox status
  final def invoke(messageHandle: Envelope): Unit = try {
    currentMessage = messageHandle
    cancelReceiveTimeout() // FIXME: leave this here???
    messageHandle.message match {
      case msg: AutoReceivedMessage ⇒ autoReceiveMessage(messageHandle)
      case msg                      ⇒ receiveMessage(msg)
    }
    currentMessage = null // reset current message after successful invocation
  } catch handleNonFatalOrInterruptedException { e ⇒
    handleInvokeFailure(Nil, e)
  } finally {
    checkReceiveTimeout // Reschedule receive timeout
  }

  def autoReceiveMessage(msg: Envelope): Unit = {
    if (system.settings.DebugAutoReceive)
      publish(Debug(self.path.toString, clazz(actor), "received AutoReceiveMessage " + msg))

    msg.message match {
      case t: Terminated              ⇒ receivedTerminated(t)
      case AddressTerminated(address) ⇒ addressTerminated(address)
      case Kill                       ⇒ throw new ActorKilledException("Kill")
      case PoisonPill                 ⇒ self.stop()
      case sel: ActorSelectionMessage ⇒ receiveSelection(sel)
      case Identify(messageId)        ⇒ sender() ! ActorIdentity(messageId, Some(self))
    }
  }

  private def receiveSelection(sel: ActorSelectionMessage): Unit =
    if (sel.elements.isEmpty)
      invoke(Envelope(sel.msg, sender(), system))
    else
      ActorSelection.deliverSelection(self, sender(), sel)

  final def receiveMessage(msg: Any): Unit = actor.aroundReceive(behaviorStack.head, msg)

  /*
   * ACTOR CONTEXT IMPLEMENTATION
   */

  final def sender(): ActorRef = currentMessage match {
    case null                      ⇒ system.deadLetters
    case msg if msg.sender ne null ⇒ msg.sender
    case _                         ⇒ system.deadLetters
  }

  def become(behavior: Actor.Receive, discardOld: Boolean = true): Unit =
    behaviorStack = behavior :: (if (discardOld && behaviorStack.nonEmpty) behaviorStack.tail else behaviorStack)

 /**
  * @note IMPLEMENT IN SCALA.JS
  * Java API // XXX: CHECK
  *
  * def become(behavior: Procedure[Any]): Unit = become(behavior, discardOld = true)
  *
  * def become(behavior: Procedure[Any], discardOld: Boolean): Unit =
  *   become({ case msg ⇒ behavior.apply(msg) }: Actor.Receive, discardOld)
  */

  def unbecome(): Unit = {
    val original = behaviorStack
    behaviorStack =
      if (original.isEmpty || original.tail.isEmpty) actor.receive :: emptyBehaviorStack
      else original.tail
  }

  /*
   * ACTOR INSTANCE HANDLING
   */

  //This method is in charge of setting up the contextStack and create a new instance of the Actor
  protected def newActor(): Actor = {
   /**
    * @note IMPLEMENT IN SCALA.JS
    * We're not using ThreadLocal anymore
    *
    contextStack.set(this :: contextStack.get)
    */
    contextStack = this :: contextStack
    try {
      behaviorStack = emptyBehaviorStack
      val instance = props.newActor()

      if (instance eq null)
        throw ActorInitializationException(self, "Actor instance passed to actorOf can't be 'null'")

      // If no becomes were issued, the actors behavior is its receive method
      behaviorStack = if (behaviorStack.isEmpty) instance.receive :: behaviorStack else behaviorStack

      instance
    } finally {
     /**
      * @note IMPLEMENT IN SCALA.JS
      * Again, contextStack is just a list
      *
      * val stackAfter = contextStack.get
      * if (stackAfter.nonEmpty)
      *   contextStack.set(if (stackAfter.head eq null) stackAfter.tail.tail else stackAfter.tail) // pop null marker plus our context
      */
      val stackAfter = contextStack
      if(stackAfter.nonEmpty)
        contextStack = if (stackAfter.head eq null) stackAfter.tail.tail else stackAfter.tail
    }
  }

  protected def create(failure: Option[ActorInitializationException]): Unit = {
    def clearOutActorIfNonNull(): Unit = {
      if (actor != null) {
        clearActorFields(actor)
        actor = null // ensure that we know that we failed during creation
      }
    }

    failure foreach { throw _ }

    try {
      val created = newActor()
      actor = created
      created.aroundPreStart()
      checkReceiveTimeout
      if (system.settings.DebugLifecycle) publish(Debug(self.path.toString, clazz(created), "started (" + created + ")"))
    } catch {
      case e: InterruptedException ⇒
        clearOutActorIfNonNull()
      /**
       * @note IMPLEMENT IN SCALA.JS
       *
               Thread.currentThread().interrupt()
         throw ActorInitializationException(self, "interruption during creation", e)
       */
      case NonFatal(e) ⇒
        clearOutActorIfNonNull()
        e match {
          case i: InstantiationException ⇒ throw ActorInitializationException(self,
            """exception during creation, this problem is likely to occur because the class of the Actor you tried to create is either,
               a non-static inner class (in which case make it a static inner class or use Props(new ...) or Props( new UntypedActorFactory ... )
               or is missing an appropriate, reachable no-args constructor.
            """, i.getCause)
          case x ⇒ throw ActorInitializationException(self, "exception during creation", x)
        }
    }
  }

  private def supervise(child: ActorRef, async: Boolean): Unit =
    if (!isTerminating) {
      // Supervise is the first thing we get from a new child, so store away the UID for later use in handleFailure()
      initChild(child) match {
        case Some(crs) ⇒
          handleSupervise(child, async)
          if (system.settings.DebugLifecycle) publish(Debug(self.path.toString, clazz(actor), "now supervising " + child))
        case None ⇒ publish(Error(self.path.toString, clazz(actor), "received Supervise from unregistered child " + child + ", this will not end well"))
      }
    }

  // future extension point
  protected def handleSupervise(child: ActorRef, async: Boolean): Unit = child match {
/**
 * @note IMPLEMENT IN SCALA.JS
 *
     case r: RepointableActorRef if async ⇒ r.point()
 */
    case _                               ⇒
  }

/**
 * @note IMPLEMENT IN SCALA.JS
 *
   @tailrec private final def lookupAndSetField(clazz: Class[_], instance: AnyRef, name: String, value: Any): Boolean = {
     @tailrec def clearFirst(fields: Array[java.lang.reflect.Field], idx: Int): Boolean =
       if (idx < fields.length) {
         val field = fields(idx)
         if (field.getName == name) {
           field.setAccessible(true)
           field.set(instance, value)
           true
         } else clearFirst(fields, idx + 1)
       } else false

     clearFirst(clazz.getDeclaredFields, 0) || {
       clazz.getSuperclass match {
         case null ⇒ false // clazz == classOf[AnyRef]
         case sc   ⇒ lookupAndSetField(sc, instance, name, value)
       }
     }
   }
 */

  final protected def clearActorCellFields(cell: ActorCell): Unit = {
    cell.unstashAllLatestFirst()
/**
 * @note IMPLEMENT IN SCALA.JS
 *
     if (!lookupAndSetField(classOf[ActorCell], cell, "props", ActorCell.terminatedProps))
       throw new IllegalArgumentException("ActorCell has no props field")
 */
  }

  final protected def clearActorFields(actorInstance: Actor): Unit = {
    setActorFields(actorInstance, context = null, self = system.deadLetters)
    currentMessage = null
    behaviorStack = emptyBehaviorStack
  }

  final protected def setActorFields(actorInstance: Actor, context: ActorContext, self: ActorRef): Unit = {
    //actorInstance.setActorFields(context = context, self = self)
    
   /** XXX: FIX ME
    *  The issue is the following:
    *  `context` and `self` need to be `val`s inside Actor, otherwise you cannot `import` them.
    *  Being `val`s they cannot be overwritten, so Akka/JVM uses `java.lang.reflect.Field` which
    *  is not available in JS environments (of course).
    *  What I'm doing at the moment is mimicking the behaviour, by recursively following up the 
    *  function chain of `context` and `self` (starting from the property getter) to find out
    *  the *hidden* name, which is then overwritten. This results in the correct result being 
    *  returned when accessing the properties, but relies heavily on undefined behaviour subject 
    *  to changes, so we should really find a more reliable solution. @sjrd mentioned a scalac
    *  plugin or an IR manipulator as possible alternatives.
    */
    
    import scala.scalajs.js
    
    def getProto(a: Any) = js.Object.getPrototypeOf(a.asInstanceOf[js.Object])
    def getGetter(a: js.Object, s: String) = {
      @tailrec
      def getG(p: js.Object): (js.Object, String) = { 
        val desc = js.Object.getOwnPropertyDescriptor(p, s)
        if(desc.toString() != "undefined")
          (p, desc.get.toString())
        else getG(getProto(p))
      }
      
      getG(getProto(a))
    }
  
    def setField(instance: AnyRef, f: String, v: Any) = {
      try {
        val (proto, str) = getGetter(instance.asInstanceOf[js.Object], f)
        val getter = str.split("this\\.")(1).split("\\(")(0)
        val otherstring = proto.asInstanceOf[js.Dictionary[_]](getter).toString()
        val fin = otherstring.split("this\\.")(1).split("}")(0).trim()

        instance.asInstanceOf[js.Dictionary[js.Any]](fin) = v.asInstanceOf[js.Any]
      } catch { case e: Throwable => e.printStackTrace(); throw e }
    }
    if(actorInstance ne null) {
      setField(actorInstance, "context", context)
      setField(actorInstance, "self", self)
      assert(actorInstance.context eq context)
      assert(actorInstance.self eq self)
    /**
     * @note IMPLEMENT IN SCALA.JS
     *
           if (!lookupAndSetField(actorInstance.getClass, actorInstance, "context", context)
             || !lookupAndSetField(actorInstance.getClass, actorInstance, "self", self))
             throw new IllegalActorStateException(actorInstance.getClass + " is not an Actor since it have not mixed in the 'Actor' trait")
     */
    }
  }

  // logging is not the main purpose, and if it fails there’s nothing we can do
  protected final def publish(e: LogEvent): Unit = try system.eventStream.publish(e) catch { case NonFatal(_) ⇒ }

  protected final def clazz(o: AnyRef): Class[_] = if (o eq null) this.getClass else o.getClass
}

