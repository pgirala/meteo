package com.meteo.screen.ubicacion;

import io.jmix.ui.screen.*;
import com.meteo.entity.Ubicacion;

@UiController("Ubicacion.edit")
@UiDescriptor("ubicacion-edit.xml")
@EditedEntityContainer("ubicacionDc")
public class UbicacionEdit extends StandardEditor<Ubicacion> {
}
