package tech.windswept.htmlrooms

import org.scalajs.dom._
import org.scalajs.dom.html
import org.scalajs.dom.html.Button
import org.scalajs.dom.raw.{HTMLInputElement, HTMLSelectElement}

object Main {

  lazy val canvas = document.getElementById("canvas").asInstanceOf[html.Canvas]
  lazy val rc = canvas.getContext("2d").asInstanceOf[CanvasRenderingContext2D]

  // All in world units
  val canvasWidthInWorldUnits = 100.0
  var canvasHeightInWorldUnits = 10.0
  var pixelInWorldUnits = 0.0

  val gridMarkerSize = 0.1

  var algorithmState: Option[AlgorithmState] = None

  var updateIntervalId: Option[Int] = None

  def drawGrid() = {

    val leftBorder = -canvasWidthInWorldUnits * 0.5
    val rightBorder = canvasWidthInWorldUnits * 0.5
    val topBorder = -canvasHeightInWorldUnits * 0.5
    val bottomBorder = canvasHeightInWorldUnits * 0.5

    val xMin = Math.floor(leftBorder)
    val xMax = Math.floor(rightBorder)
    val yMin = Math.floor(topBorder)
    val yMax = Math.floor(bottomBorder)

    rc.fillStyle = "grey"

    for (x <- xMin.to(xMax).by(1.0);
         y <- yMin.to(yMax).by(1.0)
    )
      yield {
        rc.fillRect(x - gridMarkerSize * 0.5, y - gridMarkerSize * 0.5, gridMarkerSize, gridMarkerSize)
      }
  }

  def repaint() = {
    rc.clearRect(-canvasWidthInWorldUnits * 0.5, -canvasHeightInWorldUnits * 0.5, canvasWidthInWorldUnits, canvasHeightInWorldUnits)
    drawGrid()
    algorithmState.foreach(_.drawState(rc))
  }

  def onResize() = {

    canvas.width = canvas.clientWidth
    canvas.height = canvas.clientHeight

    val worldUnitInPixels = canvas.clientWidth / canvasWidthInWorldUnits

    pixelInWorldUnits = 1.0 / worldUnitInPixels

    canvasHeightInWorldUnits = canvasWidthInWorldUnits * canvas.clientHeight / canvas.clientWidth

    rc.setTransform(worldUnitInPixels, 0, 0, worldUnitInPixels, canvas.clientWidth / 2, canvas.clientHeight / 2)

    repaint()
  }

  def update() = {
    algorithmState.foreach {
      state =>
        if (state.nextStep()) {
          console.log("Algorithm terminated")
          updateIntervalId.foreach(window.clearInterval)
          updateIntervalId = None
        }
        repaint()
    }
  }

  def init() = {

    updateIntervalId.foreach(window.clearInterval)

    val number = document.getElementById("numberOfRooms").asInstanceOf[HTMLInputElement].value.toInt
    val updateInterval = document.getElementById("stepDelay").asInstanceOf[HTMLInputElement].value.toInt

    document.getElementById("algorithmSelect").asInstanceOf[HTMLSelectElement].value match {
      case "1" =>
        algorithmState = Some(SeparationAlgorithm.init(number, 2, 15))
        updateIntervalId = Some(window.setInterval(() => update, updateInterval))

      case x => console.error(s"Unexpected algorithm value: $x")
    }
  }

  def main(args: Array[String]): Unit = {

    window.onresize = _ => onResize
    window.onload = _ => onResize

    document.getElementById("startButton").asInstanceOf[Button].onclick = _ => init
  }
}
