package com.deploymentzone.avionics

import akka.actor.{ActorRef, Actor}

trait PilotProvider {
  def newPilot(plane : ActorRef, autopilot : ActorRef, controls : ActorRef, altimeter : ActorRef): Actor =
    new Pilot(plane, autopilot, controls, altimeter)
  def newCopilot(plane : ActorRef, autopilot : ActorRef, altimeter : ActorRef): Actor =
    new Copilot(plane, autopilot, altimeter)
}
