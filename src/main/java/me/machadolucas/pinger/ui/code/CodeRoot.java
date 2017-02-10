package me.machadolucas.diagnosis.ui.code;

import com.vaadin.annotations.Theme;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@Theme("diagnosys")
@SpringUI(path = "codes")
public class CodeRoot extends UI {

    VerticalLayout mainLayout = new VerticalLayout();
    Label pageTitle = new Label("Gerador de códigos para pacientes");

    // =========================================================

    @Override
    protected void init(final VaadinRequest vaadinRequest) {
        Page.getCurrent().setTitle("Geração de códigos");
        configureComponents();
        buildLayout();
    }

    private void configureComponents() {
        this.mainLayout.setMargin(true);
        this.mainLayout.setSpacing(true);

        this.pageTitle.setStyleName(ValoTheme.LABEL_H2);
    }

    private void buildLayout() {
        final Label info = new Label("Essa funcionalidade foi movida para a interface administrativa e protegida por senha.", ContentMode.HTML);
        final Link homepage = new Link("Ir para interface administrativa", new ExternalResource("/admin"));

        this.mainLayout.addComponents(this.pageTitle, getGap("2em"), info, getGap("2em"), homepage);
        setContent(this.mainLayout);
    }

    // =================================================================================

    protected static Label getGap(final String height) {
        final Label gap = new Label();
        gap.setHeight(height);
        return gap;
    }
}
