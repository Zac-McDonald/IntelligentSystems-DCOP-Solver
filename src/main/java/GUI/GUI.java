package GUI;

import dcopsolver.computations_graph.DFSTree;
import jadex.bridge.IExternalAccess;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.TimeUnit;

public class GUI extends JFrame{

    final int WIDTH = 1200;
    final int HEIGHT = 1000;

    JPanel drawing;
    JPanel menu;
    Timer timer;
    Container container;

    public GUI(DFSTree tree, IExternalAccess agent){
        super("GUI window");
        setSize(WIDTH, HEIGHT);
        setResizable(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        getContentPane().setLayout(new BorderLayout());

        drawing = new Drawing(tree);
        getContentPane().add(drawing, BorderLayout.WEST);

        menu = new Menu();
        getContentPane().add(menu, BorderLayout.EAST);


        timer = new Timer(0, ae -> {
            repaint();
            revalidate();
        });

        timer.setRepeats(true);
        timer.setDelay(1000);
        timer.start();


        pack();
        setVisible(true);
    }

    public JPanel getGraphic() { return drawing; }

    public void setGraphic(Drawing graphic) { this.drawing = graphic; }
}
