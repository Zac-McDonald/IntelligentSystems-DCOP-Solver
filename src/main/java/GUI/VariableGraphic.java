package GUI;

import dcopsolver.dcop.Variable;

import java.awt.*;
import java.util.Random;

public class VariableGraphic {

    final int RADIUS = 50;

    int x = 0;
    int y = 0;
    Variable var;

    public VariableGraphic(Variable var){
        this.var = var;

        Random r = new Random();
    }

    public void draw(Graphics g){
        g.setColor(Color.WHITE);
        g.fillOval(x - RADIUS, y - RADIUS, 2*RADIUS, 2*RADIUS);
        g.setColor(Color.BLACK);
        g.drawOval(x - RADIUS, y - RADIUS, 2*RADIUS, 2*RADIUS);


        FontMetrics metrics = g.getFontMetrics();

        String s = var.getName() + ": " + var.getInitialValue().toString();

        g.drawString( s, x - (metrics.stringWidth(s)/2), y + (metrics.getHeight()/2));
    }
}
