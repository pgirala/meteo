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

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

@Service
public class AemetIntegrationService {

    private static final Logger log = LoggerFactory.getLogger(AemetIntegrationService.class);
    private static final String AEMET_BASE_URL = "http://www.aemet.es/es/eltiempo/observacion/ultimosdatos";

    @Autowired
    private DataManager dataManager;

    /**
     * Importa datos de AEMET
     */
    public void importarDatosAemet() {
        log.info("Iniciando importación de datos de AEMET...");

        try {
            // Buscar u obtener la organización AEMET
            Organizacion aemet = obtenerOrganizacionAemet();

            // Descargar y parsear datos
            Document doc = Jsoup.connect(AEMET_BASE_URL)
                    .timeout(30000)
                    .validateTLSCertificates(false)
                    .get();

            List<Muestra> muestras = parsearDatosAemet(doc, aemet);

            // Guardar muestras
            int guardadas = 0;
            for (Muestra muestra : muestras) {
                if (!existeMuestra(muestra.getEstacion(), muestra.getFecha())) {
                    dataManager.save(muestra);
                    guardadas++;
                }
            }

            log.info("Importación AEMET completada: {} muestras nuevas guardadas de {} encontradas",
                    guardadas, muestras.size());

        } catch (Exception e) {
            log.error("Error al importar datos de AEMET", e);
        }
    }

    private Organizacion obtenerOrganizacionAemet() {
        Optional<Organizacion> existing = dataManager.load(Organizacion.class)
                .query("select o from Organizacion o where o.nombre = :nombre")
                .parameter("nombre", "AEMET")
                .optional();

        if (existing.isPresent()) {
            return existing.get();
        }

        Organizacion aemet = dataManager.create(Organizacion.class);
        aemet.setNombre("AEMET");
        aemet.setDescripcion("Agencia Estatal de Meteorología");
        return dataManager.save(aemet);
    }

    private List<Muestra> parsearDatosAemet(Document doc, Organizacion aemet) {
        List<Muestra> muestras = new ArrayList<>();

        try {
            // Buscar tablas de datos
            Elements tables = doc.select("table.tabla_datos");

            for (Element table : tables) {
                Elements rows = table.select("tbody tr");

                for (Element row : rows) {
                    try {
                        Muestra muestra = parsearFilaAemet(row, aemet);
                        if (muestra != null) {
                            muestras.add(muestra);
                        }
                    } catch (Exception e) {
                        log.warn("Error al parsear fila de AEMET: {}", e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error al parsear documento de AEMET", e);
        }

        return muestras;
    }

    private Muestra parsearFilaAemet(Element row, Organizacion aemet) {
        Elements cols = row.select("td");
        if (cols.size() < 5) {
            return null;
        }

        try {
            // Extraer nombre de estación
            String nombreEstacion = cols.get(0).text().trim();
            if (nombreEstacion.isEmpty()) {
                return null;
            }

            // Extraer ubicación (provincia)
            String provincia = cols.get(1).text().trim();

            // Extraer temperaturas
            String minimaText = cols.get(2).text().trim();
            String maximaText = cols.get(3).text().trim();
            String precipitacionText = cols.get(4).text().trim();

            Double minima = parsearTemperatura(minimaText);
            Double maxima = parsearTemperatura(maximaText);
            Double precipitacion = parsearPrecipitacion(precipitacionText);

            // Crear o buscar ubicación
            Ubicacion ubicacion = obtenerUbicacion(provincia);

            // Crear o buscar estación
            Estacion estacion = obtenerEstacion(nombreEstacion, aemet, ubicacion);

            // Crear muestra
            Muestra muestra = dataManager.create(Muestra.class);
            muestra.setEstacion(estacion);
            muestra.setFecha(LocalDate.now());
            muestra.setMinima(minima);
            muestra.setMaxima(maxima);
            muestra.setPrecipitacion(precipitacion);

            return muestra;

        } catch (Exception e) {
            log.warn("Error al parsear fila: {}", e.getMessage());
            return null;
        }
    }

    private Double parsearTemperatura(String text) {
        if (text == null || text.isEmpty() || text.equals("-") || text.equals("--")) {
            return null;
        }
        try {
            // Remover símbolos y espacios
            text = text.replace("°", "").replace("C", "").replace(",", ".").trim();
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Double parsearPrecipitacion(String text) {
        if (text == null || text.isEmpty() || text.equals("-") || text.equals("--")) {
            return null;
        }
        try {
            text = text.replace(",", ".").replace("l/m²", "").replace("L/m²", "").trim();
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Ubicacion obtenerUbicacion(String nombreProvincia) {
        // Buscar ubicación existente
        Optional<Ubicacion> existing = dataManager.load(Ubicacion.class)
                .query("select u from Ubicacion u where u.nombre = :nombre")
                .parameter("nombre", nombreProvincia)
                .optional();

        if (existing.isPresent()) {
            return existing.get();
        }

        // Crear nueva ubicación
        Ubicacion ubicacion = dataManager.create(Ubicacion.class);
        ubicacion.setNombre(nombreProvincia);
        return dataManager.save(ubicacion);
    }

    private Estacion obtenerEstacion(String nombre, Organizacion organizacion, Ubicacion ubicacion) {
        // Buscar estación existente
        Optional<Estacion> existing = dataManager.load(Estacion.class)
                .query("select e from Estacion e where e.nombre = :nombre and e.organizacion = :org")
                .parameter("nombre", nombre)
                .parameter("org", organizacion)
                .optional();

        if (existing.isPresent()) {
            return existing.get();
        }

        // Crear nueva estación
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
