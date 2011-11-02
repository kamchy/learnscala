import scala.swing._
import java.awt.Dimension
import event._
import scala.collection.mutable.LinkedList

object App extends SimpleSwingApplication {
  var strList = LinkedList[String]()

  override def startup(args: Array[String]){
    strList = strList ++ args
    super.startup(args)
  }

  def top = new MainFrame {
    title = "Simple frame"
    preferredSize = new Dimension(800, 600)
    var slowo = "Lista napisów"
    contents = new BorderPanel(){
      val label = new Label {
        text = "<html><font color=#672638 size=24px>" + slowo + "</font>  </html>"
        minimumSize = new Dimension(0, 300)
      }
      val dataPanel = new BorderPanel() {
        val ilv = new ListView(strList){
        }
        val lv = new ScrollPane(ilv)
        val area = new TextArea(20, 50) {
          lineWrap = true
          editable = false
        }
        layout(lv) = BorderPanel.Position.North
        layout(area) = BorderPanel.Position.Center
        contents ++ Seq(lv, area)
        listenTo(ilv.mouse.clicks, ilv.selection)
        reactions += {
          case e:MouseClicked => {
              println("list view clicked")
          }
          case e:ListSelectionChanged[String] => {
            println("list view sel changed")
          }
        }
      }
      
      layout(label) =  BorderPanel.Position.North
      layout(dataPanel) = BorderPanel.Position.Center
      contents ++ Seq(label, dataPanel)
    }
  }
}
