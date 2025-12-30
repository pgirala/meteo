package com.meteo.view.muestra;

import com.meteo.entity.Muestra;
import com.meteo.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "muestras", layout = MainView.class)
@ViewController("Muestra.list")
@ViewDescriptor("muestra-list-view.xml")
@LookupComponent("muestrasDataGrid")
@DialogMode(width = "64em")
public class MuestraListView extends StandardListView<Muestra> {
}
