package me.machadolucas.pinger.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import me.machadolucas.pinger.entity.TargetEntity;
import me.machadolucas.pinger.repository.TargetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.vaadin.crudui.crud.impl.GridBasedCrudComponent;

import javax.annotation.PostConstruct;

@Theme("pinger")
@SpringUI
public class AppRoot extends UI {

    private VerticalLayout layout;
    private GridBasedCrudComponent<TargetEntity> crud;

    @Override
    protected void init(final VaadinRequest vaadinRequest) {
        Page.getCurrent().setTitle("Pinger");
    }

    @Autowired
    private TargetRepository targetRepository;

    @PostConstruct
    private void checkAuthCookie() {
        buildLayout();
    }

    private void buildLayout() {
        this.crud = new GridBasedCrudComponent<>(TargetEntity.class);

        this.crud.setFindAllOperation(() -> targetRepository.findAll());
        this.crud.setAddOperation(targetRepository::save);
        this.crud.setUpdateOperation(targetRepository::save);
        this.crud.setDeleteOperation(targetRepository::delete);

        this.layout = new VerticalLayout();
        this.layout.addComponent(crud);
        this.layout.setExpandRatio(crud, 1);
        this.layout.setSizeFull();
        this.layout.setMargin(new MarginInfo(true));
        this.layout.setSpacing(false);

        setContent(this.layout);
    }
}
