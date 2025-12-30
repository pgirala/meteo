package com.meteo.view.ubicacion;

import com.meteo.entity.Ubicacion;
import com.meteo.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "ubicaciones/:id", layout = MainView.class)
@ViewController("Ubicacion.detail")
@ViewDescriptor("ubicacion-detail-view.xml")
@EditedEntityContainer("ubicacionDc")
public class UbicacionDetailView extends StandardDetailView<Ubicacion> {
}
