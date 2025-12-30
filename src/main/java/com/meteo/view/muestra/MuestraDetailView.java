package com.meteo.view.muestra;

import com.meteo.entity.Muestra;
import com.meteo.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "muestras/:id", layout = MainView.class)
@ViewController("Muestra.detail")
@ViewDescriptor("muestra-detail-view.xml")
@EditedEntityContainer("muestraDc")
public class MuestraDetailView extends StandardDetailView<Muestra> {
}
