package com.meteo.screen.organizacion;

import io.jmix.ui.screen.*;
import com.meteo.entity.Organizacion;

@UiController("Organizacion.browse")
@UiDescriptor("organizacion-browse.xml")
@LookupComponent("organizacionesTable")
public class OrganizacionBrowse extends StandardLookup<Organizacion> {
}
