package com.meteo.screen.comunidad;

import io.jmix.ui.screen.*;
import com.meteo.entity.Comunidad;

@UiController("Comunidad.edit")
@UiDescriptor("comunidad-edit.xml")
@EditedEntityContainer("comunidadDc")
public class ComunidadEdit extends StandardEditor<Comunidad> {
}
