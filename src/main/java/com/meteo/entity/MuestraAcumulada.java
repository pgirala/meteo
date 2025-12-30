package com.meteo.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO para mostrar muestras acumuladas por fecha
 */
@JmixEntity(name = "MuestraAcumulada")
public class MuestraAcumulada {

    @JmixGeneratedValue
    private UUID id;

    @InstanceName
    private LocalDate fecha;

    private Double minimaPromedio;
    private Double maximaPromedio;
    private Double precipitacionTotal;
    private Long cantidadMuestras;

    public MuestraAcumulada() {
    }

    public MuestraAcumulada(LocalDate fecha, Double minimaPromedio, Double maximaPromedio,
                            Double precipitacionTotal, Long cantidadMuestras) {
        this.id = UUID.randomUUID();
        this.fecha = fecha;
        this.minimaPromedio = minimaPromedio;
        this.maximaPromedio = maximaPromedio;
        this.precipitacionTotal = precipitacionTotal;
        this.cantidadMuestras = cantidadMuestras;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Double getMinimaPromedio() {
        return minimaPromedio;
    }

    public void setMinimaPromedio(Double minimaPromedio) {
        this.minimaPromedio = minimaPromedio;
    }

    public Double getMaximaPromedio() {
        return maximaPromedio;
    }

    public void setMaximaPromedio(Double maximaPromedio) {
        this.maximaPromedio = maximaPromedio;
    }

    public Double getPrecipitacionTotal() {
        return precipitacionTotal;
    }

    public void setPrecipitacionTotal(Double precipitacionTotal) {
        this.precipitacionTotal = precipitacionTotal;
    }

    public Long getCantidadMuestras() {
        return cantidadMuestras;
    }

    public void setCantidadMuestras(Long cantidadMuestras) {
        this.cantidadMuestras = cantidadMuestras;
    }
}
