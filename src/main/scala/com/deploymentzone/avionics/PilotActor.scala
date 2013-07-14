package com.deploymentzone.avionics

import akka.actor.{Actor, ActorRef}

object Pilots {
  case object ReadyToGo
  case object RelinquishControl
}

abstract class PilotActor extends Actor {
  import Pilots._
  import Plane._

  var associatePilot: ActorRef = context.system.deadLetters
  var autopilot: ActorRef = context.system.deadLetters
  def associatePilotName : String

  def receive = {
    case ReadyToGo =>
      context.parent ! GiveMeControl
      associatePilot = context.actorFor("../" + associatePilotName)
//      autopilot = context.actorFor("../Autopilot")

  }
}
