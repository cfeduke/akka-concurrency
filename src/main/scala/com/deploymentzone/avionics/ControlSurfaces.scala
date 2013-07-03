package com.deploymentzone.avionics

import akka.actor.{ActorRef, Actor}

object ControlSurfaces {
  sealed abstract class StickCommand
  case class StickBack(amount: Float) extends StickCommand
  case class StickForward(amount: Float) extends StickCommand
}

class ControlSurfaces(altimeter: ActorRef) extends Actor {
  import ControlSurfaces._
  import Altimeter._

  def receive = {
    case StickBack(amount) =>
      altimeter ! RateChange(amount)
    case StickForward(amount) =>
      altimeter ! RateChange(-1 * amount)
  }
}
