package com.meteo.screen.provincia;

import io.jmix.ui.screen.*;
import com.meteo.entity.Provincia;

@UiController("Provincia.edit")
@UiDescriptor("provincia-edit.xml")
@EditedEntityContainer("provinciaDc")
public class ProvinciaEdit extends StandardEditor<Provincia> {
}
