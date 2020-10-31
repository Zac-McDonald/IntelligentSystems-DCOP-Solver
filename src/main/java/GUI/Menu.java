package GUI;

import javax.swing.*;
import java.awt.*;

public class Menu extends JPanel {
    final int WIDTH = 300;

    public Menu() {
        setLayout(null);

        JButton b = new JButton("button a");
        b.setLocation(0, 0);
        b.setSize(WIDTH, 200);
        add(b);

        JButton c = new JButton("button b");
        c.setLocation(0, 200);
        c.setSize(WIDTH, 200);
        add(c);

    }
}
