package me.machadolucas.diagnosis.ui.admin.results;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.event.FieldEvents;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import me.machadolucas.diagnosis.entity.*;
import me.machadolucas.diagnosis.questionnariesdata.SectionsCreator;
import me.machadolucas.diagnosis.ui.admin.AdminPanelView;
import me.machadolucas.diagnosis.ui.admin.AdminViews;
import org.vaadin.dialogs.ConfirmDialog;
import org.vaadin.haijian.ExcelExporter;
import org.vaadin.haijian.PdfExporter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ManagePersonsView extends AdminViews {

    public static final String COLUMN_SELECIONAR = "-";
    public static final String COLUMN_CODIGO = "Código";
    public static final String COLUMN_NOME = "Nome";
    public static final String COLUMN_INICIAL = "Inicial";
    public static final String COLUMN_RASTR_ADOL = "Rastr.(Adol.)";
    public static final String COLUMN_RASTR_PAIS = "Rastr.(Pais)";
    public static final String COLUMN_DATA_DE_CRIAÇÃO = "Data de criação";
    public static final String COLUMN_VER_RESPOSTAS = "Ver respostas";

    Label pageTitle = new Label("Gerenciar pacientes");

    Button backButton = new Button("Voltar", this::goBack);

    Table persons = new Table("Seus pacientes:");
    List<Person> personList;
    List<AdminUser> admins;

    TextField creationAmount = new TextField("Quantidade de códigos a gerar");
    ComboBox transferSelected = new ComboBox("Transferir selecionados");
    ExcelExporter excelExporter = new ExcelExporter(this.persons);
    PdfExporter pdfExporter = new PdfExporter(this.persons);
    Button removeSelected = new Button("Excluir selecionados");
    Button createPersonButton = new Button("Criar novos códigos", this::generateAndInsertNewPerson);

    public ManagePersonsView(final AdminUser adminUser, final List<Person> personList, final List<AdminUser> admins) {
        this.adminUser = adminUser;
        this.personList = personList;
        this.admins = admins;

        configureComponents();
        buildLayout();
    }

    @Override
    protected void configureComponents() {
        setMargin(true);
        setSpacing(true);

        this.creationAmount.setValue("1");
        this.creationAmount.setMaxLength(2);
        this.createPersonButton.addStyleName(ValoTheme.BUTTON_PRIMARY);

        // ----------- Tabela de pacientes --------------
        initializeTable();

        // ------------- Barra de botões -----------------
        this.excelExporter.setCaption("Exportar para Excel");
        this.excelExporter.setVisibleColumns(new Object[]{ //
                COLUMN_CODIGO, COLUMN_NOME, COLUMN_INICIAL, COLUMN_RASTR_ADOL, COLUMN_RASTR_PAIS,
                COLUMN_DATA_DE_CRIAÇÃO});
        this.pdfExporter.setCaption("Exportar para PDF");
        this.pdfExporter.setVisibleColumns(new Object[]{ //
                COLUMN_CODIGO, COLUMN_NOME, COLUMN_INICIAL, COLUMN_RASTR_ADOL, COLUMN_RASTR_PAIS,
                COLUMN_DATA_DE_CRIAÇÃO});
        this.pdfExporter.setWithBorder(true);

        this.removeSelected.addClickListener(this::removeSelected);
        this.removeSelected.addStyleName(ValoTheme.BUTTON_DANGER);

        this.transferSelected.addValueChangeListener(this::transferSelected);
        this.transferSelected.setNewItemsAllowed(false);
        this.transferSelected.setImmediate(true);
        this.admins.forEach(adminUser -> this.transferSelected.addItem(adminUser));
        this.transferSelected.removeItem(this.adminUser);

        this.backButton.setStyleName(ValoTheme.BUTTON_FRIENDLY);
        // ------------------------------------------------
    }

    private void initializeTable() {
        this.persons.addContainerProperty(COLUMN_SELECIONAR, CheckBox.class, null);
        this.persons.addContainerProperty(COLUMN_CODIGO, String.class, null);
        this.persons.addContainerProperty(COLUMN_NOME, TextField.class, null);
        this.persons.addContainerProperty(COLUMN_INICIAL, String.class, null);
        this.persons.addContainerProperty(COLUMN_RASTR_ADOL, String.class, null);
        this.persons.addContainerProperty(COLUMN_RASTR_PAIS, String.class, null);
        this.persons.addContainerProperty(COLUMN_DATA_DE_CRIAÇÃO, String.class, null);
        this.persons.addContainerProperty(COLUMN_VER_RESPOSTAS, Button.class, null);

        this.personList.forEach(this::insertInTable);

        this.persons.setPageLength(this.personList.size());
    }

    @Override
    protected void buildLayout() {
        final HorizontalLayout header = getHeaderMenu(this.pageTitle);

        final VerticalLayout codeCreationLayout = new VerticalLayout();
        codeCreationLayout.setSpacing(true);
        codeCreationLayout.addComponents(this.creationAmount, this.createPersonButton);

        final CheckBox selectAll = new CheckBox("Selecionar todos", false);
        selectAll.addValueChangeListener(this::selectAll);
        selectAll.setImmediate(true);

        final HorizontalLayout buttonsBar = new HorizontalLayout();
        buttonsBar.setSpacing(true);
        buttonsBar.addComponents(this.backButton, this.excelExporter, this.pdfExporter, this.removeSelected, this
                .transferSelected);
        updateButtonsByTableEmptyness();

        addComponents(header, codeCreationLayout, getGap("1em"), this.persons, selectAll, buttonsBar);
    }

    // =================================================================================

    private void goBack(final Button.ClickEvent event) {
        getAdminRoot().getContentPanel().setContent(new AdminPanelView(this.adminUser));
    }

    private void selectAll(final Property.ValueChangeEvent event) {
        final boolean selected = (boolean) event.getProperty().getValue();
        for (final Object o : this.persons.getItemIds()) {
            final Person person = (Person) o;
            final Item item = this.persons.getItem(person);
            ((CheckBox) item.getItemProperty(COLUMN_SELECIONAR).getValue()).setValue(selected);
        }
    }

    private void updateButtonsByTableEmptyness() {
        this.excelExporter.setEnabled(this.persons.size() > 0);
        this.pdfExporter.setEnabled(this.persons.size() > 0);
        this.removeSelected.setEnabled(this.persons.size() > 0);
        this.transferSelected.setEnabled(this.persons.size() > 0);
    }

    private List<Person> getSelectedPeople() {
        final List<Person> selectedPeople = new ArrayList<>();
        for (final Object o : this.persons.getItemIds()) {
            final Person person = (Person) o;
            final Item item = this.persons.getItem(person);
            if (item != null) {
                final CheckBox selection = (CheckBox) item.getItemProperty(COLUMN_SELECIONAR).getValue();
                if (selection.getValue()) {
                    selectedPeople.add(person);
                }
            }
        }
        return selectedPeople;
    }

    private void transferSelected(final Property.ValueChangeEvent event) {
        final AdminUser adminUser = (AdminUser) event.getProperty().getValue();
        if (adminUser != null) {
            final List<Person> peopleToMove = getSelectedPeople();
            if (peopleToMove.size() > 0) {
                ConfirmDialog.show(getAdminRoot().getUI(), "Tem certeza que deseja transferir os pacientes " +
                        "selecionados?", "Somente " + adminUser.getName() + " terá acesso aos dados dos pacientes " +
                        "transferidos.", "Sim", "Não", (ConfirmDialog.Listener) dialog -> {
                    if (dialog.isConfirmed()) {
                        peopleToMove.forEach(person -> {
                            person.setAdminResponsible(adminUser);
                            getAdminRoot().getPersonRepository().save(person);
                            this.persons.removeItem(person);
                        });
                        this.persons.setPageLength(this.persons.getPageLength() - 1);
                        updateButtonsByTableEmptyness();
                        Notification.show("Pacientes transferidos com sucesso.", Notification.Type.HUMANIZED_MESSAGE);
                    }
                });
            }
            this.transferSelected.setValue(null);
        }
    }

    private void changePersonName(final FieldEvents.TextChangeEvent event) {
        final TextField field = (TextField) event.getSource();
        final Person person = (Person) field.getData();
        person.setPersonName(event.getText());
        getAdminRoot().getPersonRepository().save(person);
        Notification.show("Alteração de nome do paciente salva.", Notification.Type.TRAY_NOTIFICATION);
    }

    private void insertInTable(final Person person) {
        final CheckBox selection = new CheckBox();
        final TextField nameField = new TextField();
        nameField.setNullSettingAllowed(true);
        nameField.setValue(person.getPersonName());
        nameField.addTextChangeListener(this::changePersonName);
        nameField.setData(person);
        nameField.setImmediate(true);
        final Button seeResults = new Button("Respostas", this::seeResults);
        seeResults.setData(person);
        this.persons.addItem(new Object[]{selection, person.getPersonCode(), nameField, //
                        person.getCodes().get(SectionType.INICIAL), //
                        person.getCodes().get(SectionType.RASTREAMENTO_C), //
                        person.getCodes().get(SectionType.RASTREAMENTO_P), //
                        person.getCreationDate().toString(), seeResults}, //
                person);
    }

    private void seeResults(final Button.ClickEvent event) {
        final Person person = (Person) event.getButton().getData();
        person.postConstruct();
        getAdminRoot().getQuestionnaireRepository().save(person.getInexistentQuestionnaires());
        getAdminRoot().getPersonRepository().save(person);
        getAdminRoot().getContentPanel().setContent(new PersonResultView(this.adminUser, person));
    }

    // ============ Create Person =============================================

    private void generateAndInsertNewPerson(final Button.ClickEvent event) {
        if (this.creationAmount.isEmpty() || !this.creationAmount.getValue().matches("^[0-9]{1,3}")) {
            Notification.show("Quantidade de códigos tem valor inválido!", Notification.Type.ERROR_MESSAGE);
            return;
        }
        final int codesToBeCreated = Integer.valueOf(this.creationAmount.getValue());

        for (int i = 0; i < codesToBeCreated; i++) {
            final Person person = new Person();
            person.setPersonCode(getAdminRoot().getCodeGenerator().generateLiteralCode());
            for (final SectionType type : SectionType.values()) {
                person.getCodes().put(type, getAdminRoot().getCodeGenerator().generateNumericCode());
            }

            Person existingPerson = getAdminRoot().getPersonRepository().findByPersonCode(person.getPersonCode());
            while (existingPerson != null) {
                person.setPersonCode(getAdminRoot().getCodeGenerator().generateLiteralCode());
                existingPerson = getAdminRoot().getPersonRepository().findByPersonCode(person.getPersonCode());
            }

            person.setAdminResponsible(this.adminUser);

            createSessionStructForPerson(person);
            saveStructure(person);

            System.out.println("M=generateAndInsertNewPerson,personCode=" + person.getPersonCode() + ",codes=" +
                    person.getCodes().toString() + ",adminResponsible=" + person.getAdminResponsible().getName());

            insertInTable(person);

        }
        this.persons.setPageLength(this.persons.size());
        updateButtonsByTableEmptyness();
    }

    private void createSessionStructForPerson(final Person newPerson) {
        newPerson.setInitialSection(SectionsCreator.sessaoInicial(null));
        newPerson.setChildSection(SectionsCreator.sessaoRastreamento_C(null));
        newPerson.setParentSection(SectionsCreator.sessaoRastreamento_P(null));
        newPerson.setDoctorSection(SectionsCreator.sessaoDoutor(null));
    }

    private void saveStructure(final Person newPerson) {
        saveSession(newPerson.getChildSection());
        saveSession(newPerson.getInitialSection());
        saveSession(newPerson.getParentSection());
        saveSession(newPerson.getDoctorSection());

        getAdminRoot().getPersonRepository().save(newPerson);
    }

    private void saveSession(final PersonSection personSection) {
        for (final HashMap.Entry<String, Questionnaire> entry : personSection.getQuestionnaires().entrySet()) {
            final Questionnaire questionnaire = entry.getValue();
            if (questionnaire.getSuplementQuestionnaire() != null) {
                getAdminRoot().getQuestionnaireRepository().save(questionnaire.getSuplementQuestionnaire());
            }
            getAdminRoot().getQuestionnaireRepository().save(questionnaire);
        }
    }

    // ============ Delete Person =============================================

    private void removeSelected(final Button.ClickEvent event) {
        final List<Person> peopleToRemove = getSelectedPeople();
        if (peopleToRemove.size() > 0) {
            ConfirmDialog.show(getAdminRoot().getUI(), "Tem certeza que deseja excluir os dados dos pacientes " +
                    "selecionados?", "Essa ação é irreversível e todos os dados relacionados a esses pacientes serão " +
                    "perdidos.", "Sim", "Não", (ConfirmDialog.Listener) dialog -> {
                if (dialog.isConfirmed()) {
                    peopleToRemove.forEach(this::removePerson);
                    updateButtonsByTableEmptyness();
                    this.persons.setPageLength(this.persons.getPageLength() - 1);
                    Notification.show("Pacientes excluídos com sucesso.", Notification.Type.HUMANIZED_MESSAGE);
                }
            });
        }
    }

    private void removePerson(final Person personToRemove) {
        deleteStructure(personToRemove);
        this.persons.removeItem(personToRemove);
    }

    private void deleteStructure(final Person oldPerson) {
        deleteSession(oldPerson.getInitialSection());
        deleteSession(oldPerson.getChildSection());
        deleteSession(oldPerson.getParentSection());
        deleteSession(oldPerson.getDoctorSection());

        getAdminRoot().getPersonRepository().delete(oldPerson);
    }

    private void deleteSession(final PersonSection personSection) {
        for (final HashMap.Entry<String, Questionnaire> entry : personSection.getQuestionnaires().entrySet()) {
            final Questionnaire questionnaire = entry.getValue();
            if (questionnaire.getSuplementQuestionnaire() != null) {
                getAdminRoot().getQuestionnaireRepository().delete(questionnaire.getSuplementQuestionnaire());
            }
            getAdminRoot().getQuestionnaireRepository().delete(questionnaire);
        }
    }

}
