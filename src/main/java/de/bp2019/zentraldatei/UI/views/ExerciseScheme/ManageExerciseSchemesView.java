package de.bp2019.zentraldatei.UI.views.ExerciseScheme;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import de.bp2019.zentraldatei.UI.views.BaseView;
import de.bp2019.zentraldatei.UI.views.MainAppView;
import de.bp2019.zentraldatei.model.Institute;
import de.bp2019.zentraldatei.model.exercise.ExerciseScheme;
import de.bp2019.zentraldatei.service.ExerciseSchemeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

/**
 * View that displays a list of all Exercises
 *
 * @author Luca Dinies
 **/
@PageTitle("Zentraldatei | Übungsschemas")
@Route(value = ManageExerciseSchemesView.ROUTE, layout = MainAppView.class)
public class ManageExerciseSchemesView extends BaseView {

    private static final long serialVersionUID = 1L;

    public static final String ROUTE = "manage-exerciseSchemes";

    private static final Logger LOGGER = LoggerFactory.getLogger(ManageExerciseSchemesView.class);

    private ExerciseSchemeService exerciseSchemeService;

    Grid<ExerciseScheme> grid = new Grid<>();

    @Autowired
    public ManageExerciseSchemesView(ExerciseSchemeService exerciseSchemeService) {
        super("Übungsschemas");

        LOGGER.debug("started creation of ManageExerciseSchemesView");

        this.exerciseSchemeService = exerciseSchemeService;

        /* Table for the existing ExerciseSchemes */
        grid.setWidth("100%");
        grid.setItems(exerciseSchemeService.getAllExerciseSchemes());

        grid.addComponentColumn(item -> createNameButton(item)).setAutoWidth(true);
        grid.addComponentColumn(item -> createInstitutesTag(item)).setAutoWidth(true);
        grid.addComponentColumn(item -> createDeleteButton(item)).setFlexGrow(0).setWidth("4em");

        add(grid);

        Button newExerciseSchemeButton = new Button("Neues Übungsschema");
        newExerciseSchemeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        add(newExerciseSchemeButton);
        setHorizontalComponentAlignment(Alignment.END, newExerciseSchemeButton);

        newExerciseSchemeButton.addClickListener(event -> UI.getCurrent().navigate(EditExerciseSchemeView.ROUTE + "/new"));

        LOGGER.debug("finished creation of ManageExerciseSchemesView");

    }

    /**
     * Used to create the button for the Grid entries that displays the name and
     * links to the edit page of the individual ExerciseScheme.
     *
     * @param item ModuleScheme to create the Button for
     * @return
     * @author Luca Dinies
     */
    private Button createNameButton(ExerciseScheme item) {
        Button button = new Button(item.getName(), clickEvent -> {
            UI.getCurrent().navigate(EditExerciseSchemeView.ROUTE + "/" + item.getId());
        });
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        return button;
    }

    /**
     * Used to generate the Institutes field for each Grid item
     *
     * @param item
     * @return
     * @author Luca Dinies
     */
    private Text createInstitutesTag(ExerciseScheme item) {
        Optional<String> text = item.getInstitutes().stream().map(Institute::getName)
                .sorted(String.CASE_INSENSITIVE_ORDER).reduce((i1, i2) -> i1 + ", " + i2);

        if (text.isPresent()) {
            return new Text(text.get());
        } else {
            return new Text("");
        }
    }

    /**
     * Used to generate the delete button for each Grid Item
     *
     * @param item
     * @return
     * @author Luca Dinies
     */
    private Button createDeleteButton(ExerciseScheme item) {

        Button button = new Button(new Icon(VaadinIcon.CLOSE), clickEvent -> {
            Dialog dialog = new Dialog();
            exerciseSchemeService.deleteExerciseScheme(item);
            dialog.add(new Text("Wirklich Löschen?"));
            dialog.setCloseOnEsc(false);
            dialog.setCloseOnOutsideClick(false);

            Button confirmButton = new Button("Löschen", event -> {
                exerciseSchemeService.deleteExerciseScheme(item);
                ListDataProvider<ExerciseScheme> dataProvider = (ListDataProvider<ExerciseScheme>) grid
                        .getDataProvider();
                dataProvider.getItems().remove(item);
                dataProvider.refreshAll();

                dialog.close();
                Dialog answerDialog = new Dialog();
                answerDialog.add(new Text("Übungsschema '" + item.getName() + "' gelöscht"));
                answerDialog.open();
            });

            Button cancelButton = new Button("Abbruch", event -> {
                dialog.close();
            });

            dialog.add(confirmButton, cancelButton);
            dialog.open();

        });

        button.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_ERROR);
        return button;
    }

}
