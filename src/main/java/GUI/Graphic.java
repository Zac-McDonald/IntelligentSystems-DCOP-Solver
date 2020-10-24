package GUI;

import dcopsolver.computations_graph.DFSTree;

import javax.swing.*;
import java.awt.*;

public class Graphic extends Canvas {

    public Graphic(DFSTree tree){
        super.setSize(400, 400);
    }

    public void paint(Graphics g) {
        g.fillOval(100, 100, 200, 200);
    }
}
