package com.deploymentzone.avionics

import akka.actor.{ActorRef, Props, Actor, ActorLogging}
import com.deploymentzone.avionics.EventSource.RegisterListener

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
  val altimeter = context.actorOf(Props(newAltimeter), "Altimeter")
  val controls = context.actorOf(Props(new ControlSurfaces(altimeter)), "ControlSurfaces")
  val cfg = context.system.settings.config
  val pilot = context.actorOf(Props(newPilot), cfg.getString(s"$configKeyPrefix.pilotName"))
  val copilot = context.actorOf(Props(newCopilot), cfg.getString(s"$configKeyPrefix.copilotName"))
//  val autopilot = context.actorOf(Props[AutoPilot])
  val flightAttendant = context.actorOf(Props(LeadFlightAttendant()), cfg.getString(s"$configKeyPrefix.leadAttendantName"))

  override def preStart() {
    altimeter ! RegisterListener(self)
    List(pilot, copilot) foreach { _ ! Pilots.ReadyToGo }
  }

  def receive = {
    case GiveMeControl =>
      log.info("Plane giving control")
      sender ! Controls(controls)
    case AltitudeUpdate(altitude) =>
      log.info(s"Altitude is now: $altitude")
  }
}
