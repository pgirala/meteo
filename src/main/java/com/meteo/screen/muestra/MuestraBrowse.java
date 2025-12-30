package com.meteo.screen.muestra;

import io.jmix.ui.screen.*;
import com.meteo.entity.Muestra;

@UiController("Muestra.browse")
@UiDescriptor("muestra-browse.xml")
@LookupComponent("muestrasTable")
public class MuestraBrowse extends StandardLookup<Muestra> {
}
