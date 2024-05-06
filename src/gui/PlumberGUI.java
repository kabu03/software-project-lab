package gui;

import model.Plumber;

import javax.swing.*;
import java.awt.*;

public class PlumberGUI {
    private Plumber plumber;
    public ImageIcon plumberImg = new ImageIcon("images/model.Plumber.png");
    public PlumberGUI(Plumber plumber){
        this.plumber = plumber;
    }


    public void draw(Graphics g){
        Point pos = plumber.getPosition();
        g.drawImage(plumberImg.getImage(), pos.x, pos.y, null);
    }
}
