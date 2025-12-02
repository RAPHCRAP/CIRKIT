package com.example.cirkitry;

import java.util.function.Consumer;

import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
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

    public VBox getComponentSidebar() {
    return componentSidebar;
}


private Consumer<Mode> modeChangeListener;

public void setOnModeChanged(Consumer<Mode> listener) {
    this.modeChangeListener = listener;
}

    // ----------------------------------------------------------
    // PUBLIC UPDATE METHODS
    // ----------------------------------------------------------
    public void updatePositionLabel(int x, int y) {
        cameraPosLabel.setText("(" + x + ", " + y + ")");
    }

    public void updateMessage(String msg) {
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
    // TOP BAR
    // ----------------------------------------------------------
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
        toggleRunBtn.setOnAction(e -> toggleRunPanel());

        HBox topBar = new HBox(20, menuBar, modeSwitchBtn, toggleSidebarBtn, toggleRunBtn);
        topBar.setPadding(new Insets(5));
        topBar.setStyle("-fx-background-color: #222;");
        topBar.setMouseTransparent(false);

        return topBar;
    }

    private MenuBar createMenuBar() {

        Menu file = new Menu("File");
        file.getItems().addAll(
            styledMenuItem("Open"),
            styledMenuItem("Save"),
            styledMenuItem("Save As"),
            styledMenuItem("Exit")
        );

        Menu tools = new Menu("Tools");
        tools.getItems().addAll(
            styledMenuItem("Export Image")
        );

        MenuBar bar = new MenuBar(file, tools);
        bar.setStyle("-fx-background-color: #222;");

        return bar;
    }

    private MenuItem styledMenuItem(String name) {
        MenuItem item = new MenuItem(name);
        item.setStyle("-fx-text-fill: white;");
        return item;
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
