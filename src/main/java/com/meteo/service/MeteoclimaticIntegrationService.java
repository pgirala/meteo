package com.meteo.service;

import com.meteo.entity.*;
import io.jmix.core.DataManager;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class MeteoclimaticIntegrationService {

    private static final Logger log = LoggerFactory.getLogger(MeteoclimaticIntegrationService.class);
    private static final String MC_BASE_URL = "https://www.meteoclimatic.net/mapinfo/ES";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private DataManager dataManager;

    /**
     * Importa datos de Meteoclimatic para los últimos 7 días
     */
    public void importarDatosMeteoclimatic() {
        log.info("Iniciando importación de datos de Meteoclimatic...");

        try {
            Organizacion meteoclimatic = obtenerOrganizacionMeteoclimatic();

            // Obtener comunidades
            List<Comunidad> comunidades = dataManager.load(Comunidad.class).all().list();

            int totalMuestras = 0;

            // Iterar sobre últimos 7 días
            for (int diasAtras = 0; diasAtras < 7; diasAtras++) {
                LocalDate fecha = LocalDate.now().minusDays(diasAtras);

                for (Comunidad comunidad : comunidades) {
                    try {
                        List<Muestra> muestras = obtenerDatosComunidad(
                                comunidad, fecha, meteoclimatic);
                        totalMuestras += muestras.size();
                    } catch (Exception e) {
                        log.warn("Error al obtener datos de comunidad {}: {}",
                                comunidad.getNombre(), e.getMessage());
                    }
                }
            }

            log.info("Importación Meteoclimatic completada: {} muestras procesadas", totalMuestras);

        } catch (Exception e) {
            log.error("Error al importar datos de Meteoclimatic", e);
        }
    }

    private Organizacion obtenerOrganizacionMeteoclimatic() {
        Optional<Organizacion> existing = dataManager.load(Organizacion.class)
                .query("select o from Organizacion o where o.nombre = :nombre")
                .parameter("nombre", "Meteoclimatic")
                .optional();

        if (existing.isPresent()) {
            return existing.get();
        }

        Organizacion mc = dataManager.create(Organizacion.class);
        mc.setNombre("Meteoclimatic");
        mc.setDescripcion("Red de Estaciones Meteorológicas Participativas");
        return dataManager.save(mc);
    }

    private List<Muestra> obtenerDatosComunidad(Comunidad comunidad, LocalDate fecha,
                                                  Organizacion organizacion) {
        List<Muestra> muestras = new ArrayList<>();

        try {
            String url = MC_BASE_URL + comunidad.getCodigo() + "?d=" + fecha.format(DATE_FORMATTER);
            log.debug("Descargando datos de: {}", url);

            Document doc = Jsoup.connect(url)
                    .timeout(30000)
                    .validateTLSCertificates(false)
                    .get();

            Elements estacionElements = doc.select("div.estacion");

            for (Element estacionElement : estacionElements) {
                try {
                    Muestra muestra = parsearEstacionMeteoclimatic(
                            estacionElement, comunidad, fecha, organizacion);
                    if (muestra != null && !existeMuestra(muestra.getEstacion(), muestra.getFecha())) {
                        dataManager.save(muestra);
                        muestras.add(muestra);
                    }
                } catch (Exception e) {
                    log.warn("Error al parsear estación: {}", e.getMessage());
                }
            }

        } catch (Exception e) {
            log.warn("Error al obtener datos de comunidad {}: {}",
                    comunidad.getNombre(), e.getMessage());
        }

        return muestras;
    }

    private Muestra parsearEstacionMeteoclimatic(Element estacionElement, Comunidad comunidad,
                                                   LocalDate fecha, Organizacion organizacion) {
        try {
            // Extraer nombre de estación
            Element nombreElement = estacionElement.selectFirst(".nombre");
            if (nombreElement == null) {
                return null;
            }
            String nombreEstacion = nombreElement.text().trim();

            // Extraer datos meteorológicos
            Element tempMinElement = estacionElement.selectFirst(".temp-min");
            Element tempMaxElement = estacionElement.selectFirst(".temp-max");
            Element precipElement = estacionElement.selectFirst(".precipitacion");

            Double minima = tempMinElement != null ? parseDouble(tempMinElement.text()) : null;
            Double maxima = tempMaxElement != null ? parseDouble(tempMaxElement.text()) : null;
            Double precipitacion = precipElement != null ? parseDouble(precipElement.text()) : null;

            // Si no hay ningún dato, no crear muestra
            if (minima == null && maxima == null && precipitacion == null) {
                return null;
            }

            // Obtener o crear ubicación
            Ubicacion ubicacion = obtenerUbicacion(comunidad.getNombre(), comunidad);

            // Obtener o crear estación
            Estacion estacion = obtenerEstacion(nombreEstacion, organizacion, ubicacion);

            // Crear muestra
            Muestra muestra = dataManager.create(Muestra.class);
            muestra.setEstacion(estacion);
            muestra.setFecha(fecha);
            muestra.setMinima(minima);
            muestra.setMaxima(maxima);
            muestra.setPrecipitacion(precipitacion);

            return muestra;

        } catch (Exception e) {
            log.warn("Error al parsear estación Meteoclimatic: {}", e.getMessage());
            return null;
        }
    }

    private Double parseDouble(String text) {
        if (text == null || text.isEmpty() || text.equals("-") || text.equals("--")) {
            return null;
        }
        try {
            text = text.replaceAll("[^0-9.,\\-]", "").replace(",", ".").trim();
            if (text.isEmpty()) {
                return null;
            }
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Ubicacion obtenerUbicacion(String nombre, Comunidad comunidad) {
        Optional<Ubicacion> existing = dataManager.load(Ubicacion.class)
                .query("select u from Ubicacion u where u.nombre = :nombre and u.comunidad = :comunidad")
                .parameter("nombre", nombre)
                .parameter("comunidad", comunidad)
                .optional();

        if (existing.isPresent()) {
            return existing.get();
        }

        Ubicacion ubicacion = dataManager.create(Ubicacion.class);
        ubicacion.setNombre(nombre);
        ubicacion.setComunidad(comunidad);
        return dataManager.save(ubicacion);
    }

    private Estacion obtenerEstacion(String nombre, Organizacion organizacion, Ubicacion ubicacion) {
        Optional<Estacion> existing = dataManager.load(Estacion.class)
                .query("select e from Estacion e where e.nombre = :nombre and e.organizacion = :org")
                .parameter("nombre", nombre)
                .parameter("org", organizacion)
                .optional();

        if (existing.isPresent()) {
            return existing.get();
        }

        Estacion estacion = dataManager.create(Estacion.class);
        estacion.setNombre(nombre);
        estacion.setOrganizacion(organizacion);
        estacion.setUbicacion(ubicacion);
        return dataManager.save(estacion);
    }

    private boolean existeMuestra(Estacion estacion, LocalDate fecha) {
        Long count = dataManager.loadValue("select count(m) from Muestra m " +
                        "where m.estacion = :estacion and m.fecha = :fecha", Long.class)
                .parameter("estacion", estacion)
                .parameter("fecha", fecha)
                .one();
        return count > 0;
    }
}
