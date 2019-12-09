package de.bp2019.zentraldatei.view;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.BinderValidationStatus;
import com.vaadin.flow.data.binder.BindingValidationStatus;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.NotFoundException;
import com.vaadin.flow.router.Route;
import de.bp2019.zentraldatei.model.ExerciseScheme;
import de.bp2019.zentraldatei.service.ExerciseSchemeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author Luca Dinies
 *
**/

@Route(value = "exerciseScheme", layout = MainAppView.class)

public class ExerciseSchemesView extends Div implements HasUrlParameter<String> {

	private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(ExerciseSchemesView.class);

    private Binder<ExerciseScheme> binder;

    /* Binder to bind the form Data to an Object */
    private ExerciseSchemeService exerciseSchemeService;

    /*
     * set if a new MoudleScheme is being created, not set if an existing
     * ModuleScheme is being edited
     */
    private boolean isNewEntity;

    public ExerciseSchemesView() {

        LOGGER.debug("Started creation of ExerciseSchemeView");

        this.exerciseSchemeService = exerciseSchemeService;

        FormLayout form = new FormLayout();
        binder = new Binder<>();

        /*  Create the fields  */
        TextField name = new TextField("Name", "Name der Übung");
        name.setValueChangeMode(ValueChangeMode.EAGER);
        form.setWidth("40em");
        form.add(name);

        /*  TODO: Tokens  */

        Checkbox isNumeric = new Checkbox("Mit Note");
        form.add(isNumeric);

        Button save = new Button("Speichern");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        /*  Button Bar  */
        VerticalLayout actions = new VerticalLayout();
        actions.add(save);
        actions.setHorizontalComponentAlignment(FlexComponent.Alignment.END, save);
        form.add(actions);

        /*
         * Hidden TextField to bind Id, if someone knows a cleaner Solution please
         * implement it!
         */
        TextField id = new TextField("");

        /*  Binding and validation  */
        binder.bind(id, ExerciseScheme::getId, ExerciseScheme::setId);

        binder.forField(name).withValidator(new StringLengthValidator("Bitte Namen der Übung eingeben", 1, null))
                .bind(ExerciseScheme::getName, ExerciseScheme::setName);

        binder.bind(isNumeric, ExerciseScheme::getIsNumeric, ExerciseScheme::setIsNumeric);

        /*  Click-Listeners  */
        save.addClickListener(event -> {
            ExerciseScheme formData = new ExerciseScheme();
            if (binder.writeBeanIfValid(formData)) {
                Dialog dialog = new Dialog();
                if (isNewEntity) {
                    exerciseSchemeService.saveModuleScheme(formData);
                    dialog.add(new Text("Übung erfolgreich erstellt"));
                } else {
                    exerciseSchemeService.updateModuleScheme(formData);
                    dialog.add(new Text("Übung erfolgreich bearbeitet"));
                }
                UI.getCurrent().navigate("exerciseSchemes");
                dialog.open();
            } else {
                BinderValidationStatus<ExerciseScheme> validate = binder.validate();
                String errorText = validate.getFieldValidationStatuses().stream()
                        .filter(BindingValidationStatus::isError)
                        .map(BindingValidationStatus::getMessage).map(Optional::get).distinct()
                        .collect(Collectors.joining(", "));
                LOGGER.debug("There are errors: " + errorText);
            }
        });

        /*  Add Layout to Component  */

        add(form);
        LOGGER.debug("Finished creation of ManageExerciseSchemesView");

    }

    @Override
    public void setParameter(BeforeEvent event, String exerciseSchemeId) {
        if (exerciseSchemeId.equals("new")) {
            isNewEntity = true;
            /* clear fields by setting null */
            binder.readBean(null);
        } else {
            ExerciseScheme fetchedExerciseScheme = exerciseSchemeService.getExerciseSchemeById(exerciseSchemeId);
            /* getExerciseSchemeById returns null if no matching ExerciseScheme is found */
            if (fetchedExerciseScheme == null) {
                throw new NotFoundException();
            } else {
                isNewEntity = false;
                binder.readBean(fetchedExerciseScheme);
            }
        }
    }

}
