package com.deploymentzone.avionics

import akka.actor.{Actor, ActorRef}

object Pilots {
  case object ReadyToGo
  case object RelinquishControl
}

class Pilot extends Actor {
  import Pilots._
  import Plane._

  var controls: ActorRef = context.system.deadLetters
  var copilot: ActorRef = context.system.deadLetters
  var autopilot: ActorRef = context.system.deadLetters
  var copilotName = context.system.settings.config.getString("com.deploymentzone.avionics.flightcrew.copilotname")

  def receive = {
    case ReadyToGo =>
      context.parent ! GiveMeControl
      copilot = context.actorFor("../" + copilotName)
      autopilot = context.actorFor("../Autopilot")
    case Controls(controlSurfaces) =>
      controls = controlSurfaces
  }
}
