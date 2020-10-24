package GUI;

import dcopsolver.computations_graph.DFSTree;

import javax.swing.*;
import java.awt.*;

public class GUI extends JFrame{

    Canvas graphic = new Canvas();

    public GUI(DFSTree tree){
        super("GUI window");
        setSize(500,500);
        setResizable(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        add(graphic);

        setVisible(true);
    }
}
