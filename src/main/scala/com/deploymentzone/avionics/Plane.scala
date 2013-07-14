package com.deploymentzone.avionics

import akka.actor.{ActorRef, Props, Actor, ActorLogging}
import com.deploymentzone.avionics.EventSource.RegisterListener
import akka.util.Timeout
import akka.pattern.ask
import scala.concurrent.duration._
import com.deploymentzone.avionics.IsolatedLifeCycleSupervisor.WaitForStart
import scala.concurrent.Await

object Plane {
  case object GiveMeControl
  case class Controls(controls: ActorRef)
}

class Plane
  extends Actor
  with ActorLogging {
  this: PilotProvider
    with AltimeterProvider
    with LeadFlightAttendantProvider =>

  import Altimeter._
  import Plane._

  val configKeyPrefix = "com.deploymentzone.avionics.flightcrew"
  val cfg = context.system.settings.config
  val pilotName = cfg.getString(s"$configKeyPrefix.pilotName")
  val copilotName = cfg.getString(s"$configKeyPrefix.copilotName")
//  val autopilot = context.actorOf(Props[AutoPilot])
  val leadFlightAttendantName = cfg.getString(s"$configKeyPrefix.leadAttendantName")

  override def preStart() {
    startEquipment()
    startPeople()

    actorForControls("Altimeter") ! RegisterListener(self)
    List(pilotName, copilotName).map(actorForPilots) foreach { _ ! Pilots.ReadyToGo }
  }

  implicit val askTimeout = Timeout(1 second)
  def startEquipment() {
    val controls = context.actorOf(Props(new IsolatedResumeSupervisor() with OneForOneStrategyFactory {
      def childStarter() {
        val alt = context.actorOf(Props(newAltimeter), "Altimeter")
        // implicitly added to the context within this block
        context.actorOf(Props(new ControlSurfaces(alt)), "ControlSurfaces")
      }
    }), "ControlSurfaces")
    Await.result(controls ? WaitForStart, 1.second)
  }

  def startPeople() {
    val plane = self
    val controls = actorForControls("ControlSurfaces")
    val altimeter = actorForControls("Altimeter")

    val people = context.actorOf(Props(new IsolatedStopSupervisor()
      with OneForOneStrategyFactory {
      def childStarter() {
        context.actorOf(Props(newPilot(plane, null, controls, altimeter)), pilotName)
        context.actorOf(Props(newCopilot(plane, null, altimeter)), copilotName)
      }
    }), "Pilots")
    context.actorOf(Props(newLeadFlightAttendant), leadFlightAttendantName)
    Await.result(people ? WaitForStart, 1.second)
  }

  def actorForControls(name: String) = context.actorFor("Equipment/" + name)

  def actorForPilots(name : String) = context.actorFor("Pilots/" + name)

  def receive = {
    case GiveMeControl =>
      log.info("Plane giving control")
      sender ! Controls(actorForControls("ControlSurfaces"))
    case AltitudeUpdate(altitude) =>
      log.info(s"Altitude is now: $altitude")
  }
}
