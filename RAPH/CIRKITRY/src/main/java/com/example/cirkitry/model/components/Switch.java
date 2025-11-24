package  com.example.cirkitry.model.components;

import com.example.cirkitry.model.*;


import java.util.ArrayList;
import java.util.List;

class Switch extends Component {

    private boolean toggled = false;

    public Switch(List<Cell> occupied) {
        this.occupiedCells = occupied;

        this.inputPins = new ArrayList<>();  // no inputs
        this.outputPins = new ArrayList<>();

        Pin out = new Pin(PinType.OUTPUT, this);
        outputPins.add(out);

        // place the pin on the rightmost cell for example
        Cell pinCell = occupied.get(occupied.size() - 1);
        pinCell.setPin(out);
    }

    @Override
    public void computeOutput() {
        // switch simply outputs its toggled state
        outputPins.get(0).setNextSignal(toggled);
    }

    public void toggle() {
        toggled = !toggled;
    }
}
