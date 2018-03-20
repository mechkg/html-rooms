package tech.windswept.htmlrooms

import org.scalajs.dom.raw.CanvasRenderingContext2D


case class TetrisState() extends AlgorithmState {
  def nextStep(): Boolean = ???

  def drawState(rc: CanvasRenderingContext2D): Unit = ???
}

object TetrisAlgorithm extends Algorithm[TetrisState] {

  def init(numberOfRooms: Int, minSize: Int, maxSize: Int): TetrisState =
    TetrisState()
}
