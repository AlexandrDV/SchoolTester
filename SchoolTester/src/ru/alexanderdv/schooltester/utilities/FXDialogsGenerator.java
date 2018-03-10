package ru.alexanderdv.schooltester.utilities;

import java.awt.Dimension;
import java.awt.Font;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import ru.alexanderdv.schooltester.main.Main;
import ru.alexanderdv.schooltester.utilities.Logger.ExitCodes;

/**
 * 
 * 
 * @author AlexanderDV/AlexandrDV
 * @version 5.5.0a
 */
public class FXDialogsGenerator
{
	/**
	 * Creates FXDialog with owner - 'primaryStage', with message - 'msg', with type of message - 'messageType', with type of option - 'optionType' in the
	 * middle of 'Component'
	 *
	 * @param comp
	 *            - the component in the middle of them FXDialog will be created
	 * @param primaryStage
	 *            - the owner "Stage"
	 * @param msg
	 *            - the message of created FXDialog
	 * @param messageType
	 *            - the messageType of created FXDialog
	 * @param optionType
	 *            - the optionType of created FXDialog
	 * @param frameIsFX
	 *            - the type of window frame
	 */
	public static Stage showFXDialog(Stage comp, Stage parPos, Object msg, int messageType, int optionType, boolean frameIsFX, boolean wait)
	{
		Stage stage = new Stage();
		try
		{
			Pane panel = new Pane();
			Pane canvas = new Pane();
			Dimension size = MathAndTextUtils.size((String) msg, new Font("System", 0, 12));

			if (msg instanceof String)
			{
				int w = ((int) (MathAndTextUtils.size(((String) msg).split("\n")[0], new Font("System", 0, 12)).getWidth() / ((String) msg).split("\n")[0]
						.length()));
				if (size.getWidth() > 800)
				{
					msg = MathAndTextUtils.changeTextToWidth((String) msg, 800 / w, true, true);
					size = MathAndTextUtils.size((String) msg, new Font("System", 0, 12));
				}
			}
			Label label = new Label(msg.toString());
			if (msg instanceof javafx.scene.image.Image)
			{
				canvas.setBackground(new Background(new BackgroundImage((javafx.scene.image.Image) msg, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
						BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT)));
				canvas.setPrefWidth(((javafx.scene.image.Image) msg).getWidth());
				canvas.setPrefHeight(((javafx.scene.image.Image) msg).getHeight());
				panel.getChildren().add(canvas);
			}
			else panel.getChildren().add(label);

			stage.setTitle(Main.program);
			if (frameIsFX)
			{
				stage.initStyle(StageStyle.UNDECORATED);
				FXScene fxScene = new FXScene(stage, 1, Main.program, false, 1);
				fxScene.setContent(panel, Math.max(((msg instanceof javafx.scene.image.Image) ? ((javafx.scene.image.Image) msg).getWidth() : size.getWidth()),
						40 + MathAndTextUtils.size(stage.getTitle(), fxScene.getTitleFont()).width + fxScene.getButtonsWidth()),
						((msg instanceof javafx.scene.image.Image) ? ((javafx.scene.image.Image) msg).getHeight() : size.getHeight()));
				stage.setScene(new Scene(fxScene));
			}
			else
			{
				stage.initStyle(StageStyle.DECORATED);
				panel.setPrefSize(((msg instanceof javafx.scene.image.Image) ? ((javafx.scene.image.Image) msg).getWidth() : size.getWidth()),
						((msg instanceof javafx.scene.image.Image) ? ((javafx.scene.image.Image) msg).getHeight() : size.getHeight()));
				stage.setScene(new Scene(panel));
			}
			stage.sizeToScene();
			stage.setResizable(false);
			stage.getIcons().clear();
			stage.getIcons().add(new javafx.scene.image.Image(Logger.class.getResource("/Icon32x.png").openStream()));
			stage.initModality(Modality.WINDOW_MODAL);
			stage.initOwner(comp);
			if (parPos != null)
			{
				stage.setX((int) (parPos.getX() + parPos.getHeight() / 2 - stage.getWidth() / 2));
				stage.setY((int) (parPos.getY() + parPos.getHeight() / 2 - stage.getHeight() / 2));
			}
			if (wait)
				stage.showAndWait();
			else stage.show();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Main.exit(ExitCodes.UnknownError);
		}
		return stage;
	}

}
