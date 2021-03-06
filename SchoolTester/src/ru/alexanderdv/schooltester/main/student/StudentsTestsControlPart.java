package ru.alexanderdv.schooltester.main.student;

import java.io.File;
import java.util.HashMap;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import ru.alexanderdv.schooltester.main.AccountsPart;
import ru.alexanderdv.schooltester.main.Main;
import ru.alexanderdv.schooltester.main.TestingPart;
import ru.alexanderdv.schooltester.main.teacher.TeachersTestsControlPart;
import ru.alexanderdv.schooltester.utilities.Config;
import ru.alexanderdv.schooltester.utilities.fx.ProtectedFXWindow;
import ru.alexanderdv.schooltester.utilities.types.Test;
import ru.alexanderdv.schooltester.utilities.types.TestToMarket;
import ru.alexanderdv.schooltester.utilities.types.TesteeInfo;
import ru.alexanderdv.schooltester.utilities.types.TestingTask;
import ru.alexanderdv.simpleutilities.ByteUtils;
import ru.alexanderdv.simpleutilities.SystemUtils;

/**
 * 
 * @author AlexanderDV
 * @version 6.1.5a
 */
public final class StudentsTestsControlPart extends ProtectedFXWindow
{
	public static StudentsTestsControlPart instance;
	private VBox testingTasks;
	private HashMap<String, Node> tests = new HashMap<String, Node>();

	public StudentsTestsControlPart()
	{
		super(null, Main.createPane(1000, 700), 1, 2, true, true);
		instance = this;
		initialize();
	}

	private void initialize()
	{
		panel.getChildren().add(testingTasks = new VBox(5));
	}

	public void addTest(TestingTask task)
	{
		SystemUtils.writeFile(new File("PackedTestTasks/" + task.getTest().getDirName()),
				ByteUtils.objectToByteArray(task));
		if (tests.containsKey(task.getTest().getDirName()))
		{
			testingTasks.getChildren().remove(tests.get(task.getTest().getDirName()));
			tests.remove(task.getTest().getDirName());
		}
		TestToMarket testToMarket = task.getTest();
		Button startTest = new Button("Start test");
		startTest.setOnAction(e ->
		{
			for (String s : testToMarket.getFiles().keySet())
				SystemUtils.writeFile(new File("DepackedTestTasks/" + testToMarket.getDirName() + "/" + s),
						testToMarket.getFiles().get(s));

			TestingPart.instance.createNewTest(TeachersTestsControlPart.theme,
					Test.valueOf(new Config(new File("DepackedTestTasks/"
							+ testToMarket.getDirName() + "/" + testToMarket.getDirName() + ".test"))),
					testToMarket.getDirName(),
					new TesteeInfo(AccountsPart.account.get().getLogin(), "-", "-",
							AccountsPart.account.get().getSurname(), AccountsPart.account.get().getName(),
							AccountsPart.account.get().getSecondName()),
					task.getTesterInfo(), task.getSettings(), task.getTestingPartSettings(), getBounds(), false);
			// try
			// {
			// part.open(new Rectangle((int) stage.getX(), (int) stage.getY(), (int)
			// stage.getWidth(), (int) stage.getHeight()), AccountsPart.account.getValue(),
			// Main.client);
			// }
			// catch (Exception e1)
			// {
			// FXDialogsGenerator.showFXDialog(stage, msgSys.getMsg("signInToWork"), null,
			// true);
			// }
			Main.instance.hideAll();
			for (String s : testToMarket.getFiles().keySet())
				SystemUtils.deleteFile(new File("DepackedTestTask/" + testToMarket.getDirName() + "/" + s));
			SystemUtils.deleteFile(new File("PackedTestTasks/" + task.getTest().getDirName()));
		});
		Node curNode;
		testingTasks.getChildren()
				.add(curNode = new HBox(5, new Label(task.getType().name()), new Label(task.getTesterInfo().getLogin()),
						new Label(task.getTesterInfo().getSurname()), new Label(task.getTesterInfo().getName()),
						new Label(task.getTesterInfo().getSecondName()), new Label(testToMarket.getName()),
						new Label(testToMarket.getTestLanguage()), new Label(testToMarket.getDescription()),
						new Label(testToMarket.getTestVersion()), new Label(testToMarket.getProgramVersion()),
						new Label(testToMarket.getTestSubject()), new Label(testToMarket.getTestCreationDate()),
						startTest));
		tests.put(task.getTest().getDirName(), curNode);
	}

	@Override
	public String name()
	{
		return "studentsTestsControl";
	}

	@Override
	protected void _resize(int w, int h)
	{
		testingTasks.setPrefSize(w, h);
		for (Node n : testingTasks.getChildren())
			if (n instanceof Region)
				((Region) n).setPrefWidth(w);
	}

}
