package  com.example.cirkitry.model.components;

import com.example.cirkitry.model.*;


import java.util.ArrayList;
import java.util.List;

public class Bulb extends Component {

    private boolean isOn;

    public Bulb(List<Cell> occupiedCells) {
        this.occupiedCells = occupiedCells;

        this.inputPins = new ArrayList<>();
        this.outputPins = new ArrayList<>();

        // The bulb has exactly one input pin
        Pin input = new Pin(PinType.INPUT, this);
        this.inputPins.add(input);

        // You must map where this pin physically sits.
        // For example, the pin is located at the leftmost cell:
        Cell pinCell = occupiedCells.get(0);
        pinCell.setPin(input);
    }

    @Override
    public void computeOutput() {
        // Bulb just reads the input pin and updates its visual state
        boolean signal = inputPins.get(0).getSignal();
        isOn = signal;

        // NO output pins, so nothing else happens
    }

    public boolean isOn() {
        return isOn;
    }
}