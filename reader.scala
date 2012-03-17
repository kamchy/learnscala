package executor

import java.io._
import scala.actors.{Actor, OutputChannel}
import scala.actors.Actor._



class Reader (sendTo : OutputChannel[Any]) extends Actor {
  def act = {
    val name = Thread.currentThread
    println("created actor: " + name)
    var continue = true
    loopWhile(continue){
      receive {
        case proc : Process => 
          println ("Process results are processed by " + name)
          val streamReader = new InputStreamReader(proc.getInputStream)
          val bufferedReader = new BufferedReader(streamReader)
          var line:String = null
          while({line = bufferedReader.readLine; line != null}){
            sendTo ! line
           }
           continue = false
           bufferedReader.close
         }
      }
   }
 }

