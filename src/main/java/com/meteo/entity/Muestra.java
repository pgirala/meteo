package com.meteo.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.JmixEntity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

@JmixEntity
@Table(name = "MUESTRA", indexes = {
        @Index(name = "IDX_MUESTRA_FECHA", columnList = "FECHA"),
        @Index(name = "IDX_MUESTRA_ESTACION_FECHA", columnList = "ESTACION_ID, FECHA", unique = true)
})
@Entity
public class Muestra {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @NotNull
    @JoinColumn(name = "ESTACION_ID", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Estacion estacion;

    @NotNull
    @Column(name = "FECHA", nullable = false)
    private LocalDate fecha;

    @Column(name = "MINIMA")
    private Double minima;

    @Column(name = "MAXIMA")
    private Double maxima;

    @Column(name = "PRECIPITACION")
    private Double precipitacion;

    public Double getPrecipitacion() {
        return precipitacion;
    }

    public void setPrecipitacion(Double precipitacion) {
        this.precipitacion = precipitacion;
    }

    public Double getMaxima() {
        return maxima;
    }

    public void setMaxima(Double maxima) {
        this.maxima = maxima;
    }

    public Double getMinima() {
        return minima;
    }

    public void setMinima(Double minima) {
        this.minima = minima;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Estacion getEstacion() {
        return estacion;
    }

    public void setEstacion(Estacion estacion) {
        this.estacion = estacion;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @PrePersist
    @PreUpdate
    private void validateMuestra() {
        if (minima == null && maxima == null && precipitacion == null) {
            throw new IllegalStateException("Al menos un parámetro (mínima, máxima o precipitación) debe tener valor");
        }

        if (minima != null && maxima != null && minima > maxima) {
            throw new IllegalStateException("La temperatura mínima no puede ser mayor que la máxima");
        }
    }
}
