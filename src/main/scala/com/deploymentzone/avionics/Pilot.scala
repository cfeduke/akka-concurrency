package com.deploymentzone.avionics

import com.deploymentzone.avionics.Plane.Controls
import akka.actor.ActorRef

class Pilot(plane: ActorRef,
             autopilot: ActorRef,
             var controls: ActorRef,
             altimeter: ActorRef) extends PilotActor {
  override def associatePilotName = "com.deploymentzone.avionics.flightcrew.copilotName"

  def receiveControls : Receive = {
    case Controls(controlSurfaces) =>
      controls = controlSurfaces
  }

  override def receive = receiveControls orElse super.receive
}
