import javafx.animation.FadeTransition;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import static java.awt.Color.white;

public class StylingClass {

    private final int WIDTH = 75;
    private final int HEIGHT = 40;


    public StylingClass() {

    }


    public void stylingBtn(Button btn, int WIDTH, int HEIGHT, int radius) {
        btn.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #8E2DE2, #4A00E0);" +
                        "-fx-background-radius: " + radius + "px;"
        );

        btn.setMaxWidth(WIDTH);
        btn.setMinWidth(WIDTH);
        btn.setMaxHeight(HEIGHT);
        btn.setMinHeight(HEIGHT);
        btn.setTextFill(Color.WHITE);

        styleFadeBtn(btn);
    }


    public void styleFadeBtn(Button btn) {
        FadeTransition ft = new FadeTransition(Duration.millis(200), btn);
        ft.setFromValue(1.0);
        ft.setToValue(0.6);

        FadeTransition ft2 = new FadeTransition(Duration.millis(200), btn);
        ft2.setFromValue(0.6);
        ft2.setToValue(1.0);

        btn.setOnMouseEntered(event -> {
            ft.play();
        });

        btn.setOnMouseExited(event -> {
            ft2.play();
        });

    }
}
