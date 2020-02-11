package de.bp2019.pusl.ui.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;

import de.bp2019.pusl.model.Exercise;
import de.bp2019.pusl.model.Lecture;
import de.bp2019.pusl.ui.components.VerticalTabs;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;

import de.bp2019.pusl.config.AppConfig;
import de.bp2019.pusl.model.Grade;
import de.bp2019.pusl.service.GradeService;
import de.bp2019.pusl.service.LectureService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Luca Dinies
 *
 **/

@PageTitle(AppConfig.NAME + " | Noten eintragen")
@Route(value = WorkView.ROUTE, layout = MainAppView.class)
public class WorkView extends BaseView implements HasUrlParameter<String> {

    private static final long serialVersionUID = 1L;

    public static final String ROUTE = "grades";

    private ListDataProvider<Grade> gradeDataProvider;

    private GradeService gradeService;

    private ObjectId objectId;

    private Binder<Grade> binder;

    @Autowired
    private LectureService lectureService;

    private Select<Lecture> lectureFilter;
    private Select<Exercise> exerciseFilter;
    private TextField martrNumberFilter;

    private Map<String, List<String>> parametersMap;

    /** Filter for the Database Query, lookup Spring Data Query by Example! */
    private Grade filter;

    @Autowired
    public WorkView(GradeService gradeService, LectureService lectureService) {
        super("Noten eintragen");
        LOGGER.debug("Started creation of WorkView");

        this.gradeService = gradeService;

        gradeDataProvider = new ListDataProvider<Grade>(gradeService.getAll());

        filter = new Grade();;

        Lecture filterCleanModule = new Lecture("Alle Anzeigen", null, null, null, null);
        Exercise filterCleanExercise = new Exercise("Alle Anzeigen", null, false);

        VerticalTabs verticalTabs = new VerticalTabs();
        verticalTabs.setHeight("100%");
        verticalTabs.setWidth("120%");

        VerticalLayout gridAndFilter = new VerticalLayout();

        /* ########### Create the filter Fields ########### */

        HorizontalLayout filterLayout = new HorizontalLayout();
        filterLayout.setWidth("100%");
        filterLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        VerticalLayout martrGradeLayout = new VerticalLayout();

        martrNumberFilter = new TextField();
        martrNumberFilter.setLabel("Matrikelnummer");
        martrNumberFilter.setPlaceholder("Matrikelnummer");
        martrNumberFilter.setValueChangeMode(ValueChangeMode.EAGER);
        martrGradeLayout.add(martrNumberFilter);

        TextField gradeFilter = new TextField();
        gradeFilter.setLabel("Note");
        gradeFilter.setPlaceholder("Note");
        gradeFilter.setValueChangeMode(ValueChangeMode.EAGER);
        martrGradeLayout.add(gradeFilter);

        filterLayout.add(martrGradeLayout);

        VerticalLayout moduleExerciseLayout = new VerticalLayout();

        lectureFilter = new Select<>();
        lectureFilter.setItemLabelGenerator(Lecture::getName);
        List<Lecture> allLectures = lectureService.getAll();
        allLectures.add(0, filterCleanModule);
        lectureFilter.setItems(allLectures);
        lectureFilter.setValue(allLectures.get(0));
        lectureFilter.setLabel("Modul");
        moduleExerciseLayout.add(lectureFilter);

        exerciseFilter = new Select<>();
        exerciseFilter.setItemLabelGenerator(Exercise::getName);
        exerciseFilter.setEnabled(false);
        exerciseFilter.setLabel("Übung");

        exerciseFilter.setValue(filterCleanExercise);
        moduleExerciseLayout.add(exerciseFilter);

        filterLayout.add(moduleExerciseLayout);

        VerticalLayout dateLayout = new VerticalLayout();

        DatePicker startDateFilter = new DatePicker();
        startDateFilter.setLabel("Start");
        dateLayout.add(startDateFilter);

        DatePicker endDateFilter = new DatePicker();
        endDateFilter.setLabel("End");
        dateLayout.add(endDateFilter);

        filterLayout.add(dateLayout);

        gridAndFilter.add(filterLayout);

        /* ########### Create the Grid ########### */

        Grid<Grade> grid = new Grid<>();

        grid.setWidth("100%");
        grid.setDataProvider(gradeDataProvider);

        grid.addColumn(Grade::getMatrNumber).setHeader("Matr. Nr.").setAutoWidth(true).setKey("matrikelNum");
        grid.addColumn(item -> item.getLecture().getName()).setHeader("Veranstaltung").setAutoWidth(true).setKey("lecture");
        grid.addColumn(item -> item.getExercise().getName()).setHeader("Übung").setAutoWidth(true).setKey("exercise");
        grid.addColumn(item -> item.getHandIn()).setHeader("Abgabedatum").setAutoWidth(true).setKey("handin");
        grid.addColumn(item -> item.getGrade()).setHeader("Note").setAutoWidth(true).setKey("grade");

        gridAndFilter.add(grid);

        verticalTabs.addTab("Alle Noten", gridAndFilter);

        /*############## CHANGE LISTENERS ############# */

        martrNumberFilter.addValueChangeListener(event -> {
            filter.setMatrNumber(event.getValue());
            reloadFilter();

        });

        gradeFilter.addValueChangeListener(event -> {
            filter.setGrade(event.getValue());
            reloadFilter();
        });

        lectureFilter.addValueChangeListener(event -> {
            if(event.getValue().getId() == null){
                filter.setLecture(null);
                reloadFilter();
                grid.getColumnByKey("lecture").setVisible(true);
                grid.getColumnByKey("exercise").setVisible(true);

                exerciseFilter.setValue(filterCleanExercise);
                exerciseFilter.setEnabled(false);

            } else {

                filter.setLecture(event.getValue());
                reloadFilter();

                grid.getColumnByKey("lecture").setVisible(false);
                grid.getColumnByKey("exercise").setVisible(true);
                exerciseFilter.setValue(filterCleanExercise);

                List<Exercise> lectureExercises = event.getValue().getExercises();

                exerciseFilter.setItems(lectureExercises);
                exerciseFilter.setEnabled(true);


            }

        });

        exerciseFilter.addValueChangeListener(event -> {
            if(!exerciseFilter.isEmpty()) {
                if (exerciseFilter.getValue().getName().contains("Alle Anzeigen")) {
                    filter.setExercise(null);
                    reloadFilter();
                } else {
                    filter.setExercise(event.getValue());
                    reloadFilter();
                    grid.getColumnByKey("exercise").setVisible(false);
                }
            }
        });

        startDateFilter.addValueChangeListener(event -> {
            LocalDate selectedDate = event.getValue();
            LocalDate endDate = endDateFilter.getValue();
            if(selectedDate != null){
                endDateFilter.setMin(selectedDate);
                gradeDataProvider.addFilter(grade -> grade.getHandIn().isAfter(startDateFilter.getValue()));
                if(endDate == null){
                    endDateFilter.setOpened(true);
                }
            } else {
                endDateFilter.setMin(null);
            }
        });

        endDateFilter.addValueChangeListener(event -> {
            LocalDate selectedDate = event.getValue();
            LocalDate startDate = startDateFilter.getValue();
            if(selectedDate != null){
                startDateFilter.setMax(selectedDate);
                gradeDataProvider.addFilter(grade -> grade.getHandIn().isBefore(endDateFilter.getValue()));
                if(startDate == null){
                    startDateFilter.setOpened(true);
                }
            } else {
                startDateFilter.setMax(null);
            }
        });


        /*############## FORM TO INPUT A NEW GRADE ############# */

        binder = new Binder<>();

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

        Select<Lecture> lectureSelect = new Select<>();
        lectureSelect.setItemLabelGenerator(Lecture::getName);
        List<Lecture> lectures = lectureService.getAll();
        lectureSelect.setItems(lectures);
        lectureSelect.setPlaceholder("Modul");
        lectureSelect.setLabel("Modul");
        form.add(lectureSelect);

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

        /* ########### Save Button and Layout ########### */

        Button save = new Button();
        save.setText("Speichern");

        VerticalLayout gradeInputLayout = new VerticalLayout();

        gradeInputLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, form);
        gradeInputLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.END, save);

        gradeInputLayout.add(form);
        gradeInputLayout.add(save);

        verticalTabs.addTab("Note eintragen", gradeInputLayout);

        add(verticalTabs);


        /* ########### Change Listeners for Selects ########### */

        lectureSelect.addValueChangeListener(event -> {
            Lecture selectedLecture = lectureSelect.getValue();
            if(event.getValue() != null){
                List<Exercise> exercises = selectedLecture.getExercises();
                exerciseSelect.setItems(exercises);
                exerciseSelect.setEnabled(true);
                exerciseSelect.setValue(exercises.get(0));
            } else {
                exerciseSelect.setEnabled(false);
                exerciseSelect.setValue(null);
            }

        });


        /* ########### Click Listeners for Buttons ########### */

        save.addClickListener(event -> {
            Grade grade = new Grade();
            if (binder.writeBeanIfValid(grade)) {
                if(objectId != null){
                    grade.setId(objectId);
                } try {
                    gradeService.save(grade);
                } finally {
                    // TODO: implement ErrorHandling
                }
            }
            reloadFilter();
            matrikelNum.clear();
            exerciseSelect.clear();
            lectureSelect.clear();
            datePicker.setValue(LocalDate.now());
            gradeField.clear();
        });


        /* ########### Data Binding and validation ########### */

        //TODO: Validator
        binder.forField(matrikelNum).withValidator(new StringLengthValidator("Bitte Matrikelnummer eingeben", 1, null))
                .bind(Grade::getMatrNumber, Grade::setMatrNumber);

        binder.bind(lectureSelect, Grade::getLecture, Grade::setLecture);

        binder.bind(exerciseSelect, Grade::getExercise,Grade::setExercise);

        binder.bind(gradeField, Grade::getGrade, Grade::setGrade);

        binder.bind(datePicker, Grade::getHandIn, Grade::setHandIn);

    }

    /**
     * Fetch new Data from database, that matches the Filter
     *
     * @author Leon Chemnitz
     */
    private void reloadFilter() {
        LOGGER.debug(filter.toString());
        gradeDataProvider.getItems().clear();
        gradeDataProvider.getItems().addAll(gradeService.getAll(filter));
        gradeDataProvider.refreshAll();
    }


    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        Location location = event.getLocation();
        QueryParameters queryParameters = location
                .getQueryParameters();

        parametersMap = queryParameters.getParameters();

        if(parametersMap.get("lecture") != null){
            Lecture parameterLecture = lectureService.getById(parametersMap.get("lecture").get(0));
            lectureFilter.setValue(parameterLecture);
            reloadFilter();

            if (parametersMap.get("exercise") != null){
                String parameterExerciseName = parametersMap.get("exercise").get(0);
                Exercise parameterExercise = parameterLecture.getExercises().stream()
                        .filter(exercise -> exercise.getName().equals(parameterExerciseName))
                        .findFirst().get();

                exerciseFilter.setValue(parameterExercise);
                reloadFilter();
            }
        }

        if (parametersMap.get("matrNumber") != null){
            String parameterMatrikelNumber = parametersMap.get("matrNumber").get(0);
            martrNumberFilter.setValue(parameterMatrikelNumber);
            reloadFilter();
        }

    }
}
