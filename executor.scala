package executor 
import java.io._
import scala.actors._
import scala.actors.Actor._

 
  
object Executor {

private val caller = self
private val WAIT_TIME = 2000

  def execute (command : String) : String = {
    val cmd = command.split(" ")
    val pb = new ProcessBuilder(cmd : _*)
    pb.redirectErrorStream(true)
    val proc = pb.start()

    reader ! (proc, command)
    //Receive the console output from the actor.
    receiveWithin(WAIT_TIME) {
      case TIMEOUT => "receiving Timeout"
      case result:String => result
    }
  }


  private val reader = actor {
    println("created actor: " + Thread.currentThread)
    var continue = true
    loopWhile(continue){
      receive {
        case (proc:Process, cmd:String) =>
          printProcessData(cmd)
          val streamReader = new InputStreamReader(proc.getInputStream)
          val bufferedReader = new BufferedReader(streamReader)
          val stringBuilder = new StringBuilder()
          var line:String = null
          while({line = bufferedReader.readLine; line != null}){
             stringBuilder.append(line)
             stringBuilder.append("\n")
           }
           bufferedReader.close
           caller ! stringBuilder.toString
         }
      }
   }

   def printProcessData(cmd : String) = {
     "reader " + Thread.currentThread + ": " + cmd
   }


 }
