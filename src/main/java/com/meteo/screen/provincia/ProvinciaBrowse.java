package com.meteo.screen.provincia;

import io.jmix.ui.screen.*;
import com.meteo.entity.Provincia;

@UiController("Provincia.browse")
@UiDescriptor("provincia-browse.xml")
@LookupComponent("provinciasTable")
public class ProvinciaBrowse extends StandardLookup<Provincia> {
}
