package com.meteo.view.muestraacumulada;

import com.meteo.entity.MuestraAcumulada;
import com.meteo.service.MuestraService;
import com.meteo.view.main.MainView;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import io.jmix.chartsflowui.component.Chart;
import io.jmix.chartsflowui.data.item.MapDataItem;
import io.jmix.chartsflowui.kit.component.model.DataSet;
import io.jmix.chartsflowui.kit.component.model.axis.AxisType;
import io.jmix.chartsflowui.kit.component.model.axis.XAxis;
import io.jmix.chartsflowui.kit.component.model.axis.YAxis;
import io.jmix.chartsflowui.kit.component.model.series.BarSeries;
import io.jmix.chartsflowui.kit.component.model.series.LineSeries;
import io.jmix.flowui.component.grid.DataGrid;
import io.jmix.flowui.model.CollectionContainer;
import io.jmix.flowui.view.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Route(value = "muestras-acumuladas", layout = MainView.class)
@ViewController("MuestraAcumulada.list")
@ViewDescriptor("muestra-acumulada-list-view.xml")
public class MuestraAcumuladaListView extends StandardView {

    @Autowired
    private MuestraService muestraService;

    @ViewComponent
    private CollectionContainer<MuestraAcumulada> muestrasAcumuladasDc;

    @ViewComponent
    private Chart chart;

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
        configurarGrafico(datos);
    }

    private void configurarGrafico(List<MuestraAcumulada> datos) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Crear dataset con los datos
        DataSet dataSet = new DataSet();

        // Preparar datos para el gráfico
        List<String> fechas = datos.stream()
                .map(m -> m.getFecha() != null ? m.getFecha().format(formatter) : "")
                .collect(Collectors.toList());

        List<Double> minimas = datos.stream()
                .map(m -> m.getMinimaPromedio() != null ?
                        Math.round(m.getMinimaPromedio() * 10.0) / 10.0 : null)
                .collect(Collectors.toList());

        List<Double> maximas = datos.stream()
                .map(m -> m.getMaximaPromedio() != null ?
                        Math.round(m.getMaximaPromedio() * 10.0) / 10.0 : null)
                .collect(Collectors.toList());

        List<Double> precipitaciones = datos.stream()
                .map(m -> m.getPrecipitacionTotal() != null ?
                        Math.round(m.getPrecipitacionTotal() * 10.0) / 10.0 : null)
                .collect(Collectors.toList());

        dataSet.setSource(DataSet.Source.of(
                new MapDataItem().add("fecha", fechas)
                        .add("minima", minimas)
                        .add("maxima", maximas)
                        .add("precipitacion", precipitaciones)
        ));

        // Configurar ejes
        XAxis xAxis = new XAxis()
                .withType(AxisType.CATEGORY)
                .withBoundaryGap(false);

        YAxis yAxisTemp = new YAxis()
                .withName("Temperatura (°C)")
                .withPosition("left");

        YAxis yAxisPrecip = new YAxis()
                .withName("Precipitación (L/m²)")
                .withPosition("right");

        // Series de temperatura
        LineSeries minimasSeries = new LineSeries()
                .withName("Temp. Mínima")
                .withEncode("x", "fecha", "y", "minima")
                .withYAxisIndex(0);

        LineSeries maximasSeries = new LineSeries()
                .withName("Temp. Máxima")
                .withEncode("x", "fecha", "y", "maxima")
                .withYAxisIndex(0);

        // Serie de precipitación
        BarSeries precipitacionSeries = new BarSeries()
                .withName("Precipitación")
                .withEncode("x", "fecha", "y", "precipitacion")
                .withYAxisIndex(1);

        // Configurar el gráfico
        chart.setDataSet(dataSet);
        chart.addXAxis(xAxis);
        chart.addYAxis(yAxisTemp, yAxisPrecip);
        chart.addSeries(minimasSeries, maximasSeries, precipitacionSeries);

        // Configurar leyenda y título
        chart.withLegend();
        chart.withTitle("Evolución de Temperaturas y Precipitación");
    }
}
