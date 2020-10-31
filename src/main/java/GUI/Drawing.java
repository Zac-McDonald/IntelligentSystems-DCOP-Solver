package GUI;

import dcopsolver.computations_graph.DFSNode;
import dcopsolver.computations_graph.DFSTree;
import dcopsolver.dcop.Variable;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Drawing extends JPanel {

    final int WIDTH = 800;
    final int HEIGHT = 1000;
    DFSTree tree;


    List<List<VariableGraphic>> layers = new ArrayList<>();

    public Drawing(DFSTree tree){
        super.setSize(WIDTH, HEIGHT);
        this.tree = tree;
        List<DFSNode> topLayer = new ArrayList<>(tree.getTopNodes());

        AddLayer(topLayer);
        FindVariablePositions();

    }

    private void AddLayer(List<DFSNode> layer){
        List<VariableGraphic> newLayer = new ArrayList<>();
        List<DFSNode> nextLayer = new ArrayList<>();

        for (DFSNode n: layer){
            newLayer.add(new VariableGraphic(n.getVar()));

            nextLayer.addAll(n.GetChildren(false));
        }

        layers.add(newLayer);

        if (nextLayer.size() > 0)
            AddLayer(nextLayer);
    }

    private void FindVariablePositions(){
        List<VariableGraphic> biggestLayer = null;
        int lNum = 1;
        int iter = 0;
        for (List<VariableGraphic> layer: layers){
            iter ++;
            if (biggestLayer == null)
                biggestLayer = layer;
            else if (layer.size() > biggestLayer.size()) {
                biggestLayer = layer;
                lNum = iter;
            }
        }

        assert biggestLayer != null;
        int w = WIDTH / (biggestLayer.size()+1);
        int h = (HEIGHT / (layers.size() + 1));

        iter = 1;
        for (List<VariableGraphic> layer: layers){
            for (VariableGraphic vG: layer){
                vG.y = h * iter;
            }
            iter ++;
        }

        iter = 0;
        for (VariableGraphic v: biggestLayer){
            iter++;
            v.x = w * iter;
        }

        int temp = lNum;
        while (temp >1){
            temp --;

            for (VariableGraphic vG: layers.get(temp - 1)){
                int tempX = 0;
                int count = 0;
                for (DFSNode n: tree.GetNodeFromVariable(vG.var).GetChildren(false)){
                    assert FindVariableGraphic(n.getVar()) != null;
                    tempX += FindVariableGraphic(n.getVar()).x;
                     count ++;
                }

                vG.x = tempX / count;
            }
        }

        temp = lNum;
        while (temp < layers.size()){
            for (VariableGraphic vG: layers.get(temp - 1)){
                int childCount = tree.GetNodeFromVariable(vG.var).GetChildren(false).size();
                int startX = vG.x - ((childCount - 1) * w / 2);

                for (DFSNode n: tree.GetNodeFromVariable(vG.var).GetChildren(false)){
                    assert FindVariableGraphic(n.getVar()) != null;
                    FindVariableGraphic(n.getVar()).x = startX;
                    startX += w;
                }
            }
            temp ++;
        }
    }

    private VariableGraphic FindVariableGraphic(Variable var){
        for (List<VariableGraphic> layer: layers){
            for (VariableGraphic vG: layer){
                if (var.equals(vG.var))
                    return vG;
            }
        }
        return null;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (List<VariableGraphic> layer: layers){
            for(VariableGraphic vG: layer){
                for (DFSNode n: tree.GetNodeFromVariable(vG.var).GetChildren(false)){
                    assert FindVariableGraphic(n.getVar()) != null;
                    g.drawLine(vG.x, vG.y, FindVariableGraphic(n.getVar()).x, FindVariableGraphic(n.getVar()).y);
                }
            }
        }

        g.setFont(new Font(g.getFont().getFontName(), Font.BOLD, 20));

        for (List<VariableGraphic> layer: layers){
            for(VariableGraphic vG: layer){
                vG.draw(g);
            }
        }
    }

}
