package models;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.*;

/**
 * Manages the style and icons of buttons in a user interface.
 */
public class ButtonStyleManager {
    private Button currentSelectedButton;
    private final Map<Button, ImageView> buttonImageViewMap;

    /**
     * Constructs a ButtonStyleManager with the specified buttons and icons.
     *
     * @param buttons the list of buttons to manage
     * @param icons the list of icons corresponding to the buttons
     */
    public ButtonStyleManager(List<Button> buttons, List<ImageView> icons) {
        buttonImageViewMap = new HashMap<>();
        for (int i = 0; i < buttons.size(); i++) {
            buttonImageViewMap.put(buttons.get(i), icons.get(i));
        }
    }

    /**
     * Updates the selected button, applying the selected style and icon.
     *
     * @param selectedButton the button to select
     */
    public void updateSelectedButton(Button selectedButton) {
        if (currentSelectedButton != null) {
            resetButton(currentSelectedButton);
        }

        selectButton(selectedButton);
        currentSelectedButton = selectedButton;
    }

    /**
     * Applies the selected style and icon to the specified button.
     *
     * @param selectedButton the button to select
     */
    private void selectButton(Button selectedButton) {
        selectedButton.getStyleClass().add("menu_button_pressed");
        selectedButton.getStyleClass().remove("menu_button");

        ImageView icon = buttonImageViewMap.get(selectedButton);
        icon.setImage(getImageByButtonId(selectedButton.getId() + "_selected"));
    }

    /**
     * Resets the style and icon of the specified button to the default state.
     *
     * @param unselectedButton the button to reset
     */
    private void resetButton(Button unselectedButton) {
        unselectedButton.getStyleClass().remove("menu_button_pressed");
        unselectedButton.getStyleClass().add("menu_button");

        ImageView icon = buttonImageViewMap.get(unselectedButton);
        icon.setImage(getImageByButtonId(unselectedButton.getId())); // reset to default
    }

    /**
     * Retrieves the image corresponding to the specified button ID.
     *
     * @param buttonId the ID of the button
     * @return the image corresponding to the button ID
     */
    private Image getImageByButtonId(String buttonId) {
        return new Image(getClass().getResourceAsStream("/images/" + buttonId + ".png"));
    }
}
