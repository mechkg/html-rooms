package tech.windswept.htmlrooms

import org.scalajs.dom.raw.CanvasRenderingContext2D

trait AlgorithmState {
  def nextStep(): Boolean

  def drawState(rc: CanvasRenderingContext2D): Unit
}

trait Algorithm[S <: AlgorithmState] {
  def init(numberOfRooms: Int, minSize: Int, maxSize: Int): S
}
