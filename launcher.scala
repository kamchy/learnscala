import scala.swing._
import scala.swing.Swing._
import event._
import scala.collection.immutable.TreeMap
import java.awt.Dimension
import java.lang.Thread
import scala.util.Random


object App extends SimpleSwingApplication {
  val defsize = (600, 400)
  val emptyBorder = CompoundBorder(EtchedBorder(Lowered), EmptyBorder(5, 5, 5, 5))

  def label = new TextField {
    background = new Color(200, 250, 198)
    columns = 20
  }

  val area = new TextArea(20, 50) {
    lineWrap = true
    editable = false
  }


  def bordered[T <:Component] (c: T): T = {
    c.border = emptyBorder
    return c
  }

  def top = new MainFrame {
    title = "Simple frame"
    preferredSize = new Dimension(defsize._1, defsize._2)
    contents  = bordered(new BorderPanel(){
      val areaScroll = new ScrollPane(bordered(area))
      val commandField = bordered(label)
      listenTo(commandField)
      reactions += {
        case EditDone(commandField) => {
          area.append(commandField.text + "\n")
          commandField.selectAll

        }
      }
      
      layout(commandField) =  BorderPanel.Position.North
      layout(areaScroll) = BorderPanel.Position.Center
      contents ++ Seq(areaScroll, commandField)
    })
    

  }
}
