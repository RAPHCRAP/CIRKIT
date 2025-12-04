package com.example.cirkitry.model;
import java.util.ArrayList;
import java.util.List;

import com.example.cirkitry.wmodel.SelectableView;

import javafx.geometry.Point2D;

public abstract class Component {

    protected String name;
    protected String type;

     // Physical layout
    protected int width;
    protected int height;
    protected int x;  // Top-left position in grid
    protected int y;

    // Logical structure
    protected final List<Pin> inputPins = new ArrayList<>();
    protected final List<Pin> outputPins = new ArrayList<>();
    protected final List<Component> subcomponents = new ArrayList<>();

    // Physical layout (optional for your grid UI)
    protected final List<Cell> occupiedCells = new ArrayList<>();


    private SelectableView viewGroup;

    public Component(String name) {
        this.name = name;
        this.type = "Abstract{Component}";
    }

    // ------------------------------
    // Pin Management
    // ------------------------------

    public Pin addInputPin(String name) {
        Pin pin = new Pin(PinType.INPUT,this);
        inputPins.add(pin);
        return pin;
    }

    public Pin addOutputPin(String name) {
        Pin pin = new Pin(PinType.OUTPUT,this);
        outputPins.add(pin);
        return pin;
    }

    public List<Pin> getInputPins() {
        return inputPins;
    }

    public List<Pin> getOutputPins() {
        return outputPins;
    }

    // ------------------------------
    // Subcomponent Management (for composites)
    // ------------------------------

    public void addSubcomponent(Component comp) {
        subcomponents.add(comp);
    }

    public List<Component> getSubcomponents() {
        return subcomponents;
    }

    // ------------------------------
    // Layout / Grid
    // ------------------------------

    private void occupyCell(Cell cell) {
        occupiedCells.add(cell);
    }

    public List<Cell> getOccupiedCells() {
        return occupiedCells;
    }

public void commitPins() {
    // First commit subcomponents recursively
    for (Component sub : subcomponents) sub.commitPins();

    // Now commit *all* pins owned by this component:
    for (Pin p : inputPins)  p.updateSignal();
    for (Pin p : outputPins) p.updateSignal();
}

       

    public abstract void compute();

    // ------------------------------
    // Name
    // ------------------------------

    public String getName() {
        
        return name;
    }

    public String getType()
    {
        return type;
    }

    public int getHeight() {
        applyPreferredSize();
        return height;
    }


    public int getWidth() {
        applyPreferredSize();
        return width;
    }
    
    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }


    // ------------------------------
    // Simulation Hook
    // ------------------------------

    /**
     * Every component must implement its logic here.
     * For a primitive gate → do the boolean operation.
     * For a composite component → call compute() on its subcomponents in order.
     */
// ----------------------------------------------------------------------
    // SIZE & LAYOUT
    // ----------------------------------------------------------------------

    protected Point2D computePreferredSize() {
        int h = Math.max(Math.max(inputPins.size(), outputPins.size()),this.height);
        int w = Math.max(3,this.width);   // default gate width
        return new Point2D(w, h);
    }

    protected void applyPreferredSize() {
        Point2D d = computePreferredSize();
        this.width = (int)d.getX();
        this.height = (int)d.getY();
    }

    protected void layoutPins() {
        // Centered vertical layout
        for (int i = 0; i < inputPins.size(); i++) {
            int ry = (height * (i + 1)) / (inputPins.size() + 1);
            inputPins.get(i).setRelative(0, ry);
        }

        for (int i = 0; i < outputPins.size(); i++) {
            int ry = (height * (i + 1)) / (outputPins.size() + 1);
            outputPins.get(i).setRelative(width - 1, ry);
        }
    }

    // ----------------------------------------------------------------------
    // GRID PLACEMENT
    // ----------------------------------------------------------------------

    public boolean canPlace(int gridX, int gridY, Circuit circuit) {
        for (int cx = gridX; cx < gridX + width; cx++) {
            for (int cy = gridY; cy < gridY + height; cy++) {
                if (!circuit.getCell(cx, cy).canPlaceComponent()) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean canMoveTo(int gridX,int gridY,Circuit circuit)
    {
         for (int cx = gridX; cx < gridX + width; cx++) {
            for (int cy = gridY; cy < gridY + height; cy++) {
                if (!circuit.getCell(cx, cy).canMoveComponent(this)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean moveTo(int gridX,int gridY,Circuit circuit)
    {
        if(!circuit.detachComponent(this)) return false;
        
 
        this.x = gridX;
        this.y = gridY;
        
        circuit.addComponent(gridX, gridY, this);


        return true;
    }

    private void updateOccupiedCells(Circuit circuit) {
        for (int cx = x; cx < x + width; cx++) {
            for (int cy = y; cy < y + height; cy++) {
                circuit.getCell(cx, cy).setComponent(this);
            }
        }
    }

    private void updatePinCells(Circuit circuit) {
        for (Pin p : inputPins) {
            
            circuit.getCell(p.getAbsoluteX(), p.getAbsoluteY()).setPin(p);
        }
        for (Pin p : outputPins) {
            circuit.getCell(p.getAbsoluteX(), p.getAbsoluteY()).setPin(p);
        }
    }

    // ----------------------------------------------------------------------
    // MAIN ENTRYPOINT
    // ----------------------------------------------------------------------

    public boolean placeInCircuit(int gridX, int gridY, Circuit circuit) 
    {
        applyPreferredSize();  // compute correct size

        if(!pinConstraint()) return false;
        if (!canPlace(gridX, gridY, circuit)) return false;

        this.x = gridX;
        this.y = gridY;

        layoutPins();
        updateOccupiedCells(circuit);
        updatePinCells(circuit);

        return true;
    }

    private boolean pinConstraint() {
    int compX = this.x;        // component top-left grid X
    int compY = this.y;        // component top-left grid Y
    int w = this.width;
    int h = this.height;

    // Check all input pins
    for (Pin p : inputPins) {
        int px = p.getAbsoluteX();
        int py = p.getAbsoluteY();

        // Check if pin lies within component bounds
        if (px < compX || px >= compX + w ||
            py < compY || py >= compY + h) {
            return false;  // OUT OF BOUNDS → fail
        }
    }

    // Check all output pins
    for (Pin p : outputPins) {
        int px = p.getAbsoluteX();
        int py = p.getAbsoluteY();

        if (px < compX || px >= compX + w ||
            py < compY || py >= compY + h) {
            return false;
        }
    }


    return true; // All checks passed
}


 

    public void setView(SelectableView sv)
    {
        
        this.viewGroup = sv;
        
    }

    public void rebuild()
    {
        
        if(viewGroup!=null)
        {
            viewGroup.rebuild();
        }
    }

    protected void stateUpdate()
    {
        
        if(viewGroup!=null)
        {
            viewGroup.update();
        }
    }

    public SelectableView getView()
    {
      
        return viewGroup;
    }

    public void removeView()
    {
        viewGroup.removeFromSubSceneRoot();
    }
}