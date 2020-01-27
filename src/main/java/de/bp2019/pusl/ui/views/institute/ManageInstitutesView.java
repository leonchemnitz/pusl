package de.bp2019.pusl.ui.views.institute;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.bp2019.pusl.config.AppConfig;
import de.bp2019.pusl.model.Institute;
import de.bp2019.pusl.service.InstituteService;
import de.bp2019.pusl.ui.views.BaseView;
import de.bp2019.pusl.ui.views.MainAppView;

/**
 * View that displays a list of all Institutes
 * 
 * @author Leon Chemnitz
 */
@PageTitle(AppConfig.NAME + " | Institute")
@Route(value = ManageInstitutesView.ROUTE, layout = MainAppView.class)
public class ManageInstitutesView extends BaseView {

    private static final long serialVersionUID = -5763725756205681478L;

    public static final String ROUTE = "manage-institutes";

    private static final Logger LOGGER = LoggerFactory.getLogger(ManageInstitutesView.class);

    private InstituteService instituteService;

    private Grid<Institute> grid = new Grid<>();

    @Autowired
    public ManageInstitutesView(InstituteService instituteService) {
        super("Institute");
        LOGGER.debug("started creation of ManageInstitutesView");

        this.instituteService = instituteService;

        /* -- Create Components -- */

        Grid<Institute> grid = new Grid<>();

        grid.setWidth("100%");
        grid.setItems(instituteService.getAllInstitutes());

        grid.addComponentColumn(item -> createNameButton(item)).setAutoWidth(true);
        grid.addComponentColumn(item -> createDeleteButton(item)).setFlexGrow(0).setWidth("4em");

        add(grid);

        Button newInstituteButton = new Button("Neues Institut");
        newInstituteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        add(newInstituteButton);
        setHorizontalComponentAlignment(Alignment.END, newInstituteButton);

        newInstituteButton.addClickListener(event -> UI.getCurrent().navigate(EditInstituteView.ROUTE + "/new"));

        LOGGER.debug("finished creation of ManageInstitutesView");
    }

    /**
     * Used to create the button for the Grid entries that displays the name and
     * links to the edit page of the individual Institute.
     * 
     * @param item Institute to create the Button for
     * @return
     * @author Leon Chemnitz
     */
    private Button createNameButton(Institute item) {
        Button button = new Button(item.getName(), clickEvent -> {
            UI.getCurrent().navigate(EditInstituteView.ROUTE + "/" + item.getId());
        });
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        return button;
    }

    /**
     * Used to generate the delete button for each Grid Item
     * 
     * @param item entity to create button for
     * @author Leon Chemnitz
     * @return delete button
     */
    protected Button createDeleteButton(Institute item) {
        Button button = new Button(new Icon(VaadinIcon.CLOSE), clickEvent -> {
            Dialog dialog = new Dialog();
            dialog.add(new Text("Wirklich Löschen?"));
            dialog.setCloseOnEsc(false);
            dialog.setCloseOnOutsideClick(false);

            Button confirmButton = new Button("Löschen", event -> {
                instituteService.deleteInstitute(item);
                ListDataProvider<Institute> dataProvider = (ListDataProvider<Institute>) grid.getDataProvider();
                dataProvider.getItems().remove(item);
                dataProvider.refreshAll();

                dialog.close();
                Dialog answerDialog = new Dialog();
                answerDialog.add(new Text("Institut '" + item.getName() + "' gelöscht"));
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