import scala.swing._
import scala.swing.Swing._
import event._
import scala.collection.immutable.TreeMap
import java.awt.Dimension
import java.lang.Thread
import scala.util.Random
import executor._
import scala.actors._
import scala.actors.Actor._


class AppConfig {
  val defsize = (600, 400)
  val title = "Shell launcher"
  val border = CompoundBorder(EtchedBorder(Lowered), EmptyBorder(5, 5, 5, 5)) 
  val labelBgCol = new Color(200, 200, 198)
}

object App extends SimpleSwingApplication {
  val cfg = new AppConfig()
  def top = new MyFrame(cfg)
}

class MyFrame(cfg : AppConfig) extends MainFrame {
  val label = new TextField {
    background = cfg.labelBgCol
  }

  val area = new TextArea() {
    lineWrap = true
    editable = false
  }
  
  val commandField = bordered(label)
  

  title = cfg.title
  preferredSize = cfg.defsize
  
  contents  = bordered(new BorderPanel(){
    val areaScroll = new ScrollPane(area)
    listenTo(commandField)
    reactions += {
      case EditDone(commandField) => {
        if (commandField != "") {
          updater ! Command(commandField.text)
        }
      }
    }
    
    layout(commandField) =  BorderPanel.Position.North
    layout(areaScroll) = BorderPanel.Position.Center
    contents ++ Seq(areaScroll, commandField)
  })
  
  val executor = new Executor().start
  
  val updater = actor {
    loop {
      receive {
        case s : String => {
          println("updater received response " + s + ", want update gui in edt")
          onEDT(updateArea(s))
        }
        case Error(s:String) => {
          onEDT(updateAreaWithError(s))
        }
        case cmd@Command(s : String) => {
          println("updater received command " + s + ", sending to executor")
          executor ! cmd
        }

      }
    }
  }
  def updateAreaWithError(s : String) {
    updateArea("\n****** [" + s + "] *****\n")
  }
  def updateArea(s:String) = { 
    area.append(s + "\n")
    commandField.selectAll
  }

  def bordered[T <:Component] (c: T): T = {
    c.border = cfg.border
    return c
  }
}
