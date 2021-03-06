package ru.alexanderdv.schooltester.main;

import java.util.ArrayList;
import java.util.Collection;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import ru.alexanderdv.fxutilities.components.ComboboxWithAdd;
import ru.alexanderdv.schooltester.utilities.enums.Subject;
import ru.alexanderdv.schooltester.utilities.fx.FXDialogsGenerator;
import ru.alexanderdv.schooltester.utilities.network.AccountPacket;
import ru.alexanderdv.schooltester.utilities.types.Account;
import ru.alexanderdv.schooltester.utilities.types.Account.AccountType;
import ru.alexanderdv.schooltester.utilities.types.Person.Rodstvennik;

/**
 * 
 * @author AlexanderDV
 * @version 6.1.5a
 */
@Deprecated
public final class InitAccountsPart
{
	public static InitAccountsPart instance;

	@FXML
	public TextField loginField, passwordField, passwordRepeatField;
	@FXML
	public ComboBox<AccountType> accountTypeCombobox;

	@FXML
	public TabPane tabPane;

	@FXML
	public Tab signUpTab;
	@FXML
	public Button signUpButton;

	@FXML
	public Tab signInTab;
	@FXML
	public Button signInButton;

	@FXML
	public Tab deleteAccountTab;
	@FXML
	public Button deleteAccountButton;

	@FXML
	public Tab signOutTab;
	@FXML
	public Button signOutButton;

	@FXML
	public Tab profileTab;

	@FXML
	public Tab securityTab;
	@FXML
	public TextField newPasswordField, newPasswordRepeatField;
	@FXML
	public Button changePasswordButton;

	@FXML
	public Tab mainTab;
	@FXML
	public TextField surnameField, nameField, secondNameField, countryField, regionField, cityField, schoolField;
	@FXML
	public MenuButton subjectsCombobox;
	public ArrayList<Subject> selectedsubjectsCombobox = new ArrayList<Subject>();

	@FXML
	public Tab familyTab;
	@FXML
	public Label maritalStatusLabel, grandParentsLabel, parentsLabel, childrenLabel, grandChildrenLabel, siblingsLabel,
			exSpousesLabel, spouseLabel, otherRelativesLabel;
	@FXML
	public TextField maritalStatusField, spouseField;
	@FXML
	public ComboboxWithAdd grandParentsCombobox, parentsCombobox, childrenCombobox, grandChildrenCombobox,
			siblingsCombobox, exSpousesCombobox, otherRelativesCombobox;

	@FXML
	public Tab contactsTab;
	@FXML
	public Label phoneNumbersLabel, emailsLabel, personalSitesLabel, otherSitesLabel, otherContactsLabel;
	@FXML
	public ComboboxWithAdd phoneNumbersCombobox, emailsCombobox, personalSitesCombobox, otherSitesCombobox,
			otherContactsCombobox;

	@FXML
	public Tab lifeTab;
	@FXML
	public Label ageLabel, genderLabel, mainLanguagesLabel, otherLanguagesLabel, educationLabel, careerLabel;
	@FXML
	public TextField ageField;
	@FXML
	public ComboBox<String> genderCombobox;
	@FXML
	public ComboboxWithAdd mainLanguagesCombobox, otherLanguagesCombobox;
	@FXML
	public TextArea educationArea, careerArea;

	@FXML
	public Tab ideasTab;
	@FXML
	public Label aboutAlhogolLabel, aboutNarcoticsLabel, aboutSmokingLabel, ideasLabel, interestsLabel,
			favouriteBlogsLabel, favouriteBooksLabel, favouriteComputerGamesLabel, favouriteFilmsLabel,
			favouriteGamesLabel, favouriteMusicLabel, favouritePeopleLabel, favouriteShowsLabel, favouriteQuotesLabel,
			mainInLifeLabel, mainInPeopleLabel, worldOutlookLabel, otherViewsLabel, politicalViewsLabel,
			inspirationLabel;
	@FXML
	public TextField aboutAlhogolField, aboutNarcoticsField, aboutSmokingField, ideasField, interestsField,
			favouriteBlogsField, favouriteBooksField, favouriteComputerGamesField, favouriteFilmsField,
			favouriteGamesField, favouriteMusicField, favouritePeopleField, favouriteShowsField, favouriteQuotesField,
			mainInLifeField, mainInPeopleField, worldOutlookField, otherViewsField, politicalViewsField,
			inspirationField;

	@FXML
	public Tab aboutYouTab;
	@FXML
	public Label biografyLabel, homeCountryLabel, homeRegionLabel, homeCityLabel;
	@FXML
	public TextArea biografyArea;
	@FXML
	public TextField homeCountryField, homeRegionField, homeCityField;
	@FXML
	public Button saveButton;

	@FXML
	public GridPane profileMainFields, familyFields, contactsFields, lifeFields, lifeFields2, ideasFields,
			aboutYouFields;

	@FXML
	public ScrollPane ideasScrollpane;

	@FXML
	public TabPane profileTabs;

	public void createActionHandlers()
	{
		AccountsPart.account.addChangeListener(e ->
		{
			accountTypeCombobox.getSelectionModel().select(AccountsPart.account.get() == null ? AccountType.Student
					: AccountsPart.account.get().getAccountType());
			AccountsPart.instance.resize();
		});
		for (MenuItem item : subjectsCombobox.getItems())
			item.setOnAction(e ->
			{
				if (selectedsubjectsCombobox.contains(Subject.valueOf(item.getText().substring(offset))))
					selectedsubjectsCombobox.remove(Subject.valueOf(item.getText().substring(offset)));
				else selectedsubjectsCombobox.add(Subject.valueOf(item.getText().substring(offset)));
				updateSelectedsubjectsCombobox();
			});
		updateSelectedsubjectsCombobox();
		signUpButton.setOnAction(e ->
		{
			if (passwordField.getText().equals(passwordRepeatField.getText()))
			{
				Main.sendToServer(new AccountPacket("signUp", AccountsPart.account.get(),
						new Account(accountTypeCombobox.getSelectionModel().getSelectedItem(), loginField.getText(),
								passwordField.getText())));
			}
			else FXDialogsGenerator.showFXDialog((Stage) null, Main.msgSys.getMsg("passwordsNotMatch"), null, true);
		});
		signInButton.setOnAction(e ->
		{
			Main.sendToServer(new AccountPacket("signIn", AccountsPart.account.get(),
					new Account(accountTypeCombobox.getSelectionModel().getSelectedItem(), loginField.getText(),
							passwordField.getText())));
		});
		deleteAccountButton.setOnAction(e ->
		{
			if (passwordField.getText().equals(passwordRepeatField.getText()))
				Main.sendToServer(new AccountPacket("deleteAccount", AccountsPart.account.get(),
						new Account(accountTypeCombobox.getSelectionModel().getSelectedItem(), loginField.getText(),
								passwordField.getText())));
			else FXDialogsGenerator.showFXDialog((Stage) null, Main.msgSys.getMsg("passwordsNotMatch"), null, true);
		});
		signOutButton.setOnAction(e ->
		{
			Main.sendToServer(new AccountPacket("signOut", AccountsPart.account.get(), null));
		});
		saveButton.setOnAction(e ->
		{
			if (passwordField.getText().equals(passwordRepeatField.getText()))
			{
				Account account = new Account(accountTypeCombobox.getSelectionModel().getSelectedItem(),
						loginField.getText(), passwordField.getText());
				account.setSurname(surnameField.getText());
				account.setName(nameField.getText());
				account.setSecondName(secondNameField.getText());
				account.setCountry(countryField.getText());
				account.setRegion(regionField.getText());
				account.setCity(cityField.getText());
				account.setSchool(schoolField.getText());
				account.getSubjects().clear();
				for (Subject subject : selectedsubjectsCombobox)
					account.getSubjects().add(subject);

				account.setMaritalStatus(maritalStatusField.getText());
				setAll(account.getGrandParents(),sToR(grandParentsCombobox.getItems()));
				setAll(account.getParents(),sToR(parentsCombobox.getItems()));
				setAll(account.getChildren(),sToR(childrenCombobox.getItems()));
				setAll(account.getGrandChildren(),sToR(grandChildrenCombobox.getItems()));
				setAll(account.getSiblings(),sToR(siblingsCombobox.getItems()));
				setAll(account.getExSpouses(),sToR(exSpousesCombobox.getItems()));
				account.setSpouse(sToR(spouseField.getText()));
				setAll(account.getOtherRelatives(),sToR(otherRelativesCombobox.getItems()));

				setAll(account.getPhoneNumbers(),phoneNumbersCombobox.getItems());
				setAll(account.getEmails(),emailsCombobox.getItems());
				setAll(account.getPersonalSites(),personalSitesCombobox.getItems());
				setAll(account.getOtherSites(),otherSitesCombobox.getItems());
				setAll(account.getOtherContacts(),otherContactsCombobox.getItems());

				account.setAge(ageField.getText());
				account.setGender(genderCombobox.getSelectionModel().getSelectedItem());
				setAll(account.getMainLanguages(),mainLanguagesCombobox.getItems());
				setAll(account.getOtherLanguages(),otherLanguagesCombobox.getItems());
				account.setEducation(educationArea.getText());
				account.setCarriere(careerArea.getText());

				account.setAboutAlhogol(aboutAlhogolField.getText());
				account.setAboutNarcotics(aboutNarcoticsField.getText());
				account.setAboutSmoking(aboutSmokingField.getText());
				account.setIdeas(ideasField.getText());
				account.setInterests(interestsField.getText());
				account.setFavouriteBlogs(favouriteBlogsField.getText());
				account.setFavouriteBooks(favouriteBooksField.getText());
				account.setFavouriteComputerGames(favouriteComputerGamesField.getText());
				account.setFavouriteFilms(favouriteFilmsField.getText());
				account.setFavouriteGames(favouriteGamesField.getText());
				account.setFavouriteMusic(favouriteMusicField.getText());
				account.setFavouritePeople(favouritePeopleField.getText());
				account.setFavouriteShows(favouriteShowsField.getText());
				account.setFavouriteQuotes(favouriteQuotesField.getText());
				account.setMainInLife(mainInLifeField.getText());
				account.setMainInPeople(mainInPeopleField.getText());
				account.setWorldOutlook(worldOutlookField.getText());
				account.setOtherViews(otherViewsField.getText());
				account.setPoliticalViews(politicalViewsField.getText());
				account.setInspiration(inspirationField.getText());

				account.setBiografy(biografyArea.getText());
				account.setHomeCountry(homeCountryField.getText());
				account.setHomeRegion(homeRegionField.getText());
				account.setHomeCity(homeCityField.getText());
				Main.sendToServer(new AccountPacket("changeProfileInfo", account, account));
			}
			else FXDialogsGenerator.showFXDialog((Stage) null, Main.msgSys.getMsg("passwordsNotMatch"), null, true);
		});
		changePasswordButton.setOnAction(e ->
		{
			if (newPasswordField.getText().equals(newPasswordRepeatField.getText())
					&& passwordField.getText().equals(passwordRepeatField.getText()))
				Main.sendToServer(new AccountPacket("changeSecurityInfo", AccountsPart.account.get(),
						new Account(accountTypeCombobox.getSelectionModel().getSelectedItem(), loginField.getText(),
								newPasswordField.getText())));
			else FXDialogsGenerator.showFXDialog((Stage) null, Main.msgSys.getMsg("passwordsNotMatch"), null, true);
		});

		securityTab.setOnSelectionChanged(e -> AccountsPart.instance.resize());
		mainTab.setOnSelectionChanged(e -> AccountsPart.instance.resize());
		contactsTab.setOnSelectionChanged(e -> AccountsPart.instance.resize());
		familyTab.setOnSelectionChanged(e -> AccountsPart.instance.resize());
		ideasTab.setOnSelectionChanged(e -> AccountsPart.instance.resize());
		aboutYouTab.setOnSelectionChanged(e -> AccountsPart.instance.resize());
		lifeTab.setOnSelectionChanged(e -> AccountsPart.instance.resize());

		signInTab.setOnSelectionChanged(e -> handleTabSelect());
		signUpTab.setOnSelectionChanged(e -> handleTabSelect());
		profileTab.setOnSelectionChanged(e -> handleTabSelect());
		signOutTab.setOnSelectionChanged(e -> handleTabSelect());
		deleteAccountTab.setOnSelectionChanged(e -> handleTabSelect());
		changeVisibleTabs(AccountsPart.account.get());
	}

	private <E> void setAll(Collection<E> es, Collection<E> es2)
	{
		es.clear();
		es.addAll(es2);
	}

	public void resize(int w, int h)
	{
		loginField.setLayoutX(w / 2 - loginField.getWidth() / 2);
		passwordField.setLayoutX(w / 2 - passwordField.getWidth() / 2);
		passwordRepeatField.setLayoutX(w / 2 - passwordRepeatField.getWidth() / 2);
		accountTypeCombobox.setLayoutX(w / 2 + loginField.getWidth() / 2 + Main.spaceBetweenComponents);

		tabPane.setPrefWidth(w);
		profileTabs.setPrefWidth(w);

		signInButton.setLayoutX(w / 2 - signInButton.getWidth() / 2);
		signOutButton.setLayoutX(w / 2 - signOutButton.getWidth() / 2);
		signUpButton.setLayoutX(w / 2 - signUpButton.getWidth() / 2);
		deleteAccountButton.setLayoutX(w / 2 - deleteAccountButton.getWidth() / 2);

		{
			newPasswordField.setLayoutX(w / 2 - newPasswordField.getWidth() / 2);
			newPasswordRepeatField.setLayoutX(w / 2 - newPasswordRepeatField.getWidth() / 2);
			changePasswordButton.setLayoutX(w / 2 - changePasswordButton.getWidth() / 2);
		}

		profileMainFields.setLayoutX(w / 2 - profileMainFields.getWidth() / 2);
		familyFields.setLayoutX(w / 2 - familyFields.getWidth() / 2);
		lifeFields.setLayoutX(w / 2 - lifeFields.getWidth() / 2);
		lifeFields2.setLayoutX(w / 2 - lifeFields2.getWidth() / 2);
		ideasFields.setLayoutX(w / 2 - ideasFields.getWidth() / 2);
		{
			ideasScrollpane.setPrefWidth(w);
		}
		aboutYouFields.setLayoutX(w / 2 - aboutYouFields.getWidth() / 2);
		contactsFields.setLayoutX(w / 2 - contactsFields.getWidth() / 2);

		// TODO расширение скрол пейна у идеас фиелдс в высоту пока может и в ширину
		// полностью

		saveButton.setLayoutX(w / 2 - saveButton.getWidth() / 2);

		loginField.setLayoutY(Main.spaceBetweenComponents);
		accountTypeCombobox.setLayoutY(Main.spaceBetweenComponents);
		passwordField.setLayoutY(Main.spaceBetweenComponents + loginField.getLayoutY() + loginField.getHeight());
		passwordRepeatField
				.setLayoutY(Main.spaceBetweenComponents + passwordField.getLayoutY() + passwordField.getHeight());
		tabPane.setLayoutY(
				Main.spaceBetweenComponents + passwordRepeatField.getLayoutY() + passwordRepeatField.getHeight());

		tabPane.setPrefHeight(h - tabPane.getLayoutY());
		saveButton.setLayoutY(tabPane.sceneToLocal(0, h).getY() - saveButton.getHeight() - Main.spaceBetweenComponents);
		tabPane.setPrefHeight(h - tabPane.getLayoutY());
		profileTabs.setPrefHeight(saveButton.getLayoutY() - Main.spaceBetweenComponents);
		ideasFields.setLayoutY(0);
		if (ideasFields.getParent() instanceof Region)
			((Region) ideasFields.getParent()).setPrefHeight(ideasFields.getHeight());
		ideasScrollpane.setLayoutY(0);
		ideasScrollpane.setPrefHeight(
				Math.min(profileTabs.getPrefHeight() - Main.tabsSwitchHeight, ideasFields.getHeight() + 2));
	}

	CheckMenuItem showAllTabsItem;

	@FXML
	public void initialize()
	{
		instance = this;
		profileTabs.setContextMenu(new ContextMenu(showAllTabsItem = new CheckMenuItem()));
		showAllTabsItem.setOnAction(e ->
		{
			if (showAllTabsItem.isSelected())
			{
				if (!profileTabs.getTabs().contains(familyTab))
					profileTabs.getTabs().add(familyTab);
				if (!profileTabs.getTabs().contains(contactsTab))
					profileTabs.getTabs().add(contactsTab);
				if (!profileTabs.getTabs().contains(lifeTab))
					profileTabs.getTabs().add(lifeTab);
				if (!profileTabs.getTabs().contains(ideasTab))
					profileTabs.getTabs().add(ideasTab);
				if (!profileTabs.getTabs().contains(aboutYouTab))
					profileTabs.getTabs().add(aboutYouTab);
			}
			else
			{
				if (profileTabs.getTabs().contains(familyTab))
					profileTabs.getTabs().remove(familyTab);
				if (profileTabs.getTabs().contains(contactsTab))
					profileTabs.getTabs().remove(contactsTab);
				if (profileTabs.getTabs().contains(lifeTab))
					profileTabs.getTabs().remove(lifeTab);
				if (profileTabs.getTabs().contains(ideasTab))
					profileTabs.getTabs().remove(ideasTab);
				if (profileTabs.getTabs().contains(aboutYouTab))
					profileTabs.getTabs().remove(aboutYouTab);
			}
		});
		showAllTabsItem.setSelected(false);
		showAllTabsItem.fire();
		subjectsCombobox.getItems().clear();
		for (Subject subject : Subject.values())
			subjectsCombobox.getItems().add(new MenuItem("   \t" + subject.name()));
		genderCombobox.getItems().add("Man");
		genderCombobox.getItems().add("Woman");
		accountTypeCombobox.getItems().clear();
		for (AccountType accountType : AccountType.values())
			accountTypeCombobox.getItems().add(accountType);
		accountTypeCombobox.getSelectionModel().select(AccountType.Student);

		int w1 = 190, h1 = 30;
		parentsCombobox = new ComboboxWithAdd();
		familyFields.add(parentsCombobox, 1, 1);
		parentsCombobox.setPrefSize(w1, h1);
		grandParentsCombobox = new ComboboxWithAdd();
		familyFields.add(grandParentsCombobox, 1, 2);
		grandParentsCombobox.setPrefSize(w1, h1);
		childrenCombobox = new ComboboxWithAdd();
		familyFields.add(childrenCombobox, 1, 3);
		childrenCombobox.setPrefSize(w1, h1);
		grandChildrenCombobox = new ComboboxWithAdd();
		familyFields.add(grandChildrenCombobox, 1, 4);
		grandChildrenCombobox.setPrefSize(w1, h1);
		siblingsCombobox = new ComboboxWithAdd();
		familyFields.add(siblingsCombobox, 1, 5);
		siblingsCombobox.setPrefSize(w1, h1);
		exSpousesCombobox = new ComboboxWithAdd();
		familyFields.add(exSpousesCombobox, 1, 6);
		exSpousesCombobox.setPrefSize(w1, h1);
		otherRelativesCombobox = new ComboboxWithAdd();
		familyFields.add(otherRelativesCombobox, 1, 8);
		otherRelativesCombobox.setPrefSize(w1, h1);

		int w2 = 125, h2 = 30;
		mainLanguagesCombobox = new ComboboxWithAdd();
		lifeFields.add(mainLanguagesCombobox, 1, 2);
		mainLanguagesCombobox.setPrefSize(w2, h2);
		otherLanguagesCombobox = new ComboboxWithAdd();
		lifeFields.add(otherLanguagesCombobox, 1, 3);
		otherLanguagesCombobox.setPrefSize(w2, h2);

		int w3 = 130, h3 = 30;
		emailsCombobox = new ComboboxWithAdd();
		contactsFields.add(emailsCombobox, 1, 0);
		emailsCombobox.setPrefSize(w3, h3);
		phoneNumbersCombobox = new ComboboxWithAdd();
		contactsFields.add(phoneNumbersCombobox, 1, 1);
		phoneNumbersCombobox.setPrefSize(w3, h3);
		personalSitesCombobox = new ComboboxWithAdd();
		contactsFields.add(personalSitesCombobox, 1, 2);
		personalSitesCombobox.setPrefSize(w3, h3);
		otherSitesCombobox = new ComboboxWithAdd();
		contactsFields.add(otherSitesCombobox, 1, 3);
		otherSitesCombobox.setPrefSize(w3, h3);
		otherContactsCombobox = new ComboboxWithAdd();
		contactsFields.add(otherContactsCombobox, 1, 4);
		otherContactsCombobox.setPrefSize(w3, h3);
	}

	public void handleTabSelect()
	{
		if (signUpTab.isSelected())
		{
			loginField.setDisable(false);
			accountTypeCombobox.setDisable(false);
			accountTypeCombobox.setVisible(true);
			passwordRepeatField.setVisible(true);
		}
		if (signInTab.isSelected())
		{
			loginField.setDisable(false);
			accountTypeCombobox.setDisable(true);
			accountTypeCombobox.setVisible(false);
			passwordRepeatField.setVisible(false);
		}
		if (deleteAccountTab.isSelected())
		{
			loginField.setDisable(false);
			accountTypeCombobox.setDisable(true);
			accountTypeCombobox.setVisible(true);
			passwordRepeatField.setVisible(true);
		}
		if (signOutTab.isSelected())
		{
			loginField.setDisable(true);
			accountTypeCombobox.setDisable(true);
			accountTypeCombobox.setVisible(true);
			passwordRepeatField.setVisible(false);
		}
		if (profileTab.isSelected())
		{
			loginField.setDisable(true);
			accountTypeCombobox.setDisable(true);
			accountTypeCombobox.setVisible(true);
			passwordRepeatField.setVisible(true);
		}
		AccountsPart.instance.resize();
		passwordRepeatField.setText("");
	}

	public void changeVisibleTabs(Account account)
	{
		ObservableList<Tab> tabs = tabPane.getTabs();
		changeTabVisibility(tabs, account != null, profileTab);
		changeTabVisibility(tabs, account != null, signOutTab);
		changeTabVisibility(tabs, account != null, deleteAccountTab);
		changeTabVisibility(tabs, account == null, signInTab);
		changeTabVisibility(tabs, account == null, signUpTab);
		tabPane.getSelectionModel().selectFirst();
		handleTabSelect();
	}

	public void changeTabVisibility(ObservableList<Tab> tabs, boolean visible, Tab tab)
	{
		if (visible && !tabs.contains(tab))
			tabs.add(tab);
		if (!visible && tabs.contains(tab))
			tabs.remove(tab);

	}

	public void fillFieldsOfAccount(Account account)
	{
		surnameField.setText(account.getSurname());
		nameField.setText(account.getName());
		secondNameField.setText(account.getSecondName());
		countryField.setText(account.getCountry());
		regionField.setText(account.getRegion());
		cityField.setText(account.getCity());
		schoolField.setText(account.getSchool());
		selectedsubjectsCombobox.clear();
		for (Subject subject : account.getSubjects())
			selectedsubjectsCombobox.add(subject);
		updateSelectedsubjectsCombobox();

		maritalStatusField.setText(account.getMaritalStatus());
		setAll(grandParentsCombobox.getItems(),rToS(account.getGrandParents()));
		setAll(parentsCombobox.getItems(),rToS(account.getParents()));
		setAll(childrenCombobox.getItems(),rToS(account.getChildren()));
		setAll(grandChildrenCombobox.getItems(),rToS(account.getGrandChildren()));
		setAll(siblingsCombobox.getItems(),rToS(account.getSiblings()));
		setAll(exSpousesCombobox.getItems(),rToS(account.getExSpouses()));
		spouseField.setText(rToS(account.getSpouse()));
		setAll(otherRelativesCombobox.getItems(),rToS(account.getOtherRelatives()));

		setAll(phoneNumbersCombobox.getItems(),account.getPhoneNumbers());
		setAll(emailsCombobox.getItems(),account.getEmails());
		setAll(personalSitesCombobox.getItems(),account.getPersonalSites());
		setAll(otherSitesCombobox.getItems(),account.getOtherSites());
		setAll(otherContactsCombobox.getItems(),account.getOtherContacts());

		ageField.setText(account.getAge());
		genderCombobox.getSelectionModel().select(account.getGender());
		setAll(mainLanguagesCombobox.getItems(),account.getMainLanguages());
		setAll(otherLanguagesCombobox.getItems(),account.getOtherLanguages());
		educationArea.setText(account.getEducation());
		careerArea.setText(account.getCarriere());

		worldOutlookField.setText(account.getWorldOutlook());
		politicalViewsField.setText(account.getPoliticalViews());
		mainInLifeField.setText(account.getMainInLife());
		mainInPeopleField.setText(account.getMainInPeople());
		aboutSmokingField.setText(account.getAboutSmoking());
		aboutAlhogolField.setText(account.getAboutAlhogol());
		aboutNarcoticsField.setText(account.getAboutNarcotics());
		interestsField.setText(account.getInterests());
		favouriteMusicField.setText(account.getFavouriteMusic());
		favouriteFilmsField.setText(account.getFavouriteFilms());
		favouriteShowsField.setText(account.getFavouriteShows());
		favouriteBlogsField.setText(account.getFavouriteBlogs());
		favouriteBooksField.setText(account.getFavouriteBooks());
		favouriteGamesField.setText(account.getFavouriteGames());
		favouriteComputerGamesField.setText(account.getFavouriteComputerGames());
		favouritePeopleField.setText(account.getFavouritePeople());
		inspirationField.setText(account.getInspiration());
		favouriteQuotesField.setText(account.getFavouriteQuotes());
		ideasField.setText(account.getIdeas());
		otherViewsField.setText(account.getOtherViews());

		biografyArea.setText(account.getBiografy());
		homeCountryField.setText(account.getHomeCountry());
		homeRegionField.setText(account.getHomeRegion());
		homeCityField.setText(account.getHomeCity());
	}

	private String rToS(Rodstvennik r)
	{
		return r != null ? r.getLogin() + "," + r.getSurname() + "," + r.getName() + "," + r.getSecondName() : "";
	}

	private ArrayList<String> rToS(Collection<Rodstvennik> rs)
	{
		ArrayList<String> ss = new ArrayList<String>();
		for (Rodstvennik r : rs)
			ss.add(rToS(r));
		return ss;
	}

	private ArrayList<Rodstvennik> sToR(Collection<String> ss)
	{
		ArrayList<Rodstvennik> rs = new ArrayList<Rodstvennik>();
		for (String s : ss)
			rs.add(sToR(s));
		return rs;
	}

	private Rodstvennik sToR(String s)
	{
		return new Rodstvennik((s + ", , , ").split(",")[0], (s + ", , , ").split(",")[1], (s + ", , , ").split(",")[2],
				(s + ", , , ").split(",")[3]);
	}

	int offset = 4;

	void updateSelectedsubjectsCombobox()
	{
		MenuButton subjectsCombobox = this.subjectsCombobox;
		ArrayList<Subject> selectedsubjectsCombobox = this.selectedsubjectsCombobox;
		String s = "";
		for (int i = 0; i < subjectsCombobox.getItems().size(); i++)
			for (int j = 0; j < selectedsubjectsCombobox.size(); j++)
				if (subjectsCombobox.getItems().get(i).getText().substring(offset)
						.equals(selectedsubjectsCombobox.get(j).name()))
					s += (s != "" ? "; " : "") + selectedsubjectsCombobox.get(j);
		for (MenuItem item2 : subjectsCombobox.getItems())
			item2.setText(
					(selectedsubjectsCombobox.contains(Subject.valueOf(item2.getText().substring(offset))) ? "  ✔\t"
							: "   \t") + item2.getText().substring(offset));
		subjectsCombobox.setText(s);
	}
}
