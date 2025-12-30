package com.meteo.screen.muestra;

import io.jmix.ui.screen.*;
import com.meteo.entity.Muestra;

@UiController("Muestra.edit")
@UiDescriptor("muestra-edit.xml")
@EditedEntityContainer("muestraDc")
public class MuestraEdit extends StandardEditor<Muestra> {
}
