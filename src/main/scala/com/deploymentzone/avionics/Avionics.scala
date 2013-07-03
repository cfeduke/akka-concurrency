package com.deploymentzone.avionics

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import akka.util.Timeout
import akka.actor.{ActorRef, Props, ActorSystem}
import scala.concurrent.Await
import akka.pattern.ask

object Avionics {
  implicit val timeout = Timeout(5.seconds)
  val system = ActorSystem("PlaneSimulation")
  val plane = system.actorOf(Props[Plane], "Plane")

  def main(args: Array[String]) {
    import ControlSurfaces._

    val control = Await.result((plane ? Plane.GiveMeControl).mapTo[ActorRef], 5.seconds)

    def scheduleStick(delay: FiniteDuration, message : StickCommand) {
      system.scheduler.scheduleOnce(delay) {
        control ! message
      }
    }

    scheduleStick(200.millis, StickBack(1f))
    scheduleStick(1.seconds, StickBack(0f))
    scheduleStick(3.seconds, StickBack(0.5f))
    scheduleStick(4.seconds, StickBack(0f))
    system.scheduler.scheduleOnce(5.seconds) {
      system.shutdown()
    }
  }
}
