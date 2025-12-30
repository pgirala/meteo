package com.meteo.view.organizacion;

import com.meteo.entity.Organizacion;
import com.meteo.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.EditedEntityContainer;
import io.jmix.flowui.view.StandardDetailView;
import io.jmix.flowui.view.ViewController;
import io.jmix.flowui.view.ViewDescriptor;

@Route(value = "organizaciones/:id", layout = MainView.class)
@ViewController("Organizacion.detail")
@ViewDescriptor("organizacion-detail-view.xml")
@EditedEntityContainer("organizacionDc")
public class OrganizacionDetailView extends StandardDetailView<Organizacion> {
}
