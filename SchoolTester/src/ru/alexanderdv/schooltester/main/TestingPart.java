package ru.alexanderdv.schooltester.main;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import ru.alexanderdv.fxutilities.components.ButtonFX;
import ru.alexanderdv.schooltester.utilities.Logger.ExitCodes;
import ru.alexanderdv.schooltester.utilities.MessageSystem;
import ru.alexanderdv.schooltester.utilities.SystemUtils;
import ru.alexanderdv.schooltester.utilities.fx.FXDialogsGenerator;
import ru.alexanderdv.schooltester.utilities.fx.ProtectedFXWindow;
import ru.alexanderdv.schooltester.utilities.types.Question;
import ru.alexanderdv.schooltester.utilities.types.Question.Answer;
import ru.alexanderdv.schooltester.utilities.types.Question.QuestionType;
import ru.alexanderdv.schooltester.utilities.types.Test;
import ru.alexanderdv.schooltester.utilities.types.Theme;
import ru.alexanderdv.schooltester.utilities.types.UserAnswer;
import ru.alexanderdv.simpleutilities.Entry;

/**
 * 
 * 
 * @author AlexanderDV/AlexandrDV
 * @version 5.9.0a
 */
public class TestingPart extends ProtectedFXWindow
{

	private static final MessageSystem msgSys = StartPart.msgSys;
	private Test test;
	private Theme theme;
	private UserAnswer[] answers;
	private int[] buttonsArrangement;
	private ArrayList<Integer>[] buttonsArrangementForDistribution;
	private ArrayList<Integer> skippedQuestions;
	private int currentQuestionNumber;
	private int lastNotSkippedQuestion;
	private boolean[] selectedAnswers;
	private boolean indicateQualityOfLastAnswer, indicateQualityOfAllAnswers, showRightAnswer, canGoToAllQuestions, skipButtonOption, pauseOption,
			pauseOnUnfocus, fixedQSelectBtnHeight, hide;
	private boolean paused;
	private int timeToTest;
	private float timeOfTest, fullTime;
	private int rightAnswers, perfectAnswers;
	private int timeToPause;
	private long lastTime;
	private boolean wasGoToEnd;
	private Pane questionSelectPanel, answersPanel;
	private ButtonFX[] questionSelectAnswerButtons;
	private ButtonFX questionSign;
	private ButtonFX[] answerButtons;
	private ButtonFX timer, skip, next, finish, back;
	private Pane qualityIndicator;
	private Pane[] qualityIndicators;
	private boolean finished;
	private boolean unlimitedTime;

	/**
	 * 
	 * @param theme
	 * @param parentPosition
	 * @param testName
	 * @param test.getQuestions()
	 * @param classNumber
	 * @param classLetter
	 * @param surname
	 * @param name
	 * @param secondName
	 * @param timeToTest
	 * @param indicateQualityOfLastAnswer
	 * @param indicateQualityOfAllAnswers
	 * @param showRightAnswer
	 * @param canGoToAllQuestions
	 * @param skipButtonOption
	 * @param pauseOption
	 * @param pauseOnUnfocus
	 * @param anticopy
	 * @param antiscreenshot
	 */
	public TestingPart(String secondaryTitle, URL url, Rectangle parentPosition, final Test test, final Theme theme, String classNumber, String classLetter,
			String surname, String name, String secondName, boolean indicateQualityOfLastAnswer, boolean indicateQualityOfAllAnswers, boolean showRightAnswer,
			boolean canGoToAllQuestions, boolean skipButtonOption, boolean pauseOption, boolean pauseOnUnfocus, boolean anticopy, boolean antiscreenshot,
			boolean fixedQSelectBtnHeight, boolean hide, int spaceText, boolean fillAllHeight, boolean maxHeight)
	{
		super(secondaryTitle, url, 0, 1);

		this.questionSelectPanel = InitTestingPart.instance.questionSelectPanel;
		this.answersPanel = InitTestingPart.instance.answersPanel;
		this.questionSign = InitTestingPart.instance.questionSign;
		this.answerButtons = new ButtonFX[6];
		this.timer = InitTestingPart.instance.timer;
		this.skip = InitTestingPart.instance.skip;
		this.next = InitTestingPart.instance.next;
		this.finish = InitTestingPart.instance.finish;
		this.qualityIndicator = InitTestingPart.instance.qualityIndicator;
		this.answersPanel = InitTestingPart.instance.answersPanel;
		skippedQuestions = new ArrayList<Integer>();
		answers = new UserAnswer[test.getQuestions().length];
		this.unlimitedTime = test.timeIsUnlimited();
		if (!unlimitedTime)
			this.timeToTest = test.getMaxTestTime();
		this.test = test;
		this.theme = theme;
		this.indicateQualityOfLastAnswer = indicateQualityOfLastAnswer;
		this.indicateQualityOfAllAnswers = indicateQualityOfAllAnswers;
		this.showRightAnswer = showRightAnswer;
		this.canGoToAllQuestions = canGoToAllQuestions;
		this.skipButtonOption = skipButtonOption;
		this.pauseOption = pauseOption;
		this.pauseOnUnfocus = pauseOnUnfocus;
		this.fixedQSelectBtnHeight = fixedQSelectBtnHeight;
		this.hide = hide;

		this.spaceText = spaceText;
		this.fillAllHeight = fillAllHeight;
		this.maxHeight = maxHeight;

		if (unlimitedTime)
		{
			pauseOption = false;
			pauseOnUnfocus = false;
			timeOfTest = timeToPause = timeToTest = -1;
		}

		ButtonFX.anticntrlc = anticopy;
		ButtonFX.antiscreenshot = antiscreenshot;
		if (antiscreenshot)
		{
			class ImageSelection implements Transferable
			{
				private Image image;

				private ImageSelection(Image image)
				{
					this.image = image;
				}

				public DataFlavor[] getTransferDataFlavors()
				{
					return new DataFlavor[]
					{
							DataFlavor.imageFlavor
					};
				}

				public boolean isDataFlavorSupported(DataFlavor flavor)
				{
					return DataFlavor.imageFlavor.equals(flavor);
				}

				public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException
				{
					if (!DataFlavor.imageFlavor.equals(flavor))
						throw new UnsupportedFlavorException(flavor);
					return image;
				}
			}
			ButtonFX.antish1 = e ->
			{
				if (e.getCode() == KeyCode.PRINTSCREEN)
				{
					FXDialogsGenerator.showFXDialog(stage, (Stage) null, msgSys.getMsg("printscreenWasClicked"), 0, 0, Main.isFxWindowFrame(), true);
					Timer timer = new Timer(100, ae ->
					{
						Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
						clipboard.setContents(new ImageSelection(new ImageIcon().getImage()), null);
					});
					timer.setRepeats(false);
					timer.start();
				}
			};
		}
		createGUI(parentPosition, test.getName(), classNumber, classLetter, surname, name, secondName);
		for (int jj = 0; jj < answerButtons.length; jj++)
		{
			int j = jj;
			ButtonFX btn = answerButtons[j];
			btn.addActionListener((e) ->
			{
				btn.setClicked((test.getQuestions()[currentQuestionNumber].getType() == QuestionType.PickOne || test.getQuestions()[currentQuestionNumber]
						.getType() == QuestionType.SelectSome) ? selectedAnswers[j] = !selectedAnswers[j] : false);
				updateButtonMenu(currentQuestionNumber, j);
				if (test.getQuestions()[currentQuestionNumber].getType() == QuestionType.PickOne)
					for (int k = 0; k < answerButtons.length; k++)
						answerButtons[k].setClicked(j == k);
			});
		}
		createQuestionSelectPanel();
		updateQuestionSelectPanel();
		openQuestion(-1, 0);

		lastTime = Calendar.getInstance().getTimeInMillis();
		new Timer(1, e ->
		{
			if (!finished)
			{
				long time = Calendar.getInstance().getTimeInMillis();
				if (test.getTableQuestionSelector() == null)
				{
					if (!paused)
						timeOfTest += ((float) (time - lastTime)) / 1000f;
					fullTime += ((float) (time - lastTime)) / 1000f;
					timeToPause--;
					String newTimerText = toSize((timeToTest - (int) timeOfTest) / 60, 2).substring(0, 2) + ":" + toSize((timeToTest - (int) timeOfTest) % 60,
							2).substring(0, 2);
					if (!timerText.equals(newTimerText))
						Platform.runLater(() -> timer.setText(timerText = newTimerText));
					if (timeOfTest >= timeToTest)
						Platform.runLater(() -> finish.click());
				}
				lastTime = time;
			}
		}).start();
		updateLabelsInPart();
	}

	String timerText = "";
	int answerButtonSpace, answerButtonHeightWithSpace, answerButtonHeight, defaultAswerButtonHeight;
	boolean showLogs;

	/**
	 * 
	 * @param parentPosition
	 * @param testName
	 * @param classNumber
	 * @param classLetter
	 * @param surname
	 * @param name
	 * @param secondName
	 */
	private void createGUI(Rectangle parentPosition, String testName, String classNumber, String classLetter, String surname, String name, String secondName)
	{
		if (test.getTableQuestionSelector() == null)
			if (pauseOnUnfocus)
				stage.focusedProperty().addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean focused) -> pause(!focused
						&& !showLogs));

		questionSign = new ButtonFX("", new CornerRadii(0, 0, 40, 40, false), new CornerRadii(0, 0, 32, 32, false));
		questionSign.setFramesize(6);
		InitTestingPart.instance.signPanel.getChildren().add(questionSign);
		questionSign.setBounds(0, 0, InitTestingPart.instance.signPanel.getPrefWidth(), InitTestingPart.instance.signPanel.getPrefHeight());
		questionSign.setDisabledV(true);
		if (test.getTableQuestionSelector() != null)
		{
			InitTestingPart.instance.questionsTablePane.getChildren().add(new Label(test.getName()));
			for (int x = 0; x < test.getTableQuestionSelector().getCols().length; x++)
			{
				Label lbl = new Label(test.getTableQuestionSelector().getCols()[x]);
				lbl.setLayoutY(x * 40 + 40);
				InitTestingPart.instance.questionsTablePane.getChildren().add(lbl);
			}
			for (int y = 0; y < test.getTableQuestionSelector().getRows().length; y++)
			{
				Label lbl = new Label(test.getTableQuestionSelector().getRows()[y]);
				lbl.setLayoutY(y * 40 + 40);
				InitTestingPart.instance.questionsTablePane.getChildren().add(lbl);
			}
			for (int x = 0; x < test.getTableQuestionSelector().getQuestionsTable().length; x++)
				for (int y = 0; y < test.getTableQuestionSelector().getQuestionsTable()[x].length; y++)
				{
					Button btn = new Button(test.getTableQuestionSelector().getQuestionsTable()[x][y].getText());
					btn.setLayoutX(x * 40 + 40);
					btn.setLayoutY(y * 40 + 40);
					int xx = x, yy = y;
					btn.setOnAction(e ->
					{
						InitTestingPart.instance.questionsTablePane.setVisible(false);
						InitTestingPart.instance.testingPane.setVisible(true);
						openQuestion(currentQuestionNumber, currentQuestionNumber = test.getQuestionNumberAtQuestionsByQuestionIndex(test
								.getTableQuestionSelector().getQuestionsTable()[xx][yy].getQuestionNumber()));
					});
					InitTestingPart.instance.questionsTablePane.getChildren().add(btn);
				}
		}
		// Options pane
		{
			int pos = 0, count = 0;

			if (!unlimitedTime)
			{
				timer = new ButtonFX(toSize((int) timeToTest / 60, 2).substring(0, 2) + ":" + toSize((int) timeToTest % 60, 2).substring(0, 2), new CornerRadii(
						8, 8, 8, 8, false), new CornerRadii(6, 6, 6, 6, false));
				timer.addActionListener(e -> pause(!paused));
				InitTestingPart.instance.optionButtonsPanel.getChildren().add(timer);
				count++;
			}

			if (test.getTableQuestionSelector() == null)
			{
				skip = new ButtonFX("", new CornerRadii(8, 8, 8, 8, false), new CornerRadii(6, 6, 6, 6, false));
				skip.addActionListener(e -> skip());
				skip.setDisabledV(!skipButtonOption);
				InitTestingPart.instance.optionButtonsPanel.getChildren().add(skip);
				count++;

				next = new ButtonFX("", new CornerRadii(8, 8, 8, 8, false), new CornerRadii(6, 6, 6, 6, false));
				next.addActionListener(e -> next());
				InitTestingPart.instance.optionButtonsPanel.getChildren().add(next);
				count++;

				finish = new ButtonFX("", new CornerRadii(8, 8, 8, 8, false), new CornerRadii(6, 6, 6, 6, false));
				finish.addActionListener(e ->
				{
					if (skippedQuestions.size() == 1)
						if (skippedQuestions.get(0) == currentQuestionNumber)
							goNext();
					if (!e.getActionCommand().equals("user") || skippedQuestions.size() == 0)
						finish(testName, classNumber, classLetter, surname, name, secondName);
					else FXDialogsGenerator.showFXDialog(stage, (Stage) null, msgSys.getMsg("youHaveSkipped"), 0, 0, Main.isFxWindowFrame(), true);
				});
				InitTestingPart.instance.optionButtonsPanel.getChildren().add(finish);
				count++;
			}

			if (test.getTableQuestionSelector() != null)
			{
				back = new ButtonFX("", new CornerRadii(8, 8, 8, 8, false), new CornerRadii(6, 6, 6, 6, false));
				back.addActionListener(e -> back());
				InitTestingPart.instance.optionButtonsPanel.getChildren().add(back);
				count++;
			}
			// Set bounds
			int optionsSpace = 5, optionButtonAndSpaceWidth = ((int) InitTestingPart.instance.optionButtonsPanel.getPrefWidth() + optionsSpace) / count,
					optionButtonWidth = optionButtonAndSpaceWidth - optionsSpace;
			if (timer != null)
				timer.setBounds(optionButtonAndSpaceWidth * pos++, 0, optionButtonWidth, InitTestingPart.instance.optionButtonsPanel.getPrefHeight());
			if (skip != null)
				skip.setBounds(optionButtonAndSpaceWidth * pos++, 0, optionButtonWidth, InitTestingPart.instance.optionButtonsPanel.getPrefHeight());
			if (next != null)
				next.setBounds(optionButtonAndSpaceWidth * pos++, 0, optionButtonWidth, InitTestingPart.instance.optionButtonsPanel.getPrefHeight());
			if (finish != null)
				finish.setBounds(optionButtonAndSpaceWidth * pos++, 0, optionButtonWidth, InitTestingPart.instance.optionButtonsPanel.getPrefHeight());
			if (back != null)
				back.setBounds(optionButtonAndSpaceWidth * pos++, 0, optionButtonWidth, InitTestingPart.instance.optionButtonsPanel.getPrefHeight());
		}

		qualityIndicator.setVisible(indicateQualityOfLastAnswer);
		if (indicateQualityOfAllAnswers)
		{
			qualityIndicators = new Pane[test.getQuestions().length];
			for (int i = 0; i < qualityIndicators.length; i++)
			{
				qualityIndicator.getChildren().add(qualityIndicators[i] = new Pane());
				qualityIndicators[i].setLayoutX(qualityIndicator.getPrefWidth() / test.getQuestions().length * i);
				qualityIndicators[i].setPrefSize(qualityIndicator.getPrefWidth() / test.getQuestions().length - 5, qualityIndicator.getPrefHeight());
			}
		}
		answerButtonSpace = 5;
		answerButtonHeightWithSpace = ((int) answersPanel.getPrefHeight() + answerButtonSpace) / answerButtons.length;
		defaultAswerButtonHeight = answerButtonHeight = answerButtonHeightWithSpace - answerButtonSpace;

		for (int i = 0; i < answerButtons.length; i++)
		{
			answerButtons[i] = new ButtonFX("", new CornerRadii(8, 8, 8, 8, false), new CornerRadii(6, 6, 6, 6, false));
			answersPanel.getChildren().add(answerButtons[i]);
			answerButtons[i].setBounds(0, answerButtonHeightWithSpace * i, answersPanel.getPrefWidth(), answerButtonHeight);
		}

		window.setVisible(true);
	}

	private void back()
	{
		saveAnswer(currentQuestionNumber);
		handleQuestion();
		InitTestingPart.instance.testingPane.setVisible(false);
		InitTestingPart.instance.questionsTablePane.setVisible(true);
	}

	/**
	 * 
	 */
	private void createQuestionSelectPanel()
	{
		questionSelectPanel.setPrefSize(InitTestingPart.instance.scrollPane.getPrefWidth(), InitTestingPart.instance.scrollPane.getPrefHeight() - 2);
		InitTestingPart.instance.scrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
		InitTestingPart.instance.scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
		questionSelectAnswerButtons = new ButtonFX[test.getQuestions().length];
		int space = 4, height = fixedQSelectBtnHeight ? 32
				: (int) Math.max(32, (questionSelectPanel.getPrefHeight() + space) / test.getQuestions().length - space);
		questionSelectPanel.setPrefHeight((height + space) * test.getQuestions().length - space);
		for (int i = 0; i < test.getQuestions().length; i++)
		{
			questionSelectAnswerButtons[i] = new ButtonFX(i + 1 + "/" + test.getQuestions().length, new CornerRadii(8, 8, 8, 8, false), new CornerRadii(6, 6, 6,
					6, false));
			int q = i;
			questionSelectAnswerButtons[i].addActionListener(event ->
			{
				questionSelectAnswerButtons[q].setClicked(false);
				int prev = currentQuestionNumber;
				currentQuestionNumber = q;
				saveAnswer(prev);
				openQuestion(prev, currentQuestionNumber);
				for (ButtonFX btnx : questionSelectAnswerButtons)
				{
					btnx.setClicked(false);
					btnx.setPressedV(false);
					btnx.setSelected(false);
				}
			});
			questionSelectAnswerButtons[i].setBounds(0, i * (height + space), InitTestingPart.instance.scrollPane.getPrefWidth() - 16, height);
			questionSelectPanel.getChildren().add(questionSelectAnswerButtons[i]);
		}
	}

	/**
	 * 
	 */
	private void pause(boolean paused)
	{
		if (pauseOption && timeToPause <= 0)
		{
			this.paused = paused;
			questionSign.setVisible(!paused);
			next.setVisible(!paused);
			finish.setVisible(!paused);
			qualityIndicator.setVisible(!paused);
			skip.setVisible(!paused);
			InitTestingPart.instance.scrollPane.setVisible(!paused);
			answersPanel.setVisible(!paused);
			timeToPause = 300;
			timer.setClicked(paused);
		}
	}

	/**
	 * 
	 */
	private void skip()
	{
		int qn = currentQuestionNumber;
		goNext();
		if (!skippedQuestions.contains(qn))
			skippedQuestions.add(qn);
		openQuestion(currentQuestionNumber - 1, currentQuestionNumber);
	}

	/**
	 * 
	 */
	private void next()
	{
		saveAnswer(currentQuestionNumber);
		handleQuestion();
		openQuestion(currentQuestionNumber, currentQuestionNumber);
	}

	/**
	 * 
	 * @param window
	 * @param _class
	 * @param surname
	 * @param name
	 * @param secondName
	 */
	private void finish(String testName, String classNumber, String classLetter, String surname, String name, String secondName)
	{
		saveAnswer(currentQuestionNumber);
		handleQuestion();
		paused = true;
		finished = true;
		int maxResult = 0;
		for (Question question : test.getQuestions())
			maxResult += question.getMaxAward();
		showResult(testName, classNumber, classLetter, surname, name, secondName, countResult(), maxResult);
		if (hide)
		{
			stage.hide();
			Main.instance.showAllHided();
		}
		else Main.exit(ExitCodes.UserCloseProgram);
	}

	/**
	 * 
	 * @return
	 */
	private int countResult()
	{
		int result = rightAnswers = perfectAnswers = 0;
		for (int i = 0; i < test.getQuestions().length; i++)
		{
			int[] results = _handleQuestion(i);
			result += results[1];
			rightAnswers += (results[0] > results[1] || results[2] == results[1]) ? 1 : 0;
			perfectAnswers += results[2] == results[1] ? 1 : 0;
		}
		return result;
	}

	/**
	 * 
	 */
	private void handleQuestion()
	{
		saveAnswer(currentQuestionNumber);
		int[] res = _handleQuestion(currentQuestionNumber);
		int minResult = res[0], questionResult = res[1], maxResult = res[2];

		if (questionResult > minResult || questionResult == maxResult)
			rightAnswers++;
		if (questionResult == maxResult)
			perfectAnswers++;

		if (indicateQualityOfLastAnswer)
		{
			Pane panel = indicateQualityOfAllAnswers ? (Pane) qualityIndicators[currentQuestionNumber] : qualityIndicator;
			if (questionResult == maxResult)
				panel.setBackground(test.getPerfectAnswerBackground());
			else if (questionResult > minResult)
				panel.setBackground(test.getRightAnswerBackground());
			else panel.setBackground(test.getWrongAnswerBackground());
			if (questionResult > maxResult)
				FXDialogsGenerator.showFXDialog(stage, (Stage) null, msgSys.getMsg("questionResultMoreThanMaxResult"), 0, 0, Main.isFxWindowFrame(), true);
		}
		if (showRightAnswer)
		{
			String rightAnswer = "";
			if (test.getQuestions()[currentQuestionNumber].getType() == QuestionType.EnterText)
			{
				for (Answer a : test.getQuestions()[currentQuestionNumber].getAnswers())
					if (test.getQuestions()[currentQuestionNumber].getAward() + a.getAward() == test.getQuestions()[currentQuestionNumber].getMaxAward())
						rightAnswer += a.getText() + "\n";
			}
			int i = 0;
			if (test.getQuestions()[currentQuestionNumber].getType() == QuestionType.PickOne)
				for (Answer a : test.getQuestions()[currentQuestionNumber].getAnswers())
				{
					if (test.getQuestions()[currentQuestionNumber].getAward() + a.getAward() == test.getQuestions()[currentQuestionNumber].getMaxAward())
					{
						rightAnswer += a.getText() + "\n";
						answerButtons[i].setDisabledBackground(theme.getQuestionSelectSkippedBackground());
						answerButtons[i].setDisabledV(true);
					}
					i++;
				}
			if (test.getQuestions()[currentQuestionNumber].getType() == QuestionType.SelectSome)
				for (Answer a : test.getQuestions()[currentQuestionNumber].getAnswers())
				{
					if (test.getQuestions()[currentQuestionNumber].getAward() + a.getAward() > 0)
					{
						rightAnswer += a.getText() + "\n";
						answerButtons[i].setDisabledBackground(theme.getQuestionSelectSkippedBackground());
						answerButtons[i].setDisabledV(true);
					}
					i++;
				}
			FXDialogsGenerator.showFXDialog(stage, (Stage) null, rightAnswer, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, Main
					.isFxWindowFrame(), true);
		}
		goNext();
	}

	/**
	 * 
	 */
	private void goNext()
	{
		if (!canGoToAllQuestions)
			if (skippedQuestions.contains((Object) currentQuestionNumber))
			{
				skippedQuestions.remove((Object) currentQuestionNumber);
				if (skippedQuestions.size() != 0)
				{
					for (int i = 0; i < skippedQuestions.size(); i++)
						if (skippedQuestions.get(i) > currentQuestionNumber)
						{
							currentQuestionNumber = skippedQuestions.get(i);
							break;
						}
						else if (i == skippedQuestions.size() - 1)
							if (skippedQuestions.get(i) >= lastNotSkippedQuestion)
								currentQuestionNumber = skippedQuestions.get(0);
							else currentQuestionNumber = lastNotSkippedQuestion;
				}
				else currentQuestionNumber = lastNotSkippedQuestion;
			}
			else currentQuestionNumber++;
		else currentQuestionNumber++;
	}

	/**
	 * 
	 * @param window
	 * @param _class
	 * @param surname
	 * @param name
	 * @param secondName
	 */
	private void showResult(String testName, String classNumber, String classLetter, String surname, String name, String secondName, int result, int maxResult)
	{
		String text = "";
		text += "syntaxLanguage" + ": \"" + msgSys.getLanguage() + "\"\n";
		text += msgSys.getMsg("testName") + ": \"" + testName + "\"\n";
		text += msgSys.getMsg("classNumber") + ": \"" + classNumber + "\"\n";
		text += msgSys.getMsg("classLetter") + ": \"" + classLetter + "\"\n";
		text += msgSys.getMsg("surname") + ": \"" + surname + "\"\n";
		text += msgSys.getMsg("name") + ": \"" + name + "\"\n";
		text += msgSys.getMsg("secondName") + ": \"" + secondName + "\"\n";
		text += msgSys.getMsg("result") + ": " + result + "\n";
		text += msgSys.getMsg("maxResult") + ": " + maxResult + "\n";
		text += msgSys.getMsg("perfectAnswersAmount") + ": " + perfectAnswers + "\n";
		text += msgSys.getMsg("rightAnswersAmount") + ": " + (rightAnswers - perfectAnswers) + "\n";
		text += msgSys.getMsg("questionsAmount") + ": " + test.getQuestions().length + "\n";
		text += msgSys.getMsg("time") + ": " + toSize((int) timeOfTest / 60, 2) + ":" + toSize((int) timeOfTest % 60, 2) + "\n";
		text += msgSys.getMsg("timeToTest") + ": " + toSize((int) timeToTest / 60, 2) + ":" + toSize((int) timeToTest % 60, 2) + "\n";
		text += msgSys.getMsg("fullTime") + ": " + toSize((int) fullTime / 60, 2) + ":" + toSize((int) fullTime % 60, 2);
		Calendar c = Calendar.getInstance();
		SystemUtils.writeFile(new File("Results/Result From " + c.get(Calendar.YEAR) + "_" + toSize(c.get(Calendar.DAY_OF_YEAR), 2) + "_" + toSize(c.get(
				Calendar.HOUR), 2) + "-" + toSize(c.get(Calendar.MINUTE), 2) + "-" + toSize(c.get(Calendar.SECOND), 2) + ".log"), text, "cp1251");
		showLogs = true;
		FXDialogsGenerator.showFXDialog(stage, (Stage) null, text, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, Main.isFxWindowFrame(), true);
	}

	/**
	 * 
	 * @param i
	 * @param size
	 * @return
	 */
	private String toSize(int i, int size)
	{
		String s = i + "";
		for (; s.length() < size;)
			s = "0" + s;
		return s;
	}

	/**
	 * 
	 */
	public void updateLabelsInPart()
	{
		super.updateLabelsInPart();
		if (next != null)
			next.setText(msgSys.getMsg("next"));
		if (skip != null)
			skip.setText(msgSys.getMsg("skip"));
		if (finish != null)
			finish.setText(msgSys.getMsg("finish"));
		if (back != null)
			back.setText(msgSys.getMsg("back"));
	}

	/**
	 * 
	 */
	private void updateQuestionSelectPanel()
	{
		for (int i = 0; i < test.getQuestions().length; i++)
		{
			questionSelectAnswerButtons[i].setNormalBackground(skippedQuestions.contains(i) ? theme.getQuestionSelectSkippedBackground()
					: theme.getQuestionSelectNormalBackground());
			questionSelectAnswerButtons[i].setNormalFrame(skippedQuestions.contains(i) ? theme.getQuestionSelectSkippedFrame()
					: theme.getQuestionSelectNormalFrame());
			questionSelectAnswerButtons[i].setNormalForeground(skippedQuestions.contains(i) ? theme.getQuestionSelectSkippedForeground()
					: theme.getQuestionSelectNormalForeground());
			questionSelectAnswerButtons[i].setDisabledV(!skippedQuestions.contains(i) && !canGoToAllQuestions || i == currentQuestionNumber);
			questionSelectAnswerButtons[i].setDisabledBackground(i == currentQuestionNumber ? theme.getSpecialButtonsBackground()
					: theme.getQuestionSelectNormalBackground());
			questionSelectAnswerButtons[i].setDisabledFrame(i == currentQuestionNumber ? theme.getSpecialButtonsFrame() : theme.getQuestionSelectNormalFrame());
			questionSelectAnswerButtons[i].setDisabledForeground(i == currentQuestionNumber ? theme.getSpecialButtonsForeground()
					: theme.getQuestionSelectNormalForeground());
		}
	}

	/**
	 * 
	 * @param questionNumber
	 */
	private void saveAnswer(int questionNumber)
	{
		int selectedNumber = -1;
		for (int j = 0; j < selectedAnswers.length; j++)
			if (selectedAnswers[j])
				selectedNumber = j;
		answers[questionNumber] = new UserAnswer(selectedNumber, selectedAnswers.clone(), buttonsArrangement.clone(), buttonsArrangementForDistribution.clone(),
				test.getQuestions()[questionNumber].getType() == QuestionType.EnterText ? answerButtons[0].getMainFieldText() : null);
	}

	int spaceText;
	boolean fillAllHeight, maxHeight;

	private void updateButtonPose(int questionNumber, int btn)
	{
		answerButtonSpace = spaceText;
		answerButtonHeightWithSpace = ((int) answersPanel.getPrefHeight() + answerButtonSpace) / (!fillAllHeight || test.getQuestions()[questionNumber]
				.getType() == QuestionType.EnterText ? answerButtons.length : test.getQuestions()[questionNumber].getAnswers().length);
		answerButtonHeight = maxHeight && test.getQuestions()[questionNumber].getType() != QuestionType.EnterText ? answerButtonHeightWithSpace
				- answerButtonSpace : defaultAswerButtonHeight;
		answerButtons[btn].setBounds(0, (test.getQuestions()[questionNumber].getType() != null ? btn : buttonsArrangement[btn]) * answerButtonHeightWithSpace,
				answerButtons[btn].getPrefWidth(), answerButtonHeight);
	}

	/**
	 * 
	 * @param question
	 * @param info
	 * @param number
	 */
	private void openQuestion(int prev, int questionNumber)
	{
		if (answers[questionNumber] != null)
			buttonsArrangement = answers[questionNumber].getAnswerArrangement();
		else buttonsArrangement = test.getQuestions()[questionNumber].getType() == QuestionType.Arrangement ? intLessZeroArr(answerButtons.length)// array(answerButtons.length)
				: intLessZeroArr(answerButtons.length);
		InitTestingPart.instance.questionInfoTextfield.setText(msgSys.getMsg(test.getQuestions()[questionNumber].getType().name() + "questionInfo"));
		if (answers[questionNumber] != null)
			buttonsArrangementForDistribution = answers[questionNumber].getAnswerArrangementForDistribution();
		else buttonsArrangementForDistribution = intLessZeroListsArr(answerButtons.length);
		if (test.getQuestions()[questionNumber].getType() == QuestionType.EnterText)
		{
			for (ButtonFX button : answerButtons)
				button.setVisible(false);
			answerButtons[0].setVisible(true);
			answerButtons[0].setEditable(true);
			answerButtons[0].setText(answers[questionNumber] != null ? answers[questionNumber].getAnswerText() : "");
			answerButtons[0].setFont(new javafx.scene.text.Font(answerButtons[0].getFont().getName(), test.getQuestions()[questionNumber].getAnswers()[0]
					.getFont().getSize()));
			answerButtons[0].setClicked(false);
			answerButtons[0].setDisabledV(false);
		}
		else
		{
			for (int i = 0; i < answerButtons.length; i++)
				answerButtons[i].setVisible(test.getQuestions()[questionNumber].getAnswers().length > i);
			for (int i = 0; i < test.getQuestions()[questionNumber].getAnswers().length; i++)
			{
				answerButtons[i].setEditable(false);
				answerButtons[i].setText(test.getQuestions()[questionNumber].getAnswers()[i].getText());
				answerButtons[i].setFont(new Font(answerButtons[i].getFont().getName(), test.getQuestions()[questionNumber].getAnswers()[i].getFont()
						.getSize()));
				answerButtons[i].setClicked(false);
				answerButtons[i].setDisabledV(false);
			}
		}
		for (int i = 0; i < test.getQuestions()[questionNumber].getAnswers().length; i++)
		{
			updateButtonPose(questionNumber, i);
			updateButtonMenu(questionNumber, i);
		}
		switch (test.getQuestions()[questionNumber].getType())
		{
			case PickOne:
				questionSign.setDisabledBackground(theme.getPickOne().getQuestionBackground());
				questionSign.setDisabledFrame(theme.getPickOne().getQuestionFrame());
				questionSign.setDisabledForeground(theme.getPickOne().getQuestionForeground());

				for (ButtonFX answer : answerButtons)
				{
					answer.setNormalBackground(theme.getPickOne().getAnswersBackground());
					answer.setNormalFrame(theme.getPickOne().getAnswersFrame());
					answer.setNormalForeground(theme.getPickOne().getAnswersForeground());
				}
				break;
			case SelectSome:
				questionSign.setDisabledBackground(theme.getSelectSome().getQuestionBackground());
				questionSign.setDisabledFrame(theme.getSelectSome().getQuestionFrame());
				questionSign.setDisabledForeground(theme.getSelectSome().getQuestionForeground());

				for (ButtonFX answer : answerButtons)
				{
					answer.setNormalBackground(theme.getSelectSome().getAnswersBackground());
					answer.setNormalFrame(theme.getSelectSome().getAnswersFrame());
					answer.setNormalForeground(theme.getSelectSome().getAnswersForeground());
				}
				break;
			case EnterText:
				questionSign.setDisabledBackground(theme.getEnterText().getQuestionBackground());
				questionSign.setDisabledFrame(theme.getEnterText().getQuestionFrame());
				questionSign.setDisabledForeground(theme.getEnterText().getQuestionForeground());

				for (ButtonFX answer : answerButtons)
				{
					answer.setNormalBackground(theme.getEnterText().getAnswersBackground());
					answer.setNormalFrame(theme.getEnterText().getAnswersFrame());
					answer.setNormalForeground(theme.getEnterText().getAnswersForeground());
				}
				break;
			case Arrangement:
				questionSign.setDisabledBackground(theme.getArrangement().getQuestionBackground());
				questionSign.setDisabledFrame(theme.getArrangement().getQuestionFrame());
				questionSign.setDisabledForeground(theme.getArrangement().getQuestionForeground());

				for (ButtonFX answer : answerButtons)
				{
					answer.setNormalBackground(theme.getArrangement().getAnswersBackground());
					answer.setNormalFrame(theme.getArrangement().getAnswersFrame());
					answer.setNormalForeground(theme.getArrangement().getAnswersForeground());
				}
				break;
			case Matching:
				questionSign.setDisabledBackground(theme.getMatching().getQuestionBackground());
				questionSign.setDisabledFrame(theme.getMatching().getQuestionFrame());
				questionSign.setDisabledForeground(theme.getMatching().getQuestionForeground());

				for (ButtonFX answer : answerButtons)
				{
					answer.setNormalBackground(theme.getMatching().getAnswersBackground());
					answer.setNormalFrame(theme.getMatching().getAnswersFrame());
					answer.setNormalForeground(theme.getMatching().getAnswersForeground());
				}
				break;
			case Distribution:
				questionSign.setDisabledBackground(theme.getDistribution().getQuestionBackground());
				questionSign.setDisabledFrame(theme.getDistribution().getQuestionFrame());
				questionSign.setDisabledForeground(theme.getDistribution().getQuestionForeground());

				for (ButtonFX answer : answerButtons)
				{
					answer.setNormalBackground(theme.getDistribution().getAnswersBackground());
					answer.setNormalFrame(theme.getDistribution().getAnswersFrame());
					answer.setNormalForeground(theme.getDistribution().getAnswersForeground());
				}
				break;
		}
		if (test.getTableQuestionSelector() == null)
		{
			timer.setNormalBackground(theme.getSpecialButtonsBackground());
			timer.setNormalFrame(theme.getSpecialButtonsFrame());
			timer.setNormalForeground(theme.getSpecialButtonsForeground());
			next.setNormalBackground(theme.getSpecialButtonsBackground());
			next.setNormalFrame(theme.getSpecialButtonsFrame());
			next.setNormalForeground(theme.getSpecialButtonsForeground());
			finish.setNormalBackground(theme.getSpecialButtonsBackground());
			finish.setNormalFrame(theme.getSpecialButtonsFrame());
			finish.setNormalForeground(theme.getSpecialButtonsForeground());
			skip.setNormalBackground(theme.getSpecialButtonsBackground());
			skip.setNormalFrame(theme.getSpecialButtonsFrame());
			skip.setNormalForeground(theme.getSpecialButtonsForeground());
		}
		else
		{
			back.setNormalBackground(theme.getSpecialButtonsBackground());
			back.setNormalFrame(theme.getSpecialButtonsFrame());
			back.setNormalForeground(theme.getSpecialButtonsForeground());
		}

		if (!skippedQuestions.contains(questionNumber))
			lastNotSkippedQuestion = questionNumber;
		selectedAnswers = new boolean[test.getQuestions()[questionNumber].getAnswers().length];
		questionSign.setFont(new Font(questionSign.getFont().getName(), test.getQuestions()[questionNumber].getFont().getSize()));
		questionSign.setText(test.getQuestions()[questionNumber].getText());

		if ((questionNumber >= test.getQuestions().length - 1))
			wasGoToEnd = true;

		if (test.getTableQuestionSelector() == null)
		{
			next.setDisabledV(questionNumber >= test.getQuestions().length - 1 || (!canGoToAllQuestions && skippedQuestions.size() == 1 && skippedQuestions.get(
					0) == questionNumber));
			skip.setDisabledV(questionNumber >= test.getQuestions().length - 1);
			finish.setDisabledV(!wasGoToEnd);
		}

		updateQuestionSelectPanel();
	}

	private void updateButtonMenu(int questionNumber, int i)
	{
		ContextMenu m = new ContextMenu();
		if (test.getQuestions()[questionNumber].getType() == QuestionType.Distribution)
		{
			for (int r = 0; r < test.getQuestions()[questionNumber].getIndexesForNames().length; r++)
			{
				CheckMenuItem item = new CheckMenuItem(test.getQuestions()[questionNumber].getIndexesForNames()[r]);
				int rr = r;
				item.setOnAction(e ->
				{
					if (item.isSelected() && !buttonsArrangementForDistribution[i].contains(test.getQuestions()[questionNumber]
							.getIndexesForNamesIndexes()[rr]))
						buttonsArrangementForDistribution[i].add(test.getQuestions()[questionNumber].getIndexesForNamesIndexes()[rr]);
					else if (!item.isSelected() && buttonsArrangementForDistribution[i].contains(test.getQuestions()[questionNumber]
							.getIndexesForNamesIndexes()[rr]))
						buttonsArrangementForDistribution[i].remove((Object) test.getQuestions()[questionNumber].getIndexesForNamesIndexes()[rr]);
				});
				m.getItems().add(item);
			}
		}
		else if (test.getQuestions()[questionNumber].getType() == QuestionType.Matching)
		{
			ToggleGroup tg = new ToggleGroup();
			for (int r = 0; r < test.getQuestions()[questionNumber].getIndexesForNames().length; r++)
			{
				RadioMenuItem item = new RadioMenuItem(test.getQuestions()[questionNumber].getIndexesForNames()[r]);
				item.setToggleGroup(tg);
				int rr = r;
				item.setOnAction(e ->
				{
					buttonsArrangement[i] = test.getQuestions()[questionNumber].getIndexesForNamesIndexes()[rr];
				});
				m.getItems().add(item);
			}
		}
		else if (test.getQuestions()[questionNumber].getType() == QuestionType.Arrangement)
		{
			ToggleGroup tg = new ToggleGroup();
			for (int r = 0; r < test.getQuestions()[questionNumber].getAnswers().length; r++)
			{
				RadioMenuItem item = new RadioMenuItem(r + 1 + "");
				item.setToggleGroup(tg);
				int rr = r;
				item.setOnAction(e ->
				{
					buttonsArrangement[i] = rr;
				});
				m.getItems().add(item);
			}
		}
		boolean equals = true;
		if (answerButtons[i].getContextMenu() != null)
			if (answerButtons[i].getContextMenu().getItems().size() == m.getItems().size())
				for (MenuItem item : answerButtons[i].getContextMenu().getItems())
				{
					boolean match = false;
					for (MenuItem item2 : m.getItems())
					{
						if (item.getText().equals(item2.getText()))
							match = true;
						// System.out.println(item.getText() + "\t\t" + item2.getText());
					}
					if (!match)
						equals = false;
				}
			else equals = false;
		else equals = false;
		if (!equals)
			answerButtons[i].setContextMenu(m);
	}

	/**
	 * 
	 * @param questionNumber
	 * @return
	 */
	private int[] _handleQuestion(int questionNumber)
	{
		Question question = test.getQuestions()[questionNumber];
		if (answers[questionNumber] != null ? answers[questionNumber].getSelectedAnswers() == null && question.getType() == QuestionType.SelectSome
				|| answers[questionNumber].getAnswerText() == null && question.getType() == QuestionType.EnterText : true)
			answers[questionNumber] = new UserAnswer(-1, new boolean[question.getAnswers().length], question.getType() == QuestionType.Arrangement
					? intLessZeroArr(question.getAnswers().length)
					/*
					 * question .getAnswersIndexes()
					 */ : intLessZeroArr(question.getAnswers().length), intLessZeroListsArr(question.getAnswers().length), "");
		int questionResult = question.getAward();
		switch (question.getType())
		{
			case EnterText:
			{
				for (int i = 0; i < question.getAnswers().length; i++)
				{
					String userAnswer = answers[questionNumber].getAnswerText(), curAnswer = question.getAnswers()[i].getText();
					if (question.isIgnoreCase())
					{
						userAnswer = userAnswer.toLowerCase();
						curAnswer = curAnswer.toLowerCase();
					}
					for (String c : question.getIgnoredCharacters().split(""))
					{
						userAnswer = userAnswer.replace(c.toLowerCase(), "");
						curAnswer = curAnswer.replace(c.toLowerCase(), "");
					}
					if (userAnswer.equals(curAnswer))
						questionResult += question.getAnswers()[i].getAward();
				}
			}
				break;
			case PickOne:
			{
				if (answers[questionNumber].getSelectedAnswerNumber() != -1)
					questionResult += question.getAnswers()[answers[questionNumber].getSelectedAnswerNumber()].getAward();
			}
				break;
			case SelectSome:
			{
				for (int i = 0; i < question.getAnswers().length; i++)
					questionResult += answers[questionNumber].getSelectedAnswers()[i] ? question.getAnswers()[i].getAward() : 0;
			}
				break;
			case Arrangement:
			{
				int res = 0;
				int[] answersIndexes = new int[question.getAnswersIndexes().length];
				for (int i = 0; i < question.getAnswersIndexes().length; i++)
					answersIndexes[question.getAnswersIndexes()[i]] = i;
				for (HashMap<Integer, Integer> hashMap : question.getAnswerArrangementForArrangement().keySet())
				{
					boolean b = true;
					for (Integer integer : hashMap.keySet())
						if (answers[questionNumber].getAnswerArrangement()[answersIndexes[integer]] != hashMap.get(integer))
							b = false;
					if (b)
						if (question.isHandleOnlyMaximal())
							res = Math.max(res, question.getAnswerArrangementForArrangement().get(hashMap));
						else res += question.getAnswerArrangementForArrangement().get(hashMap);
				}
				questionResult += res;
			}
				break;
			case Matching:
			{
				int res = 0;
				int[] answersIndexes = new int[question.getAnswersIndexes().length];
				for (int i = 0; i < question.getAnswersIndexes().length; i++)
					answersIndexes[question.getAnswersIndexes()[i]] = i;
				for (HashMap<Integer, Integer> hashMap : question.getAnswerArrangementForMatching().keySet())
				{
					boolean b = true;
					for (Integer integer : hashMap.keySet())
						if (answers[questionNumber].getAnswerArrangement()[answersIndexes[integer]] != hashMap.get(integer))
							b = false;
					if (b)
						if (question.isHandleOnlyMaximal())
							res = Math.max(res, question.getAnswerArrangementForMatching().get(hashMap));
						else res += question.getAnswerArrangementForMatching().get(hashMap);
				}
				questionResult += res;
			}
				break;
			case Distribution:
			{
				int res = 0;
				int[] answersIndexes = new int[question.getAnswersIndexes().length];
				for (int i = 0; i < question.getAnswersIndexes().length; i++)
					answersIndexes[question.getAnswersIndexes()[i]] = i;
				for (HashMap<Entry<Integer, Boolean>, ArrayList<Integer>> hashMap : question.getAnswerArrangementForDistribution().keySet())
				{
					boolean b = true;
					ArrayList<Integer> list;
					for (Entry<Integer, Boolean> integerBoolean : hashMap.keySet())
						if ((list = hashMap.get(integerBoolean)) != null)
						{
							for (Integer i : list)
								if (!answers[questionNumber].getAnswerArrangementForDistribution()[answersIndexes[integerBoolean.getFirst()]].contains(i))
									b = false;
							if (integerBoolean.getSecond())
								if (answers[questionNumber].getAnswerArrangementForDistribution()[answersIndexes[integerBoolean.getFirst()]].size() != hashMap
										.get(integerBoolean).size())
									b = false;
						}
						else b = false;
					if (b)
						if (question.isHandleOnlyMaximal())
							res = Math.max(res, question.getAnswerArrangementForDistribution().get(hashMap));
						else res += question.getAnswerArrangementForDistribution().get(hashMap);
				}
				questionResult += res;
			}
				break;
		}
		questionResult = Math.max(question.getMinResult(), questionResult);
		return new int[]
		{
				question.getAward(),
				questionResult,
				question.getMaxAward()
		};
	}

	private int[] intLessZeroArr(int length)
	{
		int[] i = new int[length];
		for (int j = 0; j < i.length; j++)
			i[j] = -1;
		return i;
	}

	private ArrayList<Integer>[] intLessZeroListsArr(int length)
	{
		@SuppressWarnings("unchecked")
		ArrayList<Integer>[] i = new ArrayList[length];
		for (int j = 0; j < i.length; j++)
			i[j] = new ArrayList<Integer>();
		return i;
	}
}