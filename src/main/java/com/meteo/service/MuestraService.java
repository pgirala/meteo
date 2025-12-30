package com.meteo.service;

import com.meteo.entity.MuestraAcumulada;
import io.jmix.core.DataManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class MuestraService {

    @Autowired
    private DataManager dataManager;

    /**
     * Obtiene datos acumulados agrupados por fecha
     * @param fechaInicio Fecha de inicio (opcional)
     * @param fechaFin Fecha de fin (opcional)
     * @return Lista de muestras acumuladas
     */
    public List<MuestraAcumulada> obtenerDatosAcumulados(LocalDate fechaInicio, LocalDate fechaFin) {
        StringBuilder jpql = new StringBuilder(
            "select new com.meteo.entity.MuestraAcumulada(" +
            "m.fecha, " +
            "avg(m.minima), " +
            "avg(m.maxima), " +
            "sum(m.precipitacion), " +
            "count(m)) " +
            "from Muestra m "
        );

        List<String> conditions = new ArrayList<>();

        if (fechaInicio != null) {
            conditions.add("m.fecha >= :fechaInicio");
        }

        if (fechaFin != null) {
            conditions.add("m.fecha <= :fechaFin");
        }

        if (!conditions.isEmpty()) {
            jpql.append("where ").append(String.join(" and ", conditions)).append(" ");
        }

        jpql.append("group by m.fecha order by m.fecha desc");

        var query = dataManager.load(MuestraAcumulada.class)
                .query(jpql.toString());

        if (fechaInicio != null) {
            query = query.parameter("fechaInicio", fechaInicio);
        }

        if (fechaFin != null) {
            query = query.parameter("fechaFin", fechaFin);
        }

        return query.list();
    }

    /**
     * Obtiene datos de la última semana
     */
    public List<MuestraAcumulada> obtenerUltimaSemana() {
        LocalDate fin = LocalDate.now();
        LocalDate inicio = fin.minusDays(7);
        return obtenerDatosAcumulados(inicio, fin);
    }

    /**
     * Obtiene datos del último mes
     */
    public List<MuestraAcumulada> obtenerUltimoMes() {
        LocalDate fin = LocalDate.now();
        LocalDate inicio = fin.minusMonths(1);
        return obtenerDatosAcumulados(inicio, fin);
    }

    /**
     * Obtiene datos del último trimestre
     */
    public List<MuestraAcumulada> obtenerUltimoTrimestre() {
        LocalDate fin = LocalDate.now();
        LocalDate inicio = fin.minusMonths(3);
        return obtenerDatosAcumulados(inicio, fin);
    }

    /**
     * Obtiene datos del último año
     */
    public List<MuestraAcumulada> obtenerUltimoAnio() {
        LocalDate fin = LocalDate.now();
        LocalDate inicio = fin.minusYears(1);
        return obtenerDatosAcumulados(inicio, fin);
    }
}
