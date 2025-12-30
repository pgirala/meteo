package com.meteo.screen.muestraacumulada;

import com.meteo.entity.MuestraAcumulada;
import com.meteo.service.MuestraService;
import io.jmix.charts.component.Chart;
import io.jmix.charts.component.SerialChart;
import io.jmix.charts.model.*;
import io.jmix.charts.model.axis.CategoryAxis;
import io.jmix.charts.model.axis.ValueAxis;
import io.jmix.charts.model.chart.impl.SerialChartModelImpl;
import io.jmix.charts.model.graph.Graph;
import io.jmix.ui.component.Button;
import io.jmix.ui.component.GroupTable;
import io.jmix.ui.model.CollectionContainer;
import io.jmix.ui.screen.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.format.DateTimeFormatter;
import java.util.List;

@UiController("MuestraAcumulada.browse")
@UiDescriptor("muestra-acumulada-browse.xml")
@LookupComponent("muestrasAcumuladasTable")
public class MuestraAcumuladaBrowse extends StandardLookup<MuestraAcumulada> {

    @Autowired
    private MuestraService muestraService;

    @Autowired
    private CollectionContainer<MuestraAcumulada> muestrasAcumuladasDc;

    @Autowired
    private SerialChart chart;

    @Subscribe
    public void onInit(InitEvent event) {
        cargarDatos(muestraService.obtenerUltimoMes());
    }

    @Subscribe("btnSemana")
    public void onBtnSemanaClick(Button.ClickEvent event) {
        cargarDatos(muestraService.obtenerUltimaSemana());
    }

    @Subscribe("btnMes")
    public void onBtnMesClick(Button.ClickEvent event) {
        cargarDatos(muestraService.obtenerUltimoMes());
    }

    @Subscribe("btnTrimestre")
    public void onBtnTrimestreClick(Button.ClickEvent event) {
        cargarDatos(muestraService.obtenerUltimoTrimestre());
    }

    @Subscribe("btnAnio")
    public void onBtnAnioClick(Button.ClickEvent event) {
        cargarDatos(muestraService.obtenerUltimoAnio());
    }

    private void cargarDatos(List<MuestraAcumulada> datos) {
        muestrasAcumuladasDc.setItems(datos);
        configurarGrafico(datos);
    }

    private void configurarGrafico(List<MuestraAcumulada> datos) {
        // Crear el modelo del gráfico
        SerialChartModelImpl chartModel = new SerialChartModelImpl();
        chartModel.setCategoryField("fecha");

        // Configurar el eje de categorías (fechas)
        CategoryAxis categoryAxis = new CategoryAxis();
        categoryAxis.setGridPosition(GridPosition.START);
        categoryAxis.setLabelRotation(45);
        chartModel.setCategoryAxis(categoryAxis);

        // Configurar el eje de valores
        ValueAxis valueAxis = new ValueAxis();
        valueAxis.setTitle("Temperatura (°C)");
        valueAxis.setPosition(Position.LEFT);
        chartModel.addValueAxes(valueAxis);

        ValueAxis precipitacionAxis = new ValueAxis();
        precipitacionAxis.setTitle("Precipitación (L/m²)");
        precipitacionAxis.setPosition(Position.RIGHT);
        chartModel.addValueAxes(precipitacionAxis);

        // Gráfico de línea para temperatura mínima
        Graph minimaGraph = new Graph();
        minimaGraph.setId("minima");
        minimaGraph.setValueField("minimaPromedio");
        minimaGraph.setTitle("Temp. Mínima");
        minimaGraph.setType(GraphType.LINE);
        minimaGraph.setLineColor(Color.BLUE);
        minimaGraph.setLineThickness(2);
        minimaGraph.setBalloon(new Balloon());
        minimaGraph.setBalloonText("[[category]]<br>Min: [[value]]°C");
        chartModel.addGraphs(minimaGraph);

        // Gráfico de línea para temperatura máxima
        Graph maximaGraph = new Graph();
        maximaGraph.setId("maxima");
        maximaGraph.setValueField("maximaPromedio");
        maximaGraph.setTitle("Temp. Máxima");
        maximaGraph.setType(GraphType.LINE);
        maximaGraph.setLineColor(Color.RED);
        maximaGraph.setLineThickness(2);
        maximaGraph.setBalloon(new Balloon());
        maximaGraph.setBalloonText("[[category]]<br>Max: [[value]]°C");
        chartModel.addGraphs(maximaGraph);

        // Gráfico de columnas para precipitación
        Graph precipitacionGraph = new Graph();
        precipitacionGraph.setId("precipitacion");
        precipitacionGraph.setValueField("precipitacionTotal");
        precipitacionGraph.setTitle("Precipitación");
        precipitacionGraph.setType(GraphType.COLUMN);
        precipitacionGraph.setFillAlphas(0.8);
        precipitacionGraph.setLineColor(Color.DARKBLUE);
        precipitacionGraph.setValueAxis(precipitacionAxis);
        precipitacionGraph.setBalloon(new Balloon());
        precipitacionGraph.setBalloonText("[[category]]<br>Precip.: [[value]] L/m²");
        chartModel.addGraphs(precipitacionGraph);

        // Configurar la leyenda
        Legend legend = new Legend();
        legend.setUseGraphSettings(true);
        chartModel.setLegend(legend);

        // Configurar datos
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        List<io.jmix.charts.model.DataItem> dataItems = datos.stream()
                .map(m -> {
                    io.jmix.charts.model.DataItem item = new io.jmix.charts.model.DataItem();
                    item.add("fecha", m.getFecha() != null ? m.getFecha().format(formatter) : "");
                    item.add("minimaPromedio", m.getMinimaPromedio() != null ?
                            Math.round(m.getMinimaPromedio() * 10.0) / 10.0 : null);
                    item.add("maximaPromedio", m.getMaximaPromedio() != null ?
                            Math.round(m.getMaximaPromedio() * 10.0) / 10.0 : null);
                    item.add("precipitacionTotal", m.getPrecipitacionTotal() != null ?
                            Math.round(m.getPrecipitacionTotal() * 10.0) / 10.0 : null);
                    return item;
                })
                .toList();

        chartModel.setDataProvider(new ListDataProvider(dataItems));

        chart.setConfiguration(chartModel);
        chart.repaint();
    }
}
