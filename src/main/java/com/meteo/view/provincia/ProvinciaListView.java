package com.meteo.view.provincia;

import com.meteo.entity.Provincia;
import com.meteo.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "provincias", layout = MainView.class)
@ViewController("Provincia.list")
@ViewDescriptor("provincia-list-view.xml")
@LookupComponent("provinciasDataGrid")
@DialogMode(width = "64em")
public class ProvinciaListView extends StandardListView<Provincia> {
}
