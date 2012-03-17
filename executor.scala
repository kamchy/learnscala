package executor 
import java.io._
import scala.actors._
import scala.actors.Actor._


 
case class Response (s: String)
case class Command(s:String)
case class Error(s:String)
  
class Executor extends Actor {

private val WAIT_TIME = 2000
private val TEST_WAIT = 5000

  def act = {
    loop {
      self.receive {
        case cmd@Command(command) => 
          try {
            val proc = createAndStartProcess(command)
            execute(proc, command)
          } catch {
            case e : IOException => sender ! Error(e.getMessage)
          }
      }
    }
  }

  def execute(proc : Process, cmd : String) {
    println("Executor received %s, creating reader".format(cmd))
    val reader = new Reader(sender).start
    println("reader started")
    reader !  proc
  }

  def createAndStartProcess (command : String) : Process = {
    val cmd = command.split(" ")
    val pb = new ProcessBuilder(cmd : _*)
    pb.redirectErrorStream(true)
    val proc = pb.start()
    return proc
  }


  def printProcessData(cmd : String) = {
    "reader " + Thread.currentThread + ": " + cmd
  }

  def waitSome() = {
    try {
      Thread.sleep(TEST_WAIT)
    } catch {
      case e : Exception => throw new RuntimeException()
    }
  }
   


 }
