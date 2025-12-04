package com.example.cirkitry.handler;

import java.util.List;
import java.util.function.Consumer;

import com.example.cirkitry.mathsutil.TruthTable;

import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;


public class GUIOverlay {

    private BorderPane root;

    private MenuBar menuBar;

    // FOOTER LABELS
    private Label cameraPosLabel;    // right side bottom
    private Label messageLabel;      // left side bottom

    private VBox componentSidebar;
    private VBox runPanel;

    private boolean sidebarVisible = false;
    private boolean runPanelVisible = false;

    private Button toggleSidebarBtn;
    private Button toggleRunBtn;
    private Button modeSwitchBtn;



    public enum Mode { EDIT, RUN }
    private Mode currentMode = Mode.EDIT;


    private Runnable onNewRequested;
private Runnable onOpenRequested;
private Runnable onSaveRequested;
private Runnable onSaveAsRequested;
private Runnable onExitRequested;

public void setOnNewRequested(Runnable r) { onNewRequested = r; }
public void setOnOpenRequested(Runnable r) { onOpenRequested = r; }
public void setOnSaveRequested(Runnable r) { onSaveRequested = r; }
public void setOnSaveAsRequested(Runnable r) { onSaveAsRequested = r; }
public void setOnExitRequested(Runnable r) { onExitRequested = r; }


private Consumer<List<TruthTable.Row>> onTruthTableRequested;

public void setOnTruthTableRequested(Consumer<List<TruthTable.Row>> c) {
    this.onTruthTableRequested = c;
}


    public GUIOverlay() {
        createOverlay();
    }

    // ----------------------------------------------------------
    // GETTERS FOR CONTROLLER
    // ----------------------------------------------------------
    public BorderPane getRoot() { return root; }
    public MenuBar getMenuBar() { return menuBar; }

    public Button getModeSwitchButton() { return modeSwitchBtn; }
    public Button getToggleSidebarButton() { return toggleSidebarBtn; }
    public Button getToggleRunButton() { return toggleRunBtn; }

    public Label getPosLabel() {return cameraPosLabel; }

    public VBox getComponentSidebar() {
    return componentSidebar;
}


public void updateTruthTable(TruthTable table) {

    runPanel.getChildren().clear();

    // Title
    Label title = new Label("Truth Table");
    title.setTextFill(Color.WHITE);
    title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
    runPanel.getChildren().add(title);

    List<TruthTable.Row> rows = table.getRows();
    if (rows.isEmpty()) {
        Label empty = new Label("No outputs");
        empty.setTextFill(Color.WHITE);
        runPanel.getChildren().add(empty);
        return;
    }

    // Grid
    GridPane grid = new GridPane();
    grid.setHgap(10);
    grid.setVgap(5);

    // HEADER
    int colIndex = 0;
    for (String inputName : table.getInputNames()) {
        grid.add(makeCell(inputName, true), colIndex++, 0);
    }
    for (String outputName : table.getOutputNames()) {
        grid.add(makeCell(outputName, true), colIndex++, 0);
    }

    // DATA ROWS
    for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
        TruthTable.Row row = rows.get(rowIndex);
        colIndex = 0;

        // Inputs
        for (String inputName : table.getInputNames()) {
            boolean val = row.inputs.get(inputName);
            grid.add(makeCell(val ? "1" : "0", false), colIndex++, rowIndex + 1);
        }

        // Outputs
        for (String outputName : table.getOutputNames()) {
            boolean val = row.outputs.get(outputName);
            grid.add(makeCell(val ? "1" : "0", false), colIndex++, rowIndex + 1);
        }
    }

    // Wrap in ScrollPane (to allow horizontal scrolling for many columns)
    ScrollPane scrollPane = new ScrollPane(grid);
    scrollPane.setFitToHeight(true);
    scrollPane.setFitToWidth(false); // allow horizontal scroll
    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
    scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

    // Keep exact style
    scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

    // Optional: adjust size to your runPanel
    scrollPane.setPrefWidth(runPanel.getPrefWidth() - 20); // leave small margin
    scrollPane.setPrefHeight(300);

    runPanel.getChildren().add(scrollPane);
}

private Label makeCell(String text, boolean header) {
    Label l = new Label(text);
    l.setTextFill(Color.WHITE);
    l.setStyle(header
        ? "-fx-font-weight: bold; -fx-border-color: #777; -fx-padding: 4;"
        : "-fx-border-color: #555; -fx-padding: 4;"
    );
    return l;
}



// Inside GUIOverlay
private Runnable onExportImageRequested;

public void setOnExportImageRequested(Runnable r) {
    this.onExportImageRequested = r;
}



  // ----------------------------------------------------------
    // TOP BAR
    // ----------------------------------------------------------
   



    private MenuBar createMenuBar() {

       MenuItem newItem  = styledMenuItem("New");
    MenuItem openItem = styledMenuItem("Open");
    MenuItem saveItem = styledMenuItem("Save");
    MenuItem saveAsItem = styledMenuItem("Save As");
    MenuItem exitItem = styledMenuItem("Exit");

    newItem.setOnAction(e -> { if (onNewRequested != null) onNewRequested.run(); });
    openItem.setOnAction(e -> { if (onOpenRequested != null) onOpenRequested.run(); });
    saveItem.setOnAction(e -> { if (onSaveRequested != null) onSaveRequested.run(); });
    saveAsItem.setOnAction(e -> { if (onSaveAsRequested != null) onSaveAsRequested.run(); });
    exitItem.setOnAction(e -> { if (onExitRequested != null) onExitRequested.run(); });

    Menu file = new Menu("File", null,
        newItem, openItem, saveItem, saveAsItem, exitItem
    );

   Menu tools = new Menu("Tools");
MenuItem exportItem = styledMenuItem("Export Image");
exportItem.setOnAction(e -> {
    if (onExportImageRequested != null) {
        onExportImageRequested.run();
    }
});
tools.getItems().add(exportItem);

    MenuBar bar = new MenuBar(file, tools);
    bar.setStyle("-fx-background-color: #222;");
    return bar;
    }

    private MenuItem styledMenuItem(String name) {
        MenuItem item = new MenuItem(name);
        item.setStyle("-fx-text-fill: white;");
        return item;
    }



private Consumer<Mode> modeChangeListener;

 private HBox createTopBar() {

        menuBar = createMenuBar();

        modeSwitchBtn = new Button("Mode: EDIT");
        styleButton(modeSwitchBtn);
        modeSwitchBtn.setOnAction(e -> toggleMode());

        toggleSidebarBtn = new Button("Components");
        styleButton(toggleSidebarBtn);
        toggleSidebarBtn.setOnAction(e -> toggleSidebar());

        toggleRunBtn = new Button("Truth Table");
        styleButton(toggleRunBtn);
        toggleRunBtn.setDisable(true);
        toggleRunBtn.setOnAction(e -> handleTable());

        HBox topBar = new HBox(20, menuBar, modeSwitchBtn, toggleSidebarBtn, toggleRunBtn);
        topBar.setPadding(new Insets(5));
        topBar.setStyle("-fx-background-color: #222;");
        topBar.setMouseTransparent(false);

        return topBar;
    }

public void setOnModeChanged(Consumer<Mode> listener) {
    this.modeChangeListener = listener;
}

    // ----------------------------------------------------------
    // PUBLIC UPDATE METHODS
    // ----------------------------------------------------------
    public void updatePositionLabel(int x, int y) {
        cameraPosLabel.setText("(" + x + ", " + y + ")");
    }

    public void showMessage(String msg) {
        messageLabel.setText(msg);

        // Clear after short duration (2 seconds)
        PauseTransition pt = new PauseTransition(Duration.seconds(2));
        pt.setOnFinished(e -> messageLabel.setText(""));
        pt.play();
    }

    // ----------------------------------------------------------
    // ROOT CREATION
    // ----------------------------------------------------------
    private void createOverlay() {

        root = new BorderPane();
        root.setPickOnBounds(false);

        root.setTop(createTopBar());
        createComponentSidebar();
        createRunPanel();

        componentSidebar.setTranslateX(-240);
        componentSidebar.setMouseTransparent(true);

        runPanel.setTranslateX(240);
        runPanel.setMouseTransparent(true);

        root.setLeft(componentSidebar);
        root.setRight(runPanel);

        // NEW FOOTER BAR
        root.setBottom(createFooterBar());
    }

  
    // ----------------------------------------------------------
    // FOOTER BAR
    // ----------------------------------------------------------
    private HBox createFooterBar() {

        messageLabel = new Label("");
        messageLabel.setTextFill(Color.WHITE);

        cameraPosLabel = new Label("(0,0)");
        cameraPosLabel.setTextFill(Color.WHITE);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox bar = new HBox(10, messageLabel, spacer, cameraPosLabel);
        bar.setPadding(new Insets(5));
        bar.setStyle("-fx-background-color: #222;");

        return bar;
    }

    // ----------------------------------------------------------
    // COMPONENT SIDEBAR
    // ----------------------------------------------------------
    private void createComponentSidebar() {

       Label title = new Label("Components");
    title.setTextFill(Color.WHITE);
    title.setStyle("-fx-font-size: 16px;");

    componentSidebar = new VBox(15, title);
    componentSidebar.setPadding(new Insets(10));
    componentSidebar.setPrefWidth(200);
    componentSidebar.setStyle("-fx-background-color: #333;");
    }

    // ----------------------------------------------------------
    // RUN PANEL
    // ----------------------------------------------------------
    private void createRunPanel() {

        Label title = new Label("Truth Table");
        title.setTextFill(Color.WHITE);

        Label demo = new Label("Output: ?");
        demo.setTextFill(Color.WHITE);

        runPanel = new VBox(10, title, demo);
        runPanel.setPadding(new Insets(10));
        runPanel.setPrefWidth(200);
        runPanel.setStyle("-fx-background-color: #333;");
    }

    // ----------------------------------------------------------
    // MODE SWITCH
    // ----------------------------------------------------------
    private void toggleMode() {
    if (currentMode == Mode.EDIT) {

        currentMode = Mode.RUN;
        modeSwitchBtn.setText("Mode: RUN");

        toggleRunBtn.setDisable(false);
        toggleSidebarBtn.setDisable(true);

        if (sidebarVisible) toggleSidebar();

    } else {

        currentMode = Mode.EDIT;
        modeSwitchBtn.setText("Mode: EDIT");

        toggleRunBtn.setDisable(true);
        toggleSidebarBtn.setDisable(false);

        if (runPanelVisible) toggleRunPanel();
    }

    //  Notify controller
    if (modeChangeListener != null)
        modeChangeListener.accept(currentMode);
}


    // ----------------------------------------------------------
    // SIDEBAR TOGGLE
    // ----------------------------------------------------------
    private void toggleSidebar() {
        sidebarVisible = !sidebarVisible;

        TranslateTransition tt = new TranslateTransition(Duration.millis(250), componentSidebar);
        tt.setToX(sidebarVisible ? 0 : -240);
        tt.play();

        componentSidebar.setMouseTransparent(!sidebarVisible);
    }

    private void handleTable() {
    toggleRunPanel();

    if (onTruthTableRequested != null) {
        
        onTruthTableRequested.accept(null); 
    }
}



    // ----------------------------------------------------------
    // RUN PANEL TOGGLE
    // ----------------------------------------------------------
    private void toggleRunPanel() {
        runPanelVisible = !runPanelVisible;

        TranslateTransition tt = new TranslateTransition(Duration.millis(250), runPanel);
        tt.setToX(runPanelVisible ? 0 : 240);
        tt.play();

        runPanel.setMouseTransparent(!runPanelVisible);
    }

    // ----------------------------------------------------------
    // BUTTON STYLE
    // ----------------------------------------------------------
    private void styleButton(Button b) {
        b.setStyle(
            "-fx-background-color: #444;" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 4;" +
            "-fx-padding: 6 10;"
        );
        b.setMouseTransparent(false);
    }
}
