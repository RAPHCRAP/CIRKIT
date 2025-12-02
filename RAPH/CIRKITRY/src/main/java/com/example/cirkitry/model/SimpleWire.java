package com.example.cirkitry.model;



public class SimpleWire 
{
    public final Pin src;
    public final Pin dst;
    public final Wire original;

    public SimpleWire(Pin src, Pin dst, Wire original) 
    {
        this.src = src;
        this.dst = dst;
        this.original = original;
    }

    public Pin getSource()
    {
        return src;
    }

    public Pin getSink()
    {
        return dst;
    }

    public Wire getOriginalWire()
    {
        return original;
    }
}
