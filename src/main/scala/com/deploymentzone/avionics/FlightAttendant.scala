package com.deploymentzone.avionics

import scala.concurrent.duration._
import akka.actor.Actor

trait AttendantResponsiveness {
  val maxResponseTimeMS: Int

  def responseDuration = scala.util.Random.nextInt(maxResponseTimeMS).millis
}

object FlightAttendant {
  case class GetDrink(drinkName: String)
  case class Drink(drinkName: String)

  def apply() = new FlightAttendant with AttendantResponsiveness {
    val maxResponseTimeMS = 5.minutes
  }
}

class FlightAttendant extends Actor {
  this: AttendantResponsiveness =>
  import FlightAttendant._

  implicit val ec = context.dispatcher

  def receive = {
    case GetDrink(drinkName) =>
      context.system.scheduler.scheduleOnce(responseDuration, sender, Drink(drinkName))
  }

}
