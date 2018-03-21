package tech.windswept.htmlrooms

import org.scalajs.dom
import org.scalajs.dom.ext.Color
import org.scalajs.dom.raw.CanvasRenderingContext2D

import scala.collection.mutable
import scala.util.Random

case class TetrisState(var roomsToPlace: Int, minSize: Int, maxSize: Int,
                       var currentRoom: Room,
                       var placementInProgress: Boolean,
                       var currentDirection: Int,
                       var leftBound: Int, var topBound: Int, var rightBound: Int, var bottomBound: Int, rooms: mutable.Buffer[Room]) extends AlgorithmState {

  val directionVectors = Array(
    Vec2(-1, 0),
    Vec2(0, -1),
    Vec2(1, 0),
    Vec2(0, 1)
  )

  def randomRoom(minSize: Int, maxSize: Int) = {
    val size = Utils.randomSize(minSize, maxSize)

    val width = Math.max(minSize, Math.min(maxSize, size + Random.nextInt(5) - 2))
    val height = Math.max(minSize, Math.min(maxSize, size + Random.nextInt(5) - 2))

    if (currentDirection == 1 || currentDirection == 3) {
      val horizontalSlack = rightBound - leftBound - width
      Room(leftBound + Random.nextInt(Math.max(horizontalSlack, 1)), -height / 2, width, height, Utils.randomColor)
    } else {
      var verticalSlack =  bottomBound - topBound - height
      Room(-width / 2, topBound + Random.nextInt(Math.max(verticalSlack, 1)), width, height, Utils.randomColor)
    }
  }

  def validPlacement(room: Room) = !rooms.exists(fixedRoom => Utils.overlap(room, fixedRoom))

  def updateBounds() = {

    var xMin = Int.MaxValue
    var xMax = Int.MinValue
    var yMin = Int.MaxValue
    var yMax = Int.MinValue

    rooms.foreach {
      r =>
        xMin = Math.min(xMin, r.x)
        xMax = Math.max(xMax, r.x + r.width)
        yMin = Math.min(yMin, r.y)
        yMax = Math.max(yMax, r.y + r.height)
    }

    leftBound = xMin - maxSize
    rightBound = xMax + maxSize
    topBound = yMin - maxSize
    bottomBound = yMax + maxSize
  }

  def nextStep(): Boolean = {
    if (roomsToPlace == 0)
      true
    else {
      if (placementInProgress) {

        if (validPlacement(currentRoom)) {
          placementInProgress = false
          rooms.append(currentRoom)
          updateBounds()
          currentDirection = (currentDirection + 1) % 4
        }
        else {
          currentRoom.x += directionVectors(currentDirection).x
          currentRoom.y += directionVectors(currentDirection).y
        }
        false
      } else {
        roomsToPlace -= 1

        currentRoom = randomRoom(minSize, maxSize)

        placementInProgress = true

        false
      }
    }
  }

  def drawState(rc: CanvasRenderingContext2D): Unit = {
    rc.lineWidth = 0.2
    rc.strokeStyle = "black"

    rooms.foreach {
      room =>
        rc.fillStyle = room.color.toHex
        rc.fillRect(room.x, room.y, room.width, room.height)
        rc.strokeRect(room.x, room.y, room.width, room.height)
    }

    if (roomsToPlace != 0) {
      rc.fillStyle = currentRoom.color.toHex
      rc.fillRect(currentRoom.x, currentRoom.y, currentRoom.width, currentRoom.height)
      rc.strokeRect(currentRoom.x, currentRoom.y, currentRoom.width, currentRoom.height)
    }
  }
}


object TetrisAlgorithm extends Algorithm[TetrisState] {
  def init(numberOfRooms: Int, minSize: Int, maxSize: Int): TetrisState =
    TetrisState(numberOfRooms, minSize, maxSize, Room(0, 0, 0, 0, Color(0, 0, 0)), false, 0, -maxSize / 2,
      -maxSize / 2, maxSize / 2, maxSize / 2, mutable.Buffer())
}
