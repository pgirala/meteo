package com.meteo.view.comunidad;

import com.meteo.entity.Comunidad;
import com.meteo.view.main.MainView;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.view.*;

@Route(value = "comunidades/:id", layout = MainView.class)
@ViewController("Comunidad.detail")
@ViewDescriptor("comunidad-detail-view.xml")
@EditedEntityContainer("comunidadDc")
public class ComunidadDetailView extends StandardDetailView<Comunidad> {
}
