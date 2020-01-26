package de.bp2019.zentraldatei.UI.views.User;

import java.util.Optional;
import java.util.stream.Collectors;

//import org.apache.catalina.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.gatanaso.MultiselectComboBox;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import de.bp2019.zentraldatei.UI.views.BaseView;
import de.bp2019.zentraldatei.UI.views.MainAppView;
import de.bp2019.zentraldatei.enums.UserType;
import de.bp2019.zentraldatei.model.Institute;
import de.bp2019.zentraldatei.model.User;
import de.bp2019.zentraldatei.service.InstituteService;
import de.bp2019.zentraldatei.service.UserService;

/**
 * View that edits User
 * 
 * @author Fabio Pereira da Costa
 */

@PageTitle("Zentraldatei | Benutzer bearbeiten")
@Route(value = EditUserView.ROUTE, layout = MainAppView.class)
public class EditUserView extends BaseView implements HasUrlParameter<String> {

	private static final long serialVersionUID = 1L;
	
	public static final String ROUTE = "edit-user-view";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EditUserView.class);

	/*
	 * no @Autowire because service is injected by constructor. Vaadin likes it
	 * better this way...
	 */
	private UserService userService;

	/** Binder to bind the form Data to an Object */
	private Binder<User> userBinder;

	/**
	 * set if a new MoudleScheme is being created, not set if an existing
	 * ModuleScheme is being edited
	 */
	private boolean isNewEntity;

	@Autowired
	public EditUserView(UserService userService, InstituteService instituteService) {
		super("neuer Benutzer");

		LOGGER.debug("Started creation of UserView");

		FormLayout userformwithbinder = new FormLayout();
		userformwithbinder.setResponsiveSteps(new ResponsiveStep("5em", 1), new ResponsiveStep("5em", 2));
		userformwithbinder.setWidth("30em");
		userformwithbinder.getStyle().set("marginLeft", "3em");

		userBinder = new Binder<>();

		/*
		 * Creating the fields to put in data
		 */

		TextField firstname = new TextField();
		firstname.setLabel("Name");
		firstname.setPlaceholder("Bitte Name eingeben");
		firstname.setValueChangeMode(ValueChangeMode.EAGER);
		userformwithbinder.add(firstname);

		TextField lastname = new TextField();
		lastname.setLabel("Vorname");
		lastname.setPlaceholder("Bitte Vornamen eingeben");
		lastname.setValueChangeMode(ValueChangeMode.EAGER);
		userformwithbinder.add(lastname);

		EmailField email = new EmailField("Email");
		email.setClearButtonVisible(true);
		email.setErrorMessage("Bitte gültige Email eingeben");
		userformwithbinder.add(email);
		
		MultiselectComboBox<Institute> institutes = new MultiselectComboBox<Institute>();
		institutes.setLabel("Institute");
		institutes.setItems(instituteService.getAllInstitutes());
		institutes.setItemLabelGenerator(Institute::getName);
		userformwithbinder.add(institutes);

		PasswordField password = new PasswordField();
		password.setLabel("Password");
		password.setPlaceholder("Enter password");
		password.setValue("Password");
		userformwithbinder.add(password);

		ComboBox<UserType> userType = new ComboBox<UserType>();
		userType.setItems(UserType.ADMIN, UserType.HIWI, UserType.SUPERADMIN, UserType.WIMI);
		userType.setValue(UserType.HIWI);
		userformwithbinder.add(userType);

		// adding elements to layout
		userformwithbinder.add(firstname);
		userformwithbinder.add(lastname);
		userformwithbinder.add(email);
		userformwithbinder.add(institutes);
		userformwithbinder.add(password);
		userformwithbinder.add(userType);

		// creating Buttons
		Button save = new Button("Speichern");
		
		HorizontalLayout actions = new HorizontalLayout();
		actions.add(save);
		save.getStyle().set("marginRight", "10em");
		userformwithbinder.add(actions);

		
		// Data Binding and Validation
		userBinder.forField(firstname).withValidator(new StringLengthValidator("Bitte einen Namen eingeben", 1, null))
				.bind(User::getFirstName, User::setFirstName);

		userBinder.forField(lastname).withValidator(new StringLengthValidator("Bitte einen Vornamen einbegen", 1, null))
				.bind(User::getLastName, User::setLastName);

		userBinder.forField(email).bind(User::getEMail, User::setEMail);

		userBinder.forField(institutes).withValidator(selectedInstitutes -> !selectedInstitutes.isEmpty(),
				"Bitte mindestens ein Institut angeben").bind(User::getInstitutes, User::setInstitutes);

		userBinder.forField(password)
				.withValidator(new StringLengthValidator("Bitte mindestens 4 Zeichen angeben", 4, null))
				.bind(User::getPassword, User::setPassword);

		userBinder.bind(userType, User::getType, User::setType);

		
		// Click Listeners for Buttons
		save.addClickListener(event -> {
			User formData = new User();
			if (userBinder.writeBeanIfValid(formData)) {
				Dialog dialog = new Dialog();
				if (isNewEntity) {
					userService.saveUser(formData);
					dialog.add(new Text("Neuen User erstellt..."));
				}
				UI.getCurrent().navigate(ManageUserView.ROUTE);
                dialog.open();
			} else {
				BinderValidationStatus<User> validate = userBinder.validate();
				String errorText = validate.getFieldValidationStatuses().stream()
						.filter(BindingValidationStatus::isError).map(BindingValidationStatus::getMessage)
						.map(Optional::get).distinct().collect(Collectors.joining(", "));
				LOGGER.debug("There are errors: " + errorText);
			}

		});

		add(userformwithbinder);
		LOGGER.debug("Finished creation Userview");

	}

	@Override
	public void setParameter(BeforeEvent event, String userId) {
		if (userId.equals("new")) {
			isNewEntity = true;
			/* clear fields by setting null */
			userBinder.readBean(null);
		} else {
			Optional<User> fetchedUser = userService.getUserID(userId);
			/* getUserById returns null if no matching ModuleScheme is found */
			if (!fetchedUser.isPresent()) {
				throw new NotFoundException();
			} else {
				isNewEntity = false;
				userBinder.readBean(fetchedUser.get());
			}
		}
	}

}