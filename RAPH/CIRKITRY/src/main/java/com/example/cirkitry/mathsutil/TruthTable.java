package com.example.cirkitry.mathsutil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class TruthTable {

    public static class Row {
        public final LinkedHashMap<String, Boolean> inputs;
        public final LinkedHashMap<String, Boolean> outputs;

        public Row(LinkedHashMap<String, Boolean> inputs, LinkedHashMap<String, Boolean> outputs) {
            this.inputs = inputs;
            this.outputs = outputs;
        }

        @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // Inputs
        for (Boolean val : inputs.values()) {
            sb.append(val ? "1" : "0").append("\t");
        }

        sb.append("| "); // separator

        // Outputs
        for (Boolean val : outputs.values()) {
            sb.append(val ? "1" : "0").append("\t");
        }

        return sb.toString();
    }

    }

    private final List<String> inputNames;
    private final List<String> outputNames;
    private final List<Row> rows;

    public TruthTable(List<String> inputNames, List<String> outputNames) {
        this.inputNames = inputNames;
        this.outputNames = outputNames;
        this.rows = new ArrayList<>();
    }

    public void addRow(boolean[] inVals, boolean[] outVals) {
        LinkedHashMap<String, Boolean> inMap = new LinkedHashMap<>();
        LinkedHashMap<String, Boolean> outMap = new LinkedHashMap<>();

        for (int i = 0; i < inputNames.size(); i++) inMap.put(inputNames.get(i), inVals[i]);
        for (int i = 0; i < outputNames.size(); i++) outMap.put(outputNames.get(i), outVals[i]);

        rows.add(new Row(inMap, outMap));
    }

    public void display() {
    // Print header
    for (String name : inputNames) {
        System.out.print(name + "\t");
    }
    for (String name : outputNames) {
        System.out.print("| " + name + "\t");
    }
    System.out.println();

    // Print a separator line
    for (int i = 0; i < inputNames.size(); i++) System.out.print("--------");
    for (int i = 0; i < outputNames.size(); i++) System.out.print("---------");
    System.out.println();

    // Print each row
    for (Row row : rows) {
        for (String in : inputNames) {
            System.out.print((row.inputs.get(in) ? 1 : 0) + "\t");
        }
        for (String out : outputNames) {
            System.out.print("| " + (row.outputs.get(out) ? 1 : 0) + "\t");
        }
        System.out.println();
    }
}


    public List<Row> getRows() { return rows; }
    public List<String> getInputNames() { return inputNames; }
    public List<String> getOutputNames() { return outputNames; }
}
