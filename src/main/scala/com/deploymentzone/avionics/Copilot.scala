package com.deploymentzone.avionics

import akka.actor.ActorRef

class Copilot(plane : ActorRef, autopilot : ActorRef, altimeter : ActorRef) extends PilotActor {
  override def associatePilotName = "com.deploymentzone.avionics.flightcrew.pilotName"
}
