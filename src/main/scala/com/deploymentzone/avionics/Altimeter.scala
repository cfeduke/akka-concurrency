package com.deploymentzone.avionics

import akka.actor.{ActorLogging, Actor}
import scala.concurrent.duration._

object Altimeter {
  case class RateChange(amount: Float)
  case class AltitudeUpdate(altitude: Double)
}

class Altimeter extends Actor with ActorLogging with EventSource {
  import Altimeter._

  implicit val ec = context.dispatcher

  val ceiling = 43000

  val maxRateOfClimb = 5000

  var rateOfClimb = 0f

  var altitude = 0d

  var lastTick = System.currentTimeMillis()

  val ticker = context.system.scheduler.schedule(100.millis, 100.millis, self, Tick)

  case object Tick

  def altimeterReceive: Receive = {
    case RateChange(amount) =>
      rateOfClimb = amount.min(1.0f).max(-1.0f) * maxRateOfClimb
      log.info(s"Altimeter changed rate of climb to $rateOfClimb")
    case Tick =>
      val tick = System.currentTimeMillis
      altitude = altitude + (tick - lastTick) / 60000.0 * rateOfClimb
      lastTick = tick
      sendEvent(AltitudeUpdate(altitude))
  }

  def receive = eventSourceReceive orElse altimeterReceive

  override def postStop() {
    ticker.cancel()
  }

}
