package tech.windswept.htmlrooms

import org.scalajs.dom.ext.Color

import scala.util.Random

case class Room(var x: Int, var y: Int, width: Int, height: Int, color: Color)

case class Vec2(var x: Int, var y: Int) {
  def +=(other: Vec2): Unit = {
    this.x += other.x
    this.y += other.y
  }

  def +(other: Vec2): Vec2 = Vec2(this.x + other.x, this.y + other.y)
}

case class Vec2Acc(var x: Int, var y: Int, var n: Int) {
  def +=(v: Vec2) = {
    this.x += v.x
    this.y += v.y
    this.n += 1
  }

  def average = Vec2(x / n, y / n)
}

object Utils {
  def randomSize(minSize: Int, maxSize: Int) = Random.nextInt(maxSize - minSize) + minSize

  def randomColor = Color(Random.nextInt(128) + 128, Random.nextInt(128) + 128, Random.nextInt(128) + 128)

  def overlap(a: Room, b: Room) =
    (a.x < (b.x + b.width)) && ((a.x + a.width) > b.x) && (a.y < (b.y + b.height)) && ((a.y + a.height) > b.y)

  def randomRoom(minSize: Int, maxSize: Int) = {

    val size = randomSize(minSize, maxSize)

    val width = Math.max(minSize, Math.min(maxSize, size + Random.nextInt(5) - 2))
    val height = Math.max(minSize, Math.min(maxSize, size + Random.nextInt(5) - 2))

    Room(-width / 2 + Random.nextInt(5) - 2, -height / 2 + Random.nextInt(5) - 2, width, height, randomColor)
  }
}
