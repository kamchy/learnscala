import scala.swing._
import scala.swing.Swing._
import event._
import scala.collection.immutable.TreeMap
import java.awt.Dimension
import java.lang.Thread
import scala.util.Random
import executor.Executor


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
  
  

  title = cfg.title
  preferredSize = cfg.defsize
  
  contents  = bordered(new BorderPanel(){
    val areaScroll = new ScrollPane(area)
    val commandField = bordered(label)
    listenTo(commandField)
    reactions += {
      case EditDone(commandField) => {
        area.append(exec(commandField.text) + "\n")
        commandField.selectAll

      }
    }
    
    layout(commandField) =  BorderPanel.Position.North
    layout(areaScroll) = BorderPanel.Position.Center
    contents ++ Seq(areaScroll, commandField)
  })
  
  def exec(s:String) = { Executor.execute(s) }
  def bordered[T <:Component] (c: T): T = {
    c.border = cfg.border
    return c
  }
}
