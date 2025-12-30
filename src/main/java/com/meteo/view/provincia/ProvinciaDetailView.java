package com.meteo.view.provincia;

import com.meteo.entity.Provincia;
import com.meteo.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "provincias/:id", layout = MainView.class)
@ViewController("Provincia.detail")
@ViewDescriptor("provincia-detail-view.xml")
@EditedEntityContainer("provinciaDc")
public class ProvinciaDetailView extends StandardDetailView<Provincia> {
}
