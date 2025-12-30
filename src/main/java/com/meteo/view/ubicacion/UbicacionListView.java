package com.meteo.view.ubicacion;

import com.meteo.entity.Ubicacion;
import com.meteo.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "ubicaciones", layout = MainView.class)
@ViewController("Ubicacion.list")
@ViewDescriptor("ubicacion-list-view.xml")
@LookupComponent("ubicacionesDataGrid")
@DialogMode(width = "64em")
public class UbicacionListView extends StandardListView<Ubicacion> {
}
