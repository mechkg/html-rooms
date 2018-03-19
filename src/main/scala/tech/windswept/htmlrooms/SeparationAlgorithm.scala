package tech.windswept.htmlrooms

import org.scalajs.dom
import org.scalajs.dom.ext.Color
import org.scalajs.dom.raw.CanvasRenderingContext2D

import scala.util.Random

case class Room(x: Int, y: Int, width: Int, height: Int, color: Color)

case class SeparationState(rooms: Array[Room]) extends AlgorithmState {

  def allOverlappingPairs = (for (a <- rooms.indices;
                                  b <- rooms.indices
                                  if (a < b)) yield (a, b))
    .filter {
      p =>
        val a = rooms(p._1)
        val b = rooms(p._2)

        (a.x < (b.x + b.width)) && ((a.x + a.width) > b.x) && (a.y < (b.y + b.height)) && ((a.y + a.height) > b.y)
    }

  def separate1d(aMin: Int, aMax: Int, bMin: Int, bMax: Int): (Int, Int) = {
    val aMid = (aMax - aMin) / 2
    val bMid = (bMax - bMin) / 2

    val point = (aMid + bMid) / 2

    if (aMid < bMid)
      (-Math.max(0, aMax - point), Math.max(0, point - bMin))
    else
      (Math.max(0, point - aMin), -Math.max(0, bMax - point))
  }

  def separate(a: Int, b: Int): (Int, Int, Int, Int) = {
    val ra = rooms(a)
    val rb = rooms(b)

    val (dxa, dxb) = separate1d(ra.x, ra.x + ra.width, rb.x, rb.x + rb.width)
    val (dya, dyb) = separate1d(ra.y, ra.y + ra.height, rb.y, rb.y + rb.height)

    (dxa, dya, dxb, dyb)
  }

  def nextStep(): Boolean = {

    val pairs = allOverlappingPairs

    dom.console.log(pairs.toString)

    if (pairs.isEmpty)
      true
    else {

      val dx = Array.fill(rooms.size)()
      val dy = Array.fill(rooms.size)(0)

      pairs.foreach {
        p =>
          val (dxa, dya, dxb, dyb) = separate(p._1, p._2)
          dx(p._1) += dxa
          dy(p._1) += dya
          dx(p._2) += dxb
          dy(p._2) += dyb
      }

      rooms.indices.foreach {
        i =>
          rooms(i) = rooms(i).copy(rooms(i).x + dx(i) / rooms.length, rooms(i).y + dy(i) / rooms.length)
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
  }
}


object SeparationAlgorithm extends Algorithm[SeparationState] {

  def randomSize(minSize: Int, maxSize: Int) = Random.nextInt(maxSize - minSize) + minSize

  def randomColor = Color(Random.nextInt(128) + 128, Random.nextInt(128) + 128, Random.nextInt(128) + 128)

  def randomRoom(minSize: Int, maxSize: Int) = {
    val width = randomSize(minSize, maxSize)
    val height = randomSize(minSize, maxSize)

    Room(-width / 2, -height / 2, width, height, randomColor)
  }

  def init(numberOfRooms: Int, minSize: Int, maxSize: Int): SeparationState =
    SeparationState(Array.fill(numberOfRooms)(randomRoom(minSize, maxSize)))
}
