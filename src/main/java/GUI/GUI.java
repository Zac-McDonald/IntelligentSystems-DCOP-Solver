package GUI;

import dcopsolver.computations_graph.DFSTree;
import jadex.bridge.IExternalAccess;

import javax.swing.*;
import java.util.concurrent.TimeUnit;

public class GUI extends JFrame implements Runnable{

    final int WIDTH = 1200;
    final int HEIGHT = 1000;

    Drawing drawing;

    public GUI(DFSTree tree, IExternalAccess agent){
        super("GUI window");
        setSize(WIDTH, HEIGHT);
        setResizable(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        drawing = new Drawing(tree);
        add(drawing);

        setVisible(true);
    }


    public void run() {
        while (true) {
            drawing.repaint();

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public Drawing getGraphic() { return drawing; }

    public void setGraphic(Drawing graphic) { this.drawing = graphic; }
}
