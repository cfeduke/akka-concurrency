package com.deploymentzone.avionics

import akka.actor.{ActorRef, Props, Actor, ActorLogging}
import com.deploymentzone.avionics.EventSource.RegisterListener

object Plane {
  case object GiveMeControl
  case class Controls(controls: ActorRef)
}

class Plane extends Actor with ActorLogging {
  import Altimeter._
  import Plane._

  val altimeter = context.actorOf(Props(Altimeter()), "Altimeter")
  val controls = context.actorOf(Props(new ControlSurfaces(altimeter)), "ControlSurfaces")

  override def preStart() {
    altimeter ! RegisterListener(self)
  }

  def receive = {
    case GiveMeControl =>
      log.info("Plane giving control")
      sender ! Controls(controls)
    case AltitudeUpdate(altitude) =>
      log.info(s"Altitude is now: $altitude")
  }
}
