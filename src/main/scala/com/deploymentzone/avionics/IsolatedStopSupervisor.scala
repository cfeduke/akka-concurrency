package com.deploymentzone.avionics

import scala.concurrent.duration.Duration
import akka.actor.{ActorKilledException, ActorInitializationException}
import akka.actor.SupervisorStrategy.{Escalate, Stop}

abstract class IsolatedStopSupervisor(maxNrRetries: Int = -1, withinTimeRange: Duration = Duration.Inf)
  extends IsolatedLifeCycleSupervisor { this: SupervisionStrategyFactory =>

  override val supervisorStrategy = makeStrategy(maxNrRetries, withinTimeRange) {
    case _: ActorInitializationException => Stop
    case _: ActorKilledException => Stop
    case _: Exception => Stop
    case _ => Escalate
  }

}
