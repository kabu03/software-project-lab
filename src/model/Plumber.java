package model;

import gui.MapGUI;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static gui.MapGUI.getSelectedEndOfPipe;
import static gui.MapGUI.selectedEndOfPipe;
import static java.lang.System.exit;

/**
 * Represents players tasked with maintaining and repairing the pipe system. Plumbers can fix
 * broken pumps, repair leaking pipes, manage pipe ends, and extend the system. They play a
 * crucial role in setting pump directions and defending against sabotage, with their actions
 * being vital for water transfer efficiency and system operation.
 */
public class Plumber extends Player {
    public Pump pickedUpPump=null;
    public EndOfPipe pickedUpEoP;

    public Plumber(String playerName) {
        this.playerName = playerName;
    }

    public int newPipecount=1;
    public int newPumpCount=1;
    public Game gameInstance;

    /**
     * Allows the model.Plumber player to take their turn.
     * Displays available actions and prompts the player to choose one.
     *
     * Overrides the abstract takeTurn method of the model.Player class.
     *
     *
     * @author Basel Al-Raoush
     * The method allows each player 2 actions to pick from in a 5-second interval for each turn.
     *                      In essence in each turn the player has either 2 actions to perform within 5 seconds.
     * @author Ibrahim Muheisen
     */
    @Override
    protected void takeTurn(Game g) {// should happen twice, user should be prompted twice EXCEPT FOR PASS TURN
        gameInstance = g;
        boolean passflag = false;
        int actionstaken=0;
        long turnStartTime = System.currentTimeMillis();
        long turnDuration = 10000; //


        while (System.currentTimeMillis() < turnStartTime + turnDuration && actionstaken < 2) {
            char choice = Character.toUpperCase(g.getCurrentAction());
            if (choice != '\0') {
                switch (choice) {
                    case 'Q':
                        System.out.println("You chose: Move to an element");
                        MapGUI.isMoveActive = true;
                        actionstaken++;
                        break;
                    case 'D':
                        System.out.println("You chose: Pick up a pump");
                        getPump(g);
                        actionstaken++;
                        break;
                    case 'I':
                        System.out.println("You chose: Insert pump into a pipe");
                        insertPump(g);
                        actionstaken++;

                        break;
                    case 'F':
                        System.out.println("You chose: Fix a broken pump");
                        fixPump();
                        actionstaken++;
                        break;
                    case 'O':
                        System.out.println("You chose: Fix a broken pipe");
                        fixPipe();
                        actionstaken++;
                        break;
                    case 'R':
                        System.out.println("You chose: Pick up the end of a pipe");

                        getEnd(currentElement);
                        actionstaken++;
                        break;
                    case 'T':
                        System.out.println("You chose: Insert the end of a pipe");
                        insertPipeEnd(currentElement);
                        actionstaken++;
                        break;
                    case 'A':
                        System.out.println("You chose: Change the input pipe of a pump");
                        if(currentElement instanceof Pump)
                        {
                            gameInstance.mapGUI.isChangingInputPipe = true;
                            gameInstance.mapGUI.isChangingOutputPipe = false;
                            gameInstance.mapGUI.selectedPump =(Pump) currentElement;
                            System.out.println("Please choose a pipe that is connected to the Pump");
                        }
                        else
                        {
                            System.out.println("You have to be standing on a pump to change the input pipe.");
                        }
                        actionstaken++;
                        break;
                    case 'S':
                        System.out.println("You chose: Change the output pipe of a pump");
                        if(currentElement instanceof Pump)
                        {
                            gameInstance.mapGUI.isChangingOutputPipe = true;
                            gameInstance.mapGUI.isChangingInputPipe = false;
                            gameInstance.mapGUI.selectedPump =(Pump) currentElement;
                            System.out.println("Please choose a pipe that is connected to the Pump");
                        }
                        else
                        {
                            System.out.println("You have to be standing on a pump to change the output pipe.");
                        }
                        actionstaken++;
                        break;
                    case 'W':
                        System.out.println("You chose: Pass Turn");
                        passflag = true;
                        passTurn();
                        return;
                        /*
                    case 'E':
                        System.out.println("You chose: End the game");
                        g.endGame();
                        exit(0);
                        break;

                         */
                    default:
                        System.out.println("Invalid input, please choose one of the valid options.");
                }
            }
        }

        if (passflag && actionstaken==1){
            return;
        }
    }


    /**
     * This method allows a plumber to pick up an end of a pipe from a selected pipe.
     * The plumber must be standing on the element where the pipe is connected.
     * The method lists all connected pipes to the element and prompts the user to select a pipe.
     * If the selected pipe is valid and connected to the element, the method attempts to pick up an end of the pipe.
     * The end of the pipe is disconnected from the element and the pipe, and is then assigned to the plumber.
     * If no free end is available to pick up from the selected pipe, an appropriate message is displayed.
     *
     * @param e The element from which an end of a pipe is to be picked up.
     * @author : Basel Al-Raoush , Nafez sayyad
     */
    public void getEnd(Element e) {
        // Retrieve the selectedEndOfPipe from MapGUI
        EndOfPipe selectedEndOfPipe = MapGUI.getSelectedEndOfPipe();

        // First, check if the plumber is standing on the element
        if (currentElement != selectedEndOfPipe.getConnectedElement()) {
            System.out.println("You have to be standing on the element to pick up the end of the pipe.");
            return;
        }

        // Check if the player has already picked up an end of pipe
        if (pickedUpEoP != null) {
            System.out.println("You have already picked up an end of pipe.");
            return;
        }

        // Check if the element is not an instance of Pipe
        if (e instanceof Pipe) {
            System.out.println("You can't pick up the end of a pipe from a pipe.");
            return;
        }

        // Check if the selected end of the pipe is connected to the current element
        boolean isConnected = false;
        for (EndOfPipe end : selectedEndOfPipe.currentPipe.endsOfPipe) {
            if (end != null) {
                isConnected = true;
                break;
            }
        }
        if (!isConnected) {
            System.out.println("The selected end of the pipe is not connected to the current element.");
            return;
        }

        // Set the visibility to false and update the picked-up end of the pipe
        selectedEndOfPipe.disconnectFromElement(e); // This should handle both the element and pipe updates
        selectedEndOfPipe.setCurrentPipe(null);
        pickedUpEoP = selectedEndOfPipe;
        gameInstance.endOfPipeList.remove(selectedEndOfPipe);
        gameInstance.mapGUI.repaint();
        System.out.println(playerName + " picked up the end of the pipe connected to " + currentElement.getName());
    }





    /**
     * This method allows a plumber to insert an end of a pipe to a selected element.
     * The plumber must be standing on the element where the pipe is to be connected.
     * The method checks if the plumber has picked up an end of a pipe and if the element is connectable.
     * If the selected element is valid and connectable, the method attempts to insert the end of the pipe.
     * The end of the pipe is connected to the element and the pipe, and is then removed from the plumber.
     * If the end of the pipe cannot be inserted to the element, an appropriate message is displayed.
     *
     * @param e The element to which an end of a pipe is to be inserted.
     * @author :Basel Al-Raoush , Nafez sayyad
     */
    public void insertPipeEnd(Element e) {
        // Check if the player is standing on the element
        if (currentElement != e) {
            System.out.println("You have to be standing on the element to insert the end of the pipe.");
            return;
        }

        // Check if the player has picked up an end of pipe
        if (pickedUpEoP == null) {
            System.out.println("No end of pipe picked up to insert.");
            return;
        }


            // List the connectable pipes that are not yet fully connected
            if (e.connectablePipes.isEmpty()) {
                System.out.println("There are no connectable pipes available at this element.");
                return;
            }
            System.out.println("Connectable pipes:");
            e.connectablePipes.forEach(pipe -> System.out.println(pipe.getName()));
            java.util.List<Pipe> connectableNotConnected = new ArrayList<>(e.connectablePipes);
            connectableNotConnected.removeAll(e.connectedPipes);
            if (connectableNotConnected.isEmpty()) {
                System.out.println("All connectable pipes are already fully connected.");
                return;
            }
            Pipe selectedPipe = connectableNotConnected.get(0);

            /* Prototype code for connecting the end of pipe to a pipe
            // Get user input on which pipe to connect the end to
            System.out.print("Enter the name of the pipe to insert the end into: ");
            String pipeName = "temp"; // temporarily set to a string
            Pipe selectedPipe = e.connectablePipes.stream()
                .filter(pipe -> pipe.getName().equals(pipeName))
                .findFirst()
                .orElse(null);
        if (selectedPipe == null) {
            System.out.println("Invalid pipe selection or not connectable.");
            return;
        }

        // Check if the selected pipe can accept more connections
        if (selectedPipe.endsOfPipe[0] != null && selectedPipe.endsOfPipe[1] != null) {
            System.out.println("Selected pipe already has both ends connected.");
            return;
        }
*/
        // Insert the end of pipe to the selected pipe and connect it to the element
        if(selectedPipe.endsOfPipe[0] == null && selectedPipe.endsOfPipe[1] != null)
        {
            boolean atStart = !selectedPipe.endsOfPipe[1].atStart;
            EndOfPipe temp;
            if((selectedPipe == gameInstance.pipeList.get(4) || selectedPipe == gameInstance.pipeList.get(5)) && atStart == false)
            {
                temp =  new EndOfPipe(selectedPipe, atStart, 0, 35);
            }
            else {
                temp = new EndOfPipe(selectedPipe, atStart, 0, 0);
            }
            selectedPipe.endsOfPipe[0] = temp;
            selectedPipe.endsOfPipe[0].connectToElement(e);
            pickedUpEoP = null;
            gameInstance.endOfPipeList.add(temp);
            gameInstance.mapGUI.repaint();
        }
        else if(selectedPipe.endsOfPipe[0] != null && selectedPipe.endsOfPipe[1] == null)
        {
            boolean atStart = !selectedPipe.endsOfPipe[0].atStart;
            EndOfPipe temp;
            if((selectedPipe == gameInstance.pipeList.get(4) || selectedPipe == gameInstance.pipeList.get(5)) && atStart == false)
            {
                temp =  new EndOfPipe(selectedPipe, atStart, 0, 35);
            }
            else {
                temp = new EndOfPipe(selectedPipe, atStart, 0, 0);
            }
            selectedPipe.endsOfPipe[1] = temp;
            selectedPipe.endsOfPipe[1].connectToElement(e);
            pickedUpEoP = null;
            gameInstance.endOfPipeList.add(temp);
            gameInstance.mapGUI.repaint();
        }
        else if(selectedPipe.endsOfPipe[0] == null && selectedPipe.endsOfPipe[1] == null) {
            boolean atStart = true;
            EndOfPipe temp;
            if((selectedPipe == gameInstance.pipeList.get(4) || selectedPipe == gameInstance.pipeList.get(5)) && atStart == false)
            {
                temp =  new EndOfPipe(selectedPipe, atStart, 0, 35);
            }
            else {
                temp = new EndOfPipe(selectedPipe, atStart, 0, 0);
            }
            selectedPipe.endsOfPipe[0] = temp;
            selectedPipe.endsOfPipe[0].connectToElement(e);
            pickedUpEoP = null;
            gameInstance.endOfPipeList.add(temp);
            gameInstance.mapGUI.repaint();
        }
        else if(selectedPipe.endsOfPipe[0].currentPipe == null && selectedPipe.endsOfPipe[1].currentPipe != null)
        {
            boolean atStart = !selectedPipe.endsOfPipe[1].atStart;
            EndOfPipe temp;
            if((selectedPipe == gameInstance.pipeList.get(4) || selectedPipe == gameInstance.pipeList.get(5)) && atStart == false)
            {
                temp =  new EndOfPipe(selectedPipe, atStart, 0, 35);
            }
            else {
                temp = new EndOfPipe(selectedPipe, atStart, 0, 0);
            }
            selectedPipe.endsOfPipe[0] = temp;
            selectedPipe.endsOfPipe[0].connectToElement(e);
            pickedUpEoP = null;
            gameInstance.endOfPipeList.add(temp);
            gameInstance.mapGUI.repaint();
        }
        else if(selectedPipe.endsOfPipe[0].currentPipe != null && selectedPipe.endsOfPipe[1].currentPipe == null)
        {
            boolean atStart = !selectedPipe.endsOfPipe[0].atStart;
            EndOfPipe temp;
            if((selectedPipe == gameInstance.pipeList.get(4) || selectedPipe == gameInstance.pipeList.get(5)) && atStart == false)
            {
                temp =  new EndOfPipe(selectedPipe, atStart, 0, 35);
            }
            else {
                temp = new EndOfPipe(selectedPipe, atStart, 0, 0);
            }
            selectedPipe.endsOfPipe[1] = temp;
            selectedPipe.endsOfPipe[1].connectToElement(e);
            pickedUpEoP = null;
            gameInstance.endOfPipeList.add(temp);
            gameInstance.mapGUI.repaint();
        }
    }


    /**
     * This method serves the purpose of fixing a punctured pipe.
     * if the pipe is punctured the plumber is allowed to fix if he is standing on it, otherwise the action will be aborted.
     * Conditions Checked: model.Plumber is occupying a pipe that has been punctured.
     * @author Ibrahim
     */
    public  void fixPipe() {
        if (currentElement instanceof Pipe && currentElement.isWorking())
            System.out.println(playerName + " attempted to fix " + currentElement.getName() + ", but it's already working.");
        if (currentElement instanceof Pipe) {
            if (!currentElement.isWorking()) {
                currentElement.setWorks(true);
                System.out.println(playerName + " fixed " + currentElement.getName());
            }
        } else
            System.out.println("You need to be standing on a punctured pipe to fix it.");
    }


    /**
     * Method for picking up a pump that was manufactured at a cistern
     * @param g1 is the model.Game instance
     *  Conditions Checked: Currently occupying a model.Cistern.
     *                      Whether the model.Cistern has a manufactured pump ready.
     * @author Ibrahim
     */
    public void getPump(Game g1) {
        // Check if the current element is a Cistern
        if (!(currentElement instanceof Cistern)) {
            System.out.println("You are not on a Cistern, move to a cistern with a manufactured pump to pick it up.");
            return;
        }

        // Check if the cistern has a manufactured pump
        if (((Cistern) currentElement).manufacturedPump == null) {
            System.out.println("This cistern does not have a pump available for pickup.");
            return;
        }

        // Retrieve the manufactured pump from the cistern
        Pump cisternPump = ((Cistern) currentElement).manufacturedPump;

        // Retrieve the selected element from MapGUI
        Element selectedElement = MapGUI.getSelectedElement();

        // Check if the selected element is the pump to be picked up and set its visibility to false
        if (selectedElement instanceof Pump && selectedElement == cisternPump) {
            selectedElement.setVisible(false);
            g1.removePump(cisternPump);
        }

        // Assign the pump to pickedUpPump and add it to the game's pump list
        pickedUpPump = cisternPump;

        // Print confirmation message
        System.out.println(playerName + " picked up " + pickedUpPump.getName() + " from the cistern.");
        g1.pumpPickedUp = true;
    }




    /**
     * Methods that inserts a pump that was obtained from a cistern, into the pipe grid.
     *
     * @param g1 is the game instance.
     * When called, the method will simulate the breaking of a pipe (currently occupied) into 2 new pipes.
     *          initialized with the respective connectivity based on direction through model.EndOfPipe instances.
     * In essence, the pump inserted (pickedUpPump) is inserted in the middle of the old pipe, where two new pipes simulate the 2 broken halves.
     *           Connectivity with the new pump is also handled.
     * Each element is also inserted into their respective element-lists, index accounted for when needed.
     * Condition checks implemented to handle when the circumstance is not applicable.(!Currently occupying a pipe & !Having a pump picked up form a  cistern.)
     * @author Ibrahim
     *
     */
    public void insertPump(Game g1){
        if (pickedUpPump != null && currentElement instanceof Pipe) {
            Pipe pipe = (Pipe) currentElement;
            Pump newPump;
            Pipe newPipe1;
            Pipe newPipe2;
            EndOfPipe newEnd1A;
            EndOfPipe newEnd1B;
            EndOfPipe newEnd2A;
            EndOfPipe newEnd2B;
            int x = pipe.getPosition().x;
            int y = pipe.getPosition().y;
            if (pipe.vertical) {
                int pumpHeight = 50;  // Height of the pump, adjust if different
                int totalHeight = pipe.height;
                int remainingHeight = totalHeight - pumpHeight;
                int newPipeHeight = remainingHeight / 2;  // Split remaining height between two new pipes

                // Reduce newPipeHeight slightly to allow for some space at the connections
                newPipeHeight -= 5;  // Adjust this value as needed to perfect the fit

                // Position the pump to start where the first new pipe ends
                int pumpStartY = y + newPipeHeight;

                newPump = new Pump("newPump" + newPumpCount, new Point(x, pumpStartY), pipe.width, pumpHeight);
                newPumpCount++;

                // Create new pipes above and below the pump
                newPipe1 = new Pipe("newPipe" + newPipecount, new Point(x, y), pipe.vertical, pipe.width, newPipeHeight);
                newPipecount++;
                newPipe2 = new Pipe("newPipe" + newPipecount, new Point(x, pumpStartY + pumpHeight), pipe.vertical, pipe.width, newPipeHeight);
                newPipecount++;
                newEnd1A = new EndOfPipe(newPipe1, true);
                newEnd1B = new EndOfPipe(newPipe1, false);
                newEnd2A  = new EndOfPipe(newPipe2, true);
                newEnd2B  = new EndOfPipe(newPipe2, false,0,35);
            } else {
                int pipeWidth = pipe.width;
                int pumpWidth = 50;
                int pumpHeight = 50;
                int newPipeWidth = (pipeWidth - pumpWidth) / 2;

                newPump = new Pump("newPump" + newPumpCount, new Point(x + newPipeWidth, y - 10), pumpWidth, pumpHeight);
                newPumpCount++;

                newPipe1 = new Pipe("newPipe" + newPipecount, new Point(x, y), pipe.vertical, newPipeWidth, pipe.height);
                newPipecount++;
                newPipe2 = new Pipe("newPipe" + newPipecount, new Point(x + newPipeWidth + pumpWidth, y), pipe.vertical, newPipeWidth, pipe.height);
                newPipecount++;
                newEnd1A = new EndOfPipe(newPipe1, true);
                newEnd1B = new EndOfPipe(newPipe1, false);
                newEnd2A  = new EndOfPipe(newPipe2, true);
                newEnd2B  = new EndOfPipe(newPipe2, false);
            }
                for (EndOfPipe end : pipe.endsOfPipe) {
                    if(end == null)
                    {
                        continue;
                    }
                    if (end.currentPipe == pipe) {
                        g1.endOfPipeList.remove(end);
                    }
                }
                g1.endOfPipeList.add(newEnd1A);
                g1.endOfPipeList.add(newEnd1B);
                g1.endOfPipeList.add(newEnd2A);
                g1.endOfPipeList.add(newEnd2B);



                if(pipe.endsOfPipe[0] != null) {
                    newPipe1.endsOfPipe[0] = pipe.endsOfPipe[0];
                }
                newPipe1.endsOfPipe[0].setCurrentPipe(newPipe1);
                newPipe1.endsOfPipe[1].setCurrentPipe(newPipe1);
                if(pipe.endsOfPipe[1] != null) // in the test case pipe 6 has only one and we need to take that into consideration.
                {
                    newPipe2.endsOfPipe[1]=pipe.endsOfPipe[1];
                }
                newPipe2.endsOfPipe[1].setCurrentPipe(newPipe2);
                newPipe2.endsOfPipe[0].setCurrentPipe(newPipe2);

                newPipe1.leakedAmount=pipe.leakedAmount/2;
                newPipe2.leakedAmount=pipe.leakedAmount/2;

                pipe.setWorks(false);
                int index = g1.pipeList.indexOf(pipe);
                int index2 = g1.elementList.indexOf(pipe);
                /* these are not needed since connectable list order does not effect anything
                int index3 = pipe.endsOfPipe[0].getConnectedElement().connectablePipes.indexOf(pipe);
                int index4= pipe.endsOfPipe[1].getConnectedElement().connectablePipes.indexOf(pipe);
                 */
                g1.elementList.remove(pipe);
                g1.pipeList.remove(pipe);

                g1.pipeList.add(index, newPipe1);
                g1.pipeList.add(index + 1, newPipe2);

                g1.elementList.add(index2,newPipe1);
                g1.elementList.add(index2+1,newPump);
                g1.elementList.add(index2+2,newPipe2);
                if(pipe.endsOfPipe[0] != null) {
                    pipe.endsOfPipe[0].getConnectedElement().connectablePipes.remove(pipe);
                }
                if(pipe.endsOfPipe[1] != null) {
                    pipe.endsOfPipe[1].getConnectedElement().connectablePipes.remove(pipe);
                }
                if(pipe.endsOfPipe[0] != null) {
                    pipe.endsOfPipe[0].getConnectedElement().connectablePipes.add(newPipe1);
                }
                if(pipe.endsOfPipe[1] != null){
                pipe.endsOfPipe[1].getConnectedElement().connectablePipes.add(newPipe2);}

                newPipe1.endsOfPipe[1].connectToElement(newPump);
                newPipe2.endsOfPipe[0].connectToElement(newPump);

                newPump.connectedPipes.add(newPipe1);
                newPump.connectedPipes.add(newPipe2);

                g1.pumpList.add(newPump);

                pickedUpPump=null;
                System.out.println(playerName + " inserted a pump into "+ pipe.getName() + ".");
                g1.removePipe(pipe);

            }
        else if(currentElement instanceof Pipe && pickedUpPump==null){
            System.out.println("You dont have pump picked up to insert here.");
        } else {
            System.out.println("You have to have picked up a pump and be standing on a pipe.");
        }
    }

    /**
     * this method fixes a broken pump.
     * Conditions checked: Currently occupying a broken pump.
     * @author Ibrahim
     */
    public void fixPump(){
        if(currentElement instanceof Pump && currentElement.isWorking()){
            System.out.println(playerName + " attempted to fix " + currentElement.getName() + ", but it's already working.");}
        if (currentElement instanceof Pump) {
            if (!currentElement.isWorking()) {
                currentElement.setWorks(true);
                System.out.println(playerName + " fixed " + currentElement.getName());
            }
        } else {
            System.out.println("You need to be standing on a broken pump to fix it.");}
    }
    }