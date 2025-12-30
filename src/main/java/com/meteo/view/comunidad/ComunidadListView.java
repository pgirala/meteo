package com.meteo.view.comunidad;

import com.meteo.entity.Comunidad;
import com.meteo.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "comunidades", layout = MainView.class)
@ViewController("Comunidad.list")
@ViewDescriptor("comunidad-list-view.xml")
@LookupComponent("comunidadesDataGrid")
@DialogMode(width = "64em")
public class ComunidadListView extends StandardListView<Comunidad> {
}
