package tech.windswept.htmlrooms

import org.scalajs.dom.raw.CanvasRenderingContext2D

import scala.util.Random

case class SeparationState(rooms: Array[Room]) extends AlgorithmState {


  def overlappingPairs = (for (a <- rooms.indices;
                               b <- rooms.indices
                               if (a < b && Utils.overlap(rooms(a), rooms(b)))) yield (a, b))

  def separationVector1d(aMin: Int, aMax: Int, bMin: Int, bMax: Int): (Int, Int) = {
    val aMid = (aMin + aMax) / 2
    val bMid = (bMin + bMax) / 2

    val point = (aMid + bMid) / 2

    if (aMid < bMid)
      (-Math.max(0, aMax - point), Math.max(0, point - bMin))
    else
      (Math.max(0, point - aMin), -Math.max(0, bMax - point))
  }

  def separationVector(a: Int, b: Int): (Vec2, Vec2) = {
    val ra = rooms(a)
    val rb = rooms(b)

    val (dxa, dxb) = separationVector1d(ra.x, ra.x + ra.width, rb.x, rb.x + rb.width)
    val (dya, dyb) = separationVector1d(ra.y, ra.y + ra.height, rb.y, rb.y + rb.height)

    (Vec2(dxa + Random.nextInt(3) - 1, dya + Random.nextInt(3) - 1), Vec2(dxb + Random.nextInt(3) - 1, dyb + Random.nextInt(3) - 1))
  }

  def nextStep(): Boolean = {

    val pairs = overlappingPairs

    if (pairs.isEmpty)
      true
    else {

      val acc = Array.fill(rooms.size)(Vec2Acc(0, 0, 0))

      pairs.foreach {
        p =>
          val (va, vb) = separationVector(p._1, p._2)
          acc(p._1) += va
          acc(p._2) += vb
      }

      rooms.indices.foreach {
        i =>
          if (acc(i).n != 0) {
            val avg = acc(i).average
            rooms(i).x += avg.x
            rooms(i).y += avg.y
          }
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

  def init(numberOfRooms: Int, minSize: Int, maxSize: Int): SeparationState =
    SeparationState(Array.fill(numberOfRooms)(Utils.randomRoom(minSize, maxSize)))
}
