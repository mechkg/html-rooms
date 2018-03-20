package tech.windswept.htmlrooms

import org.scalajs.dom
import org.scalajs.dom.ext.Color
import org.scalajs.dom.raw.CanvasRenderingContext2D

import scala.collection.mutable.Buffer


case class MinDistanceState(var roomsToPlace: Int, minSize: Int, maxSize: Int, rooms: Buffer[Room], var placementPoints: Set[Vec2]) extends AlgorithmState {


  def getEdgePoints(room: Room): Set[Vec2] = {

    val result = Buffer[Vec2]()

    room.x.to(room.x + room.width).foreach {
      x =>
        result.append(Vec2(x, room.y - 2))
        result.append(Vec2(x, room.y + room.height + 2))
    }

    (room.y + 1).to(room.y + room.height - 1).foreach {
      y =>
        result.append(Vec2(room.x - 2 , y))
        result.append(Vec2(room.x + room.width + 2, y))
    }

    result.toSet
  }

  def validPlacement(room: Room) = !rooms.exists(fixedRoom => Utils.overlap(room, fixedRoom))


  def getValidPlacements(width: Int, height: Int, placementPoints: Set[Vec2]): Buffer[Vec2] = {
    val room = Room(0, 0, width, height, Color(0, 0, 0))

    val result = Buffer[Vec2]()

    placementPoints.foreach {
      p =>
        // UL
        room.x = p.x
        room.y = p.y

        if (validPlacement(room))
          result.append(Vec2(room.x, room.y))

        // UR
        room.x = p.x - room.width
        room.y = p.y

        if (validPlacement(room))
          result.append(Vec2(room.x, room.y))

        // LL
        room.x = p.x
        room.y = p.y - room.height

        if (validPlacement(room))
          result.append(Vec2(room.x, room.y))

        // LR
        room.x = p.x - room.width
        room.y = p.y - room.height

        if (validPlacement(room))
          result.append(Vec2(room.x, room.y))
    }

    result
  }

  def nextStep(): Boolean = {
    if (roomsToPlace == 0)
      true
    else {
      roomsToPlace -= 1
      val nextRoom = Utils.randomRoom(minSize, maxSize)

      if (rooms.isEmpty) {
        rooms.append(nextRoom)
        placementPoints = getEdgePoints(nextRoom)
      } else {

        val validPlacements = getValidPlacements(nextRoom.width, nextRoom.height, placementPoints)

        val bestPlacement = validPlacements.minBy {
          p =>
            val cx = (p.x + nextRoom.width) / 2
            val cy = (p.y + nextRoom.height) / 2

            cx * cx + cy * cy
        }

        nextRoom.x = bestPlacement.x
        nextRoom.y = bestPlacement.y

        val newPlacementPoints = getEdgePoints(nextRoom)

        placementPoints = (placementPoints ++ newPlacementPoints)
        rooms.append(nextRoom)
      }

      false
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

    /* rc.lineWidth = 0.125
    rc.strokeStyle = "lightgreen"

    placementPoints.foreach {
      p =>
        rc.beginPath()
        rc.moveTo(p.x - 0.3, p.y - 0.3)
        rc.lineTo(p.x + 0.3, p.y + 0.3)
        rc.moveTo(p.x + 0.3, p.y - 0.3)
        rc.lineTo(p.x - 0.3, p.y + 0.3)
        rc.stroke()
    }*/
  }
}

object MinDistanceAlgorithm extends Algorithm[MinDistanceState] {

  def init(numberOfRooms: Int, minSize: Int, maxSize: Int): MinDistanceState =
    MinDistanceState(numberOfRooms, minSize, maxSize, Buffer(), Set())
}
