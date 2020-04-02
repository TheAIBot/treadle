/*
Copyright 2020 The Regents of the University of California (Regents)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package treadle.blackboxes

import firrtl.ir.Type
import treadle.{ScalaBlackBox, _}
import treadle.executable.{NegativeEdge, NoTransition, PositiveEdge, Transition}

class EicgWrapper(val instanceName: String) extends ScalaBlackBox {
  override def name: String = "EICG_Wrapper"

  var enableValue: BigInt = Big0
  var inputValue:  BigInt = Big0

  override def inputChanged(name: String, value: BigInt): Unit = {
    name match {
      case "en" => enableValue = value & 1
      case "in" => inputValue = value & 1
      case _    =>
    }
  }

  def getOutput(inputValues: Seq[BigInt], tpe: Type, outputName: String): BigInt = {
    enableValue & inputValue
  }

  override def clockChange(transition: Transition, clockName: String): Unit = {
    transition match {
      case NegativeEdge => inputValue = 0
      case NoTransition =>
      case PositiveEdge => inputValue = 1
      case _            =>
    }
  }

  // Don't use this.
  override def outputDependencies(outputName: String): Seq[String] = {
    Seq.empty
  }

  override def getDependencies: Seq[(String, collection.Set[String])] = {
    Seq("out" -> Set("in", "en"))
  }
}