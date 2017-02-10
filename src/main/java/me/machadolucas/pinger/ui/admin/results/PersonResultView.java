package me.machadolucas.diagnosis.ui.admin.results;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.themes.ValoTheme;

import me.machadolucas.diagnosis.entity.AdminUser;
import me.machadolucas.diagnosis.entity.Person;
import me.machadolucas.diagnosis.entity.PersonSection;
import me.machadolucas.diagnosis.entity.Questionnaire;
import me.machadolucas.diagnosis.ui.admin.AdminPanelView;
import me.machadolucas.diagnosis.ui.admin.AdminViews;

public class PersonResultView extends AdminViews {

    private static final String COLUMN_PREENCHIDO = "Status";
    private static final String COLUMN_NOME = "Questionário";
    private static final String COLUMN_LAST_UPDATE = "Última modificação";
    private static final String COLUMN_DETALHES = "Mais detalhes";

    private final Person person;
    private final Label instructions = new Label("Você pode clicar em Detalhes para atribuir a nota " +
            "resumo e ver as respostas.<br/>A coluna Status indica se o questionário está em branco, se foi " +
            "parcialmente preenchido (preto) ou se está completamente preenchido (verde).", ContentMode.HTML);
    private final Button backButton = new Button("Voltar para Gerenciar pacientes", this::goBackManagePerson);
    private final Button backToMenuButton = new Button("Voltar para Painel administrativo", this::goBackMenu);
    private final Table inicialTable = new Table("Questionários da sessão Inicial");
    private final Table rastreamentoTable = new Table("Questionários das sessões de rastreamento - Adolescente e Pais");

    private Label pageTitle;

    public PersonResultView(final AdminUser adminUser, final Person person) {
        this.adminUser = adminUser;
        this.person = person;

        configureComponents();
        buildLayout();
    }

    @Override
    protected void configureComponents() {
        setMargin(true);
        setSpacing(true);

        initializeTable(this.inicialTable, this.person.getInitialSection());
        initializeTable(this.rastreamentoTable, this.person.getChildSection());

        this.pageTitle = new Label("Visualizar resultados - Paciente: " + this.person.getPersonCode());
        this.backButton.setStyleName(ValoTheme.BUTTON_FRIENDLY);
        this.backToMenuButton.setStyleName(ValoTheme.BUTTON_FRIENDLY);

    }

    @Override
    protected void buildLayout() {

        final HorizontalLayout header = getHeaderMenu(this.pageTitle);

        final HorizontalLayout buttonsBar = new HorizontalLayout();
        buttonsBar.setSpacing(true);
        buttonsBar.addComponents(this.backButton);
        buttonsBar.addComponents(this.backToMenuButton);

        addComponents( //
                header, this.instructions, //
                this.inicialTable, //
                getGap("1em"), //
                this.rastreamentoTable, //
                getGap("1em"), //
                buttonsBar);
    }

    private void initializeTable(final Table table, final PersonSection section) {
        table.addContainerProperty(COLUMN_PREENCHIDO, Image.class, null);
        table.addContainerProperty(COLUMN_NOME, String.class, null);
        table.addContainerProperty(COLUMN_LAST_UPDATE, LocalDateTime.class, null);
        table.addContainerProperty(COLUMN_DETALHES, Button.class, null);

        for (final HashMap.Entry<String, Questionnaire> entry : section.getQuestionnaires().entrySet()) {
            final Questionnaire questionnaire = entry.getValue();
            final String fillStatus = questionnaire.checkQuestionnaireFillStatus();
            final Image fillIcon;
            switch (fillStatus) {
                case "C": {
                    fillIcon = new Image("Completo", new ExternalResource("/img/check.png"));
                    break;
                }
                case "P": {
                    fillIcon = new Image("Parcialmente preenchido", new ExternalResource("/img/incomplete.png"));
                    break;
                }
                default: {
                    fillIcon = new Image("Em branco", new ExternalResource("/img/empty.png"));
                }
            }
            fillIcon.setWidth("40px");
            final Button seeDetails = new Button("Detalhes", this::seeDetails);
            seeDetails.setData(entry.getKey());
            table.addItem(
                    new Object[] { fillIcon, questionnaire.getTitle(), questionnaire.getLastUpdated(), seeDetails },
                    questionnaire);
        }

        table.setPageLength(table.size());
    }

    private HorizontalLayout getTableBottomBar(final Table table) {
        final Button export = new Button("Exportar dados", event -> this.exportQuestionnaries(event, table));
        export.setEnabled(false);

        final HorizontalLayout buttonsBar = new HorizontalLayout();
        buttonsBar.setSpacing(true);
        buttonsBar.addComponents(export);

        return buttonsBar;
    }

    // =================================================================================

    private void goBackManagePerson(final Button.ClickEvent event) {
        final List<Person> personList = getAdminRoot().getPersonRepository().findByAdminResponsible(this.adminUser);
        final List<AdminUser> admins = getAdminRoot().getAdminUserRepository().findAll();
        getAdminRoot().getContentPanel().setContent(new ManagePersonsView(this.adminUser, personList, admins));
    }

    private void goBackMenu(final Button.ClickEvent event) {
        getAdminRoot().getContentPanel().setContent(new AdminPanelView(this.adminUser));
    }

    private void exportQuestionnaries(final Button.ClickEvent event, final Table table) {
        //TODO
    }

    private void seeDetails(final Button.ClickEvent event) {
        final String questionnaireName = (String) event.getButton().getData();
        final QuestionnaireResultsView view = new QuestionnaireResultsView(this.adminUser, questionnaireName, this
                .person);
        getAdminRoot().getContentPanel().setContent(view);
        view.scrollUp();
    }

}
