package com.palsandpalms.ui.screens;

import com.palsandpalms.ui.AssetLoader;
import com.palsandpalms.ui.GameAssets;
import com.palsandpalms.ui.GameViewport;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public final class TutorialView {

    private TutorialView() {
    }

    public static StackPane create(Runnable onContinue) {
        StackPane root = new StackPane();
        root.setMinSize(GameViewport.MIN_WIDTH, GameViewport.MIN_HEIGHT);

        ImageView bg = new ImageView(AssetLoader.loadImageNatural(GameAssets.MAIN_MENU_BACKGROUND));
        bg.fitWidthProperty().bind(root.widthProperty());
        bg.fitHeightProperty().bind(root.heightProperty());
        bg.setPreserveRatio(false);
        root.getChildren().add(bg);

        VBox content = new VBox(18);
        content.setAlignment(Pos.TOP_LEFT);
        content.setPadding(new Insets(36, 40, 36, 40));
        content.setMaxWidth(600);

        content.getChildren().addAll(
            heading("Ziel des Spiels"),
            body("Deine Bewohnerinnen sollen glücklich gemeinsam auf der Insel leben!"),

            body("Nina und Victoria sind bereits eingelebte Insulanerinnen, aber kennen einander noch gar nicht! (Obwohl sie sogar unter einem Dach zusammen leben...)"),

            body("Es liegt jetzt an dir, sie zusammenzubringen und sie zu Freundinnen werden zu lassen!\nZiehe dazu einfach eine von den beiden in die andere rein per Drag-and-Drop – so können sie eine Konversation starten :)"),

            heading("Statuswerte"),
            body("Jede/r BewohnerIn hat Statuswerte, die hochgehalten werden sollen:\n\n• Hunger wird aufgefüllt, indem man die Person an die Küchentheke droppt.\n• Schlaf erholt sich beim Sofa.\n• Hygiene wird im Bad wiederhergestellt.\n\nAchtung: Es kann sich immer nur eine Person gleichzeitig im Bad aufhalten!\n\nDie Stimmung verbessert sich, wenn sich die InselbewohnerInnen miteinander unterhalten. Und für all das musst du sorgen! Fallen Schlaf, Hunger oder Hygiene zu weit, werden die Personen traurig und müssen dolle weinen :'("),

            body("Die Statuswerte kannst du einsehen, indem du die Personen anklickst."),

            heading("InGame-Menü"),
            body("Mit der ESC-Taste kommt man jederzeit ins InGame-Menü. Dort kann man bis zu zwei weitere BewohnerInnen erstellen und benennen, zurück zum Hauptmenü gehen oder das Spiel beenden.\n\n(Der Button zum Speichern des Spielstands ist fake \u2013 er funktioniert nicht. Man muss immer wieder ein neues Spiel starten, sorry. (Prank, er funktioniert doch. Hab\u2019s gefixt))"),

            heading("Sonstige Hinweise"),
            body("In diesem Spiel gelten die Gesetze der Schwerkraft – was sehr lustig ist, wenn man die BewohnerInnen nimmt und wirft. Außerdem gibt es Tageszeiten und sich bewegende Wolken. Nur ein kurzer Side Fact, weil ja, ist 'ne Erwähnung wert."),

            italic("Das wär's von unserer Seite – viel Spaß, Frau Nerz! Abi, vielleicht finden Sie ja ein paar Easter Eggs :)"),
            italic("~ die Entwicklerinnen Nina und Vicy!")
        );

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scroll.setMaxWidth(620);
        scroll.maxHeightProperty().bind(root.heightProperty().multiply(0.82));
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        VBox wrapper = new VBox(scroll);
        wrapper.setAlignment(Pos.CENTER);
        wrapper.setMaxWidth(620);
        wrapper.setStyle("-fx-background-color: rgba(0,0,0,0.65); -fx-background-radius: 12;");

        Button next = new Button("Los geht's!");
        next.setOnAction(e -> onContinue.run());
        StackPane.setAlignment(next, Pos.BOTTOM_CENTER);
        StackPane.setMargin(next, new Insets(0, 0, 24, 0));

        root.getChildren().addAll(wrapper, next);
        return root;
    }

    private static Label heading(String text) {
        Label l = new Label(text);
        l.setTextFill(Color.WHITE);
        l.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        l.setWrapText(true);
        l.setMaxWidth(Double.MAX_VALUE);
        return l;
    }

    private static Label body(String text) {
        Label l = new Label(text);
        l.setTextFill(Color.LIGHTGRAY);
        l.setStyle("-fx-font-size: 13px;");
        l.setWrapText(true);
        l.setMaxWidth(Double.MAX_VALUE);
        return l;
    }

    private static Label italic(String text) {
        Label l = new Label(text);
        l.setTextFill(Color.color(0.75, 0.75, 0.75));
        l.setStyle("-fx-font-size: 13px; -fx-font-style: italic;");
        l.setWrapText(true);
        l.setMaxWidth(Double.MAX_VALUE);
        return l;
    }
}
