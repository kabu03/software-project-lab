package gui;

import model.Saboteur;

import javax.swing.*;
import java.awt.*;

public class SaboteurGUI {
    private Saboteur saboteur;
    public ImageIcon saboteurImg = new ImageIcon("images/model.Saboteur.png");
    public SaboteurGUI(Saboteur saboteur){
        this.saboteur = saboteur;
    }

    public void draw(Graphics g){
        Point pos = saboteur.getPosition();
        g.drawImage(saboteurImg.getImage(), pos.x, pos.y, null);
    }
}
