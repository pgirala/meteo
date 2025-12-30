package com.meteo.screen.estacion;

import io.jmix.ui.screen.*;
import com.meteo.entity.Estacion;

@UiController("Estacion.browse")
@UiDescriptor("estacion-browse.xml")
@LookupComponent("estacionesTable")
public class EstacionBrowse extends StandardLookup<Estacion> {
}
