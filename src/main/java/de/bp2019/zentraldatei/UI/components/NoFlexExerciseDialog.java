package de.bp2019.zentraldatei.ui.components;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToLongConverter;
import com.vaadin.flow.data.validator.StringLengthValidator;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.bp2019.zentraldatei.model.Exercise;
import de.bp2019.zentraldatei.model.Grade;
import de.bp2019.zentraldatei.model.Token;
import de.bp2019.zentraldatei.model.Module;
import de.bp2019.zentraldatei.service.ExerciseSchemeService;
import de.bp2019.zentraldatei.service.GradeService;
import de.bp2019.zentraldatei.service.ModuleService;


/**
 * Creates a dialog window to start an exercise for one student.
 *
 * @author Luca Dinies
 */
public class NoFlexExerciseDialog {

    private static final long serialVersionUID = 254687622689916454L;

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenEditor.class);

    private ObjectId objectId;

    Binder<Grade> binder;

    @Autowired
    public NoFlexExerciseDialog(ModuleService moduleService, ExerciseSchemeService exerciseSchemeService, GradeService gradeService) {

        Dialog dialog = new Dialog();
        dialog.setCloseOnOutsideClick(false);

        binder = new Binder<>();

        /* ########### Create the Fields ########### */

        FormLayout form = new FormLayout();
        form.setResponsiveSteps(new FormLayout.ResponsiveStep("5em", 1),
                new FormLayout.ResponsiveStep("5em", 2));
        form.setWidth("100%");
        form.getStyle().set("marginLeft", "1em");
        form.getStyle().set("marginTop", "-0.5em");

        TextField matrikelNum = new TextField();
        matrikelNum.setPlaceholder("Matrikel Nummer");
        matrikelNum.setLabel("Matrikel Nummer");
        form.add(matrikelNum);

        Select<Module> moduleSelect = new Select<>();
        moduleSelect.setItemLabelGenerator(Module::getName);
        List<Module> allModules = moduleService.getAllModules();
        moduleSelect.setItems(allModules);
        moduleSelect.setPlaceholder("Modul");
        moduleSelect.setLabel("Modul");
        form.add(moduleSelect);

        DatePicker datePicker = new DatePicker();
        datePicker.setLabel("Abgabe-Datum");
        datePicker.setValue(LocalDate.now());
        datePicker.setVisible(true);
        form.add(datePicker);

        Select<Exercise> exerciseSelect = new Select<>();
        exerciseSelect.setItemLabelGenerator(Exercise::getName);
        exerciseSelect.setEnabled(false);
        exerciseSelect.setLabel("Übung");
        form.add(exerciseSelect);

        TextField gradeField = new TextField();
        gradeField.setLabel("Note");
        gradeField.setPlaceholder("Note");
        form.add(gradeField);

        Select<Token> tokenSelect = new Select<>();
        tokenSelect.setItemLabelGenerator(Token::getName);
        tokenSelect.setEnabled(false);
        tokenSelect.setLabel("Token");
        form.add(tokenSelect);

        /* ########### Change Listeners for Selects ########### */

        moduleSelect.addValueChangeListener(event -> {
            Module selectedModule = moduleSelect.getValue();
            List<Exercise> exercises = selectedModule.getExercises();
            exerciseSelect.setItems(exercises);
            exerciseSelect.setEnabled(true);
            exerciseSelect.setValue(exercises.get(0));
        });

        exerciseSelect.addValueChangeListener(event -> {
            if(!exerciseSelect.isEmpty()) {
                Exercise selectedExercise = exerciseSelect.getValue();
                Set<Token> token = selectedExercise.getScheme().getTokens();
                tokenSelect.setItems(token);
                tokenSelect.setEnabled(true);

                if (selectedExercise.getScheme().getIsNumeric()) {
                    tokenSelect.setEnabled(false);
                    gradeField.setEnabled(true);
                } else {
                    tokenSelect.setEnabled(true);
                    gradeField.setEnabled(false);
                }
            }
        });

        /* ########### Button Bar ########### */

        HorizontalLayout buttonBar = new HorizontalLayout();

        Button cancel = new Button();
        cancel.setText("Abbruch");
        buttonBar.add(cancel);

        Button save = new Button();
        save.setText("Speichern");
        buttonBar.add(save);
        form.add(buttonBar);
        dialog.add(form);
        dialog.open();

        /* ########### Data Binding and validation ########### */

        //TODO: Validator
        binder.forField(matrikelNum).withValidator(new StringLengthValidator("Bitte Matrikelnummer eingeben", 1, null))
                .withConverter(new StringToLongConverter("Bitte eine Zahl eingeben!"))
                        .bind(Grade::getMatrNumber, Grade::setMatrNumber);

        binder.bind(moduleSelect, Grade::getModule, Grade::setModule);

        binder.bind(exerciseSelect, Grade::getExercise,Grade::setExercise);

        binder.bind(gradeField, Grade::getGrade, Grade::setGrade);

        /* ########### Click Listeners for Buttons ########### */

        cancel.addClickListener(event1 -> {
            dialog.close();
        });

        save.addClickListener(event -> {
            Grade grade = new Grade();
            if (binder.writeBeanIfValid(grade)) {
                if(objectId != null){
                    grade.setId(objectId);
                } try {
                    gradeService.save(grade);
                    dialog.close();
                } finally {
                    // TODO: implement ErrorHandling
                }
            }
        });

    }

}