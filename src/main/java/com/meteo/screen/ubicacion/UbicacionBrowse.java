package com.meteo.screen.ubicacion;

import io.jmix.ui.screen.*;
import com.meteo.entity.Ubicacion;

@UiController("Ubicacion.browse")
@UiDescriptor("ubicacion-browse.xml")
@LookupComponent("ubicacionesTable")
public class UbicacionBrowse extends StandardLookup<Ubicacion> {
}
