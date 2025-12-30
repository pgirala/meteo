package com.meteo.view.estacion;

import com.meteo.entity.Estacion;
import com.meteo.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "estaciones", layout = MainView.class)
@ViewController("Estacion.list")
@ViewDescriptor("estacion-list-view.xml")
@LookupComponent("estacionesDataGrid")
@DialogMode(width = "64em")
public class EstacionListView extends StandardListView<Estacion> {
}
