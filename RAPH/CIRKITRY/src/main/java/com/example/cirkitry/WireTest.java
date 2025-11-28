package com.example.cirkitry;

import com.example.cirkitry.model.Circuit;
import com.example.cirkitry.model.Wire;
import com.example.cirkitry.model.WireNode;
import com.example.cirkitry.model.primitivegates.AndGate;
import com.example.cirkitry.model.primitives.Switch;

public class WireTest {

    public static void test(String[] args) {
        // ---------------------------
        // 1. Initialize Circuit
        // ---------------------------
        Circuit circuit = new Circuit(100, 100); // 10x10 grid

        // ---------------------------
        // 2. Add 2 Switches
        // ---------------------------
        Switch sw1 = new Switch();
        Switch sw2 = new Switch();

        // Place switches on circuit
        if(circuit.addComponent(1,1,sw1)) System.out.print("Placed switch 1\n"); // example positions
        if(circuit.addComponent(1,4,sw2)) System.out.print("Placed switch 2\n"); 

        Wire w1 = new Wire(sw1.getOut().getAbsoluteX(), sw1.getOut().getAbsoluteY(), sw1.getOut());
        Wire w2 = new Wire(sw2.getOut().getAbsoluteX(), sw2.getOut().getAbsoluteY(), sw2.getOut());


       if(circuit.addWire(w1)) System.out.print("added wire 1\n");
       if(circuit.addWire(w2)) System.out.print("added wire 2\n");

       displayWireNodes(w1);

       WireNode n =circuit.getCell(3, 2).getNode();
       Wire w = n.getWire();
  displayWireNodes(w);

       if(w.extendEdge(n, 6,-6 , circuit)) System.out.print("extended wire 1\n"); 

       displayWireNodes(w);

       AndGate andGate = new AndGate();
       if(circuit.addComponent(-6,-6,andGate))  System.out.print("ok\n"); 

        System.err.println("and x,y(" + andGate.getInA().getAbsoluteX() + "," + andGate.getInA().getAbsoluteY() + ")");

    


          if(circuit.getCell(-6, -6).getComponent()!=null) System.out.print("has component\n");    
         WireNode n1 =circuit.getCell(6, -6).getNode(); 
         System.err.println(n1);
             displayWireNodes(w);
          if(w.deleteNode(n1,circuit)) System.out.print("ok\n"); 
    displayWireNodes(w);

      n1 = circuit.getCell(6, 2).getNode();
      w.extendEdge(n1, 6, -1, circuit);
      w.extendEdge(n1, 6, 3, circuit);
      w.extendEdge(n1, 7, -1, circuit);

      displayWireNodes(w);

      n1 = circuit.getCell(7, -1).getNode(); 
      System.err.println(w.deleteNode(n1, circuit));
        // ---------------------------
        // 3. Create wires from switches
        // ---------------------------
        // Wire wire1 = new Wire(1, 1, sw1.getOut());
        // Wire wire2 = new Wire(1, 3, sw2.getOut());

        // // Place wires at source (first node)
        // if (!wire1.placeInCircuit(circuit)) System.out.println("Failed to place wire1");
        // if (!wire2.placeInCircuit(circuit)) System.out.println("Failed to place wire2");

        // // ---------------------------
        // // 4. Add AND gate at (0,0)
        // // ---------------------------
        // AndGate andGate = new AndGate();
        // circuit.addComponent(andGate, 0, 0);

        // // ---------------------------
        // // 5. Extend wires to AND gate inputs
        // // ---------------------------
        // Pin inA = andGate.getInA();
        // Pin inB = andGate.getInB();

        // // Extend wire1 to AND gate input A
        // WireNode lastNode1 = wire1.getOccupiedCells()
        //                          .get(wire1.getOccupiedCells().size() - 1)
        //                          .getNode();
        // if (lastNode1 != null) {
        //     if (!wire1.extendEdge(lastNode1, 0, 0, circuit))
        //         System.out.println("Failed to extend wire1 to AND gate input A");
        // }

        // // Extend wire2 to AND gate input B
        // WireNode lastNode2 = wire2.getOccupiedCells()
        //                          .get(wire2.getOccupiedCells().size() - 1)
        //                          .getNode();
        // if (lastNode2 != null) {
        //     if (!wire2.extendEdge(lastNode2, 0, 1, circuit))
        //         System.out.println("Failed to extend wire2 to AND gate input B");
        // }

        // // ---------------------------
        // // 6. Print wire nodes & occupied cells
        // // ---------------------------
        // System.out.println("Wire1 nodes:");
        // for (WireNode n : wire1.getWireNodes()) {
        //     System.out.println("(" + n.getX() + "," + n.getY() + ")");
        // }

        // System.out.println("Wire2 nodes:");
        // for (WireNode n : wire2.getWireNodes()) {
        //     System.out.println("(" + n.getX() + "," + n.getY() + ")");
        // }

        // System.out.println("Wire1 occupied cells:");
        // for (Cell c : wire1.getOccupiedCells()) {
        //     System.out.println("(" + c.getX() + "," + c.getY() + ")");
        // }

        // System.out.println("Wire2 occupied cells:");
        // for (Cell c : wire2.getOccupiedCells()) {
        //     System.out.println("(" + c.getX() + "," + c.getY() + ")");
        // }
    }

     public static void displayWireNodes(Wire wire) {
        if (wire == null) {
            System.out.println("Wire is null");
            return;
        }

        System.out.println("Wire nodes:");
        int index = 0;
if(wire.getNodes().isEmpty()) System.out.println("Empty\n");

        for (WireNode node : wire.getNodes()) {
            System.out.printf("Node %d -> x: %d, y: %d%n", index, node.getX(), node.getY());
            index++;
        }
    }
}
