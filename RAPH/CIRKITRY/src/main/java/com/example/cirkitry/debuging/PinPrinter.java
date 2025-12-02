package com.example.cirkitry.debuging;

import com.example.cirkitry.model.Pin;
import com.example.cirkitry.model.Wire;

public class PinPrinter {

    // Prints a single pin with connected wires
    public static void printPin(Pin pin) {
        printPin(pin, 0);
    }

    protected static void printPin(Pin pin, int indent) {
        String ind = "  ".repeat(indent);
        System.out.println(ind + "Pin [" + (pin.isInput() ? "INPUT" : "OUTPUT") + "] of " + pin.getParent().getName());
        System.out.println(ind + "  Relative pos: (" + pin.getRelativeX() + "," + pin.getRelativeY() + ")");
        System.out.println(ind + "  Absolute pos: (" + pin.getAbsoluteX() + "," + pin.getAbsoluteY() + ")");
        System.out.println(ind + "  Signal: " + pin.getSignal());

        // Connections
        if (!pin.getConnections().isEmpty()) {
            System.out.println(ind + "  Connected wires:");
            for (Wire w : pin.getConnections()) {
                System.out.println(ind + "    - Wire@" + System.identityHashCode(w) +
                                   " | Source: " + w.getSource().getParent().getName() +
                                   ", Sinks: " + w.getSinks().stream().map(s -> s.getParent().getName()).toList());
            }
        } else {
            System.out.println(ind + "  No connected wires.");
        }
    }
}
