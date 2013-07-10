package com.deploymentzone.avionics

import akka.actor.Actor

trait PilotProvider {
  def newPilot: Actor = new Pilot
  def newCopilot: Actor = new Copilot
}
