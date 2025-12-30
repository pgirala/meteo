package com.meteo.screen.organizacion;

import io.jmix.ui.screen.*;
import com.meteo.entity.Organizacion;

@UiController("Organizacion.edit")
@UiDescriptor("organizacion-edit.xml")
@EditedEntityContainer("organizacionDc")
public class OrganizacionEdit extends StandardEditor<Organizacion> {
}
