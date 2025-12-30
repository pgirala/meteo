package com.meteo.view.organizacion;

import com.meteo.entity.Organizacion;
import com.meteo.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "organizaciones", layout = MainView.class)
@ViewController("Organizacion.list")
@ViewDescriptor("organizacion-list-view.xml")
@LookupComponent("organizacionesDataGrid")
@DialogMode(width = "64em")
public class OrganizacionListView extends StandardListView<Organizacion> {
}
