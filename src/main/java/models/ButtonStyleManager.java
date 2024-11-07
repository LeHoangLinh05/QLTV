package models;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.*;

public class ButtonStyleManager {

    private Button currentSelectedButton;
    private final Map<Button, ImageView> buttonImageViewMap;

    public ButtonStyleManager(List<Button> buttons, List<ImageView> icons) {
        buttonImageViewMap = new HashMap<>();
        for (int i = 0; i < buttons.size(); i++) {
            buttonImageViewMap.put(buttons.get(i), icons.get(i));
        }
    }

    public void updateSelectedButton(Button selectedButton) {
        if (currentSelectedButton != null) {
            resetButton(currentSelectedButton);
        }

        selectButton(selectedButton);
        currentSelectedButton = selectedButton;
    }

    private void selectButton(Button selectedButton) {
        selectedButton.getStyleClass().add("menu_button_pressed");
        selectedButton.getStyleClass().remove("menu_button");

        ImageView icon = buttonImageViewMap.get(selectedButton);
        icon.setImage(getImageByButtonId(selectedButton.getId() + "_selected"));
    }

    private void resetButton(Button unselectedButton) {
        unselectedButton.getStyleClass().remove("menu_button_pressed");
        unselectedButton.getStyleClass().add("menu_button");

        ImageView icon = buttonImageViewMap.get(unselectedButton);
        icon.setImage(getImageByButtonId(unselectedButton.getId())); // reset to default
    }

    private Image getImageByButtonId(String buttonId) {
        return new Image(getClass().getResourceAsStream("/images/" + buttonId + ".png"));
    }
}
