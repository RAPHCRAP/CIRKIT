package com.example.cirkitry.model;

import java.util.ArrayList;
import java.util.List;

public class Wire 
{
    private final Pin source;          // must be an OUTPUT pin
    private final List<Pin> sinks = new ArrayList<>();

    public Wire(Pin source) {
        if (!source.isOutput())
            throw new IllegalArgumentException("Wire source must be an OUTPUT pin");
        this.source = source;
    }

    public void addSink(Pin sink) {
        if (!sink.isInput())
            throw new IllegalArgumentException("Wire sink must be an INPUT pin");
        sinks.add(sink);
        sink.addConnection(this);
    }

    // Called during propagation phase of tick
    public void propagate() {
        boolean s = source.getSignal();
        for (Pin sink : sinks) {
            sink.setNextSignal(s);
        }
    }

    public Pin getSource() { return source; }
    public List<Pin> getSinks() { return sinks; }
}
