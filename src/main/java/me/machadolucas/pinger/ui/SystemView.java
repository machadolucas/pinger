package me.machadolucas.diagnosis.ui;

import com.vaadin.ui.VerticalLayout;

public abstract class SystemView extends VerticalLayout {

    protected abstract void configureComponents();

    protected abstract void buildLayout();

}
