package com.meteo.view.muestraacumulada;

import com.meteo.entity.MuestraAcumulada;
import com.meteo.service.MuestraService;
import com.meteo.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.Route;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Route(value = "muestras-acumuladas", layout = MainView.class)
@ViewController("MuestraAcumulada.list")
@ViewDescriptor("muestra-acumulada-list-view.xml")
public class MuestraAcumuladaListView extends StandardView {

    @Autowired
    private MuestraService muestraService;

    @ViewComponent
    private CollectionContainer<MuestraAcumulada> muestrasAcumuladasDc;

    @ViewComponent
    private DataGrid<MuestraAcumulada> muestrasAcumuladasDataGrid;

    @Subscribe
    public void onInit(InitEvent event) {
        cargarDatos(muestraService.obtenerUltimoMes());
    }

    @Subscribe("btnSemana")
    public void onBtnSemanaClick(ClickEvent<Button> event) {
        cargarDatos(muestraService.obtenerUltimaSemana());
    }

    @Subscribe("btnMes")
    public void onBtnMesClick(ClickEvent<Button> event) {
        cargarDatos(muestraService.obtenerUltimoMes());
    }

    @Subscribe("btnTrimestre")
    public void onBtnTrimestreClick(ClickEvent<Button> event) {
        cargarDatos(muestraService.obtenerUltimoTrimestre());
    }

    @Subscribe("btnAnio")
    public void onBtnAnioClick(ClickEvent<Button> event) {
        cargarDatos(muestraService.obtenerUltimoAnio());
    }

    private void cargarDatos(List<MuestraAcumulada> datos) {
        muestrasAcumuladasDc.setItems(datos);
    }
}
