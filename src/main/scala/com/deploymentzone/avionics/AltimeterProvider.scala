package com.deploymentzone.avionics

import akka.actor.Actor

trait AltimeterProvider {
  def newAltimeter: Actor = Altimeter()
}
