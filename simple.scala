import scala.swing._
import event._
import scala.collection.immutable.TreeMap
import java.awt.Dimension
import java.lang.Thread
import scala.util.Random

/**
* Appends values to randomly selected keys in the map
* and highlights updated key in the list for some
* amount of time
* */
class UpdaterThread extends Thread {
  override def run() = {
    (1 to 100).foreach{_=>update}
  }
  def randomKey(i:Iterable[String]) = {
    val s = i.toSeq
    s(Random.nextInt(s.length))
  }
  def update = {
    var key = ""
    synchronized {
      key = randomKey(App.strMap.keys)
      App.strMap = App.strMap.updated(key, Random.nextString(20))
    }
    /* Update gui in Swing's event dispatch thread*/
    Swing.onEDT {
      App.ilv.listData = App.strMap.toSeq; 
      App.ilv.selectIndices(App.ilv.listData.indexOf(key))
      App.ilv.repaint()
      App.update(App.label, key)
    }
    Thread.sleep(1000)
  }

}
/**
*
* Thread that puts generated strings to map in App
* */
class GeneratorThread extends Thread {
  override def run() = {
    (1 to 100).foreach(_=>update)
  }
  def update = {
    synchronized {
      val pair = (Random.nextString(10), Random.nextString(200))
      App.strMap +=  pair
    }
    /* Update gui in Swing's event dispatch thread*/
    Swing.onEDT {
      App.ilv.listData = App.strMap.toSeq; 
      App.ilv.selectIndices(App.ilv.listData.size - 1)
      App.ilv.repaint()
      App.update(App.label)
    }
    Thread.sleep(3000)
  }

}

object App extends SimpleSwingApplication {
  override def startup(args : Array[String]) {
    super.startup(args)
    new GeneratorThread().start()
    new UpdaterThread().start()
  }
  val defsize = (400, 400)
  var strMap = TreeMap[String, String]() +
   ("ala" -> "ma kota",
    "ala2" -> "aslds as         sfsdfss",
    "ala3" -> "xxx"
  )

  val area = new TextArea(20, 50) {
    lineWrap = true
    editable = false
  }
  val ilv = new ListView(strMap.toSeq){
    requestFocus()
    renderer = ListView.Renderer(_._1)
  }
  var lastdata = ""
  def update(label:Label, data:String = "") = {
    if (data != lastdata) lastdata = data
    label.text = 
      "<html><font color=#672638 size=24px> Lista " + 
      ilv.listData.length + 
      " napisów. <br/>"+
      lastdata +
      " </font>  </html>"
  }
    val label = new Label {
      size = new Dimension(0, 300)
      minimumSize  = size
      preferredSize = size

      update(this)
    }
  def top = new MainFrame {
    title = "Simple frame"
    preferredSize = new Dimension(defsize._1, defsize._2)
    contents  = new BorderPanel(){
      val dataPanel = new BorderPanel() {
        val lv = new ScrollPane(ilv)
        layout(lv) = BorderPanel.Position.North
        layout(area) = BorderPanel.Position.Center
        contents ++ Seq(lv, area)
        listenTo(ilv.mouse.clicks, ilv.selection)
        reactions += {
          case e:ListSelectionChanged[String] => {
            val selhead = ilv.selection.items
            synchronized{
              selhead.headOption map {s:(String, String) => 
                area.text = strMap.getOrElse(s._1, "dasdas")}
            }
          }
        }
      }
      
      layout(label) =  BorderPanel.Position.North
      layout(dataPanel) = BorderPanel.Position.Center
      contents ++ Seq(label, dataPanel)
      border = Swing.EmptyBorder(5, 5, 5, 5)
    }
  }
}
