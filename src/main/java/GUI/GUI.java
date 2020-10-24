package GUI;

import dcopsolver.computations_graph.DFSTree;

import javax.swing.*;

public class GUI extends JFrame{

    Graphic graphic;

    public GUI(DFSTree tree){
        super("GUI window");
        setSize(1200,1000);
        setResizable(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        graphic = new Graphic(tree);
        add(graphic);

        setVisible(true);
    }
}
