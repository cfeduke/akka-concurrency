package com.deploymentzone.avionics

import akka.actor.Props

object FlightAttendantPathChecker {
  def main(args: Array[String]) {
    val system = akka.actor.ActorSystem("PlaneSimulation")
    val lead = system.actorOf(Props(new LeadFlightAttendant with AttendantCreationPolicy),
      system.settings.config.getString("com.deploymentzone.avionics.flightcrew.leadAttendantName"))
    Thread.sleep(2000)
    system.shutdown()
  }
}
