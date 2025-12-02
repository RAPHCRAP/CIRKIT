package com.example.cirkitry.debuging;

import com.example.cirkitry.model.Component;
import com.example.cirkitry.model.Pin;

public class ComponentPrinter {

    // Prints everything about a component recursively
    public static void printComponent(Component c) {
        printComponent(c, 0);
    }

    private static void printComponent(Component c, int indent) {
        String ind = "  ".repeat(indent);
        System.out.println(ind + "Component: " + c.getName() + " [" + c.getType() + "]");
        System.out.println(ind + "  Position: (" + c.getX() + "," + c.getY() + "), Size: " + c.getWidth() + "x" + c.getHeight());
        System.out.println(ind + "  Occupied cells: " + c.getOccupiedCells().size());

        // Input pins
        System.out.println(ind + "  Input pins:");
        for (Pin pin : c.getInputPins()) {
            System.out.print(ind + "    - ");
            PinPrinter.printPin(pin, indent + 3);
        }

        // Output pins
        System.out.println(ind + "  Output pins:");
        for (Pin pin : c.getOutputPins()) {
            System.out.print(ind + "    - ");
            PinPrinter.printPin(pin, indent + 3);
        }

        // Subcomponents
        if (!c.getSubcomponents().isEmpty()) {
            System.out.println(ind + "  Subcomponents:");
            for (Component sub : c.getSubcomponents()) {
                printComponent(sub, indent + 2);
            }
        }
    }
}
