package model;

import java.awt.*;

/**
 * Represents the terminal endpoints of a pipe, facilitating attachment and detachment from other
 * elements. It retains information about the connected element for later retrieval.
 */


public class EndOfPipe {
    private Point position;
    private boolean atStart;
    public int width;
    public int height;
    public Pipe currentPipe;
    private boolean visible = true;
    public Point getPosition() {
        return position;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean contains(int x, int y) {
        int expandArea = 30;
        return  x >= getPosition().x - expandArea && x <= getPosition().x + width + expandArea &&
                y >= getPosition().y - expandArea && y <= getPosition().y + height + expandArea;
    }

    public EndOfPipe(Pipe p, boolean atStart)
    {
        currentPipe = p;
        this.atStart = atStart;
        width = 15;
        height = 15;
        for(int i = 0; i < 2; i++)
        {
            if(p.endsOfPipe[i] == null)
            {
                p.endsOfPipe[i] = this;
                break;
            }
        }
        setPosition();
    }
    /**
     * An attribute that stores the element that is currently connected to the end of pipe object.
     */
    private Element connectedElement;

    /**
     * The pipe that this end of the pipe is associated with.
     */

    /**
     * A method that connects the end of pipe object to an element in the system.
     * @param e The element that should be connected to.
     */
    public void connectToElement(Element e){
        connectedElement = e;
        e.connectedPipes.add(currentPipe);
    }

    /**
     * A method that disconnects the end of pipe object from an element in the system.
     * @param e The element that should be disconnected from.
     */
    public void disconnectFromElement(Element e){
        connectedElement = null;
        e.connectedPipes.remove(currentPipe);
    }

    /**
     * Retrieves the element currently connected to this end of the pipe.
     *
     */
    public Element getConnectedElement(){
        return connectedElement;
    }
    /**
     * Sets a new pipe to this end of the pipe, updating the connected element's list of connected pipes
     * if there is one.
     *
     * @param p The new pipe to be associated with this end of the pipe.
     */
    public void setCurrentPipe(Pipe p)
    {
        if(connectedElement != null) {
            connectedElement.connectedPipes.remove(currentPipe);
            currentPipe = p;
            connectedElement.connectedPipes.add(currentPipe);
        }
        else
        {
            currentPipe = p;
        }
    }
    private void setPosition() {
        int x, y;
        int manualOffsetX = -25;

        if (currentPipe.vertical) {
            x = currentPipe.getPosition().x + ((currentPipe.width - width) / 2) + manualOffsetX;

            if (atStart) {
                y = currentPipe.getPosition().y;
            } else {
                y = currentPipe.getPosition().y + currentPipe.height - height - 40;
            }
        } else {
            if (atStart) {
                x = currentPipe.getPosition().x;
                y = currentPipe.getPosition().y - height;
            } else {
                x = currentPipe.getPosition().x + currentPipe.width - width;
                y = currentPipe.getPosition().y - height;
            }
        }

        this.position = new Point(x, y);
    }

}
