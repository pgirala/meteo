package com.meteo.screen.estacion;

import io.jmix.ui.screen.*;
import com.meteo.entity.Estacion;

@UiController("Estacion.edit")
@UiDescriptor("estacion-edit.xml")
@EditedEntityContainer("estacionDc")
public class EstacionEdit extends StandardEditor<Estacion> {
}
