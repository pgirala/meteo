package com.meteo.view.estacion;

import com.meteo.entity.Estacion;
import com.meteo.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "estaciones/:id", layout = MainView.class)
@ViewController("Estacion.detail")
@ViewDescriptor("estacion-detail-view.xml")
@EditedEntityContainer("estacionDc")
public class EstacionDetailView extends StandardDetailView<Estacion> {
}
