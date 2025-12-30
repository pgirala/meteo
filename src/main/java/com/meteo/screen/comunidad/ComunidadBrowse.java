package com.meteo.screen.comunidad;

import io.jmix.ui.screen.*;
import com.meteo.entity.Comunidad;

@UiController("Comunidad.browse")
@UiDescriptor("comunidad-browse.xml")
@LookupComponent("comunidadesTable")
public class ComunidadBrowse extends StandardLookup<Comunidad> {
}
