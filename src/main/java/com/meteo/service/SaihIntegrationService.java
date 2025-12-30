package com.meteo.service;

import com.google.gson.*;
import com.meteo.entity.*;
import io.jmix.core.DataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class SaihIntegrationService {

    private static final Logger log = LoggerFactory.getLogger(SaihIntegrationService.class);
    private static final String SAIH_BASE_URL = "https://saihtajo.chtajo.es/ajax.php?url=/tr/ajax_datos_tab2/estacion:";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Autowired
    private DataManager dataManager;

    /**
     * Importa datos de SAIH TAJO para las estaciones configuradas
     */
    public void importarDatosSaih() {
        log.info("Iniciando importación de datos de SAIH TAJO...");

        try {
            Organizacion saih = obtenerOrganizacionSaih();

            // Obtener estaciones SAIH existentes
            List<Estacion> estaciones = dataManager.load(Estacion.class)
                    .query("select e from Estacion e where e.organizacion = :org")
                    .parameter("org", saih)
                    .list();

            int totalMuestras = 0;

            for (Estacion estacion : estaciones) {
                if (estacion.getCodigoExterno() != null && !estacion.getCodigoExterno().isEmpty()) {
                    try {
                        List<Muestra> muestras = obtenerDatosEstacion(estacion);
                        totalMuestras += muestras.size();
                    } catch (Exception e) {
                        log.warn("Error al obtener datos de estación {}: {}",
                                estacion.getNombre(), e.getMessage());
                    }
                }
            }

            log.info("Importación SAIH completada: {} muestras procesadas", totalMuestras);

        } catch (Exception e) {
            log.error("Error al importar datos de SAIH", e);
        }
    }

    /**
     * Importa datos de una estación SAIH específica por ID
     */
    public void importarDatosEstacion(String estacionId) {
        try {
            Organizacion saih = obtenerOrganizacionSaih();

            // Buscar estación por código externo
            Optional<Estacion> estacionOpt = dataManager.load(Estacion.class)
                    .query("select e from Estacion e where e.codigoExterno = :codigo and e.organizacion = :org")
                    .parameter("codigo", estacionId)
                    .parameter("org", saih)
                    .optional();

            if (estacionOpt.isPresent()) {
                obtenerDatosEstacion(estacionOpt.get());
            } else {
                log.warn("Estación con código {} no encontrada", estacionId);
            }
        } catch (Exception e) {
            log.error("Error al importar datos de estación {}", estacionId, e);
        }
    }

    private Organizacion obtenerOrganizacionSaih() {
        Optional<Organizacion> existing = dataManager.load(Organizacion.class)
                .query("select o from Organizacion o where o.nombre = :nombre")
                .parameter("nombre", "SAIH TAJO")
                .optional();

        if (existing.isPresent()) {
            return existing.get();
        }

        Organizacion saih = dataManager.create(Organizacion.class);
        saih.setNombre("SAIH TAJO");
        saih.setDescripcion("Sistema Automático de Información Hidrológica del Tajo");
        return dataManager.save(saih);
    }

    private List<Muestra> obtenerDatosEstacion(Estacion estacion) {
        List<Muestra> muestras = new ArrayList<>();

        try {
            String url = SAIH_BASE_URL + estacion.getCodigoExterno();
            log.debug("Descargando datos de: {}", url);

            String jsonResponse = descargarJson(url);
            JsonObject jsonObject = JsonParser.parseString(jsonResponse).getAsJsonObject();

            // Parsear datos
            if (jsonObject.has("datos") && jsonObject.get("datos").isJsonArray()) {
                JsonArray datosArray = jsonObject.getAsJsonArray("datos");

                for (JsonElement elemento : datosArray) {
                    try {
                        Muestra muestra = parsearDatoSaih(elemento.getAsJsonObject(), estacion);
                        if (muestra != null && !existeMuestra(muestra.getEstacion(), muestra.getFecha())) {
                            dataManager.save(muestra);
                            muestras.add(muestra);
                        }
                    } catch (Exception e) {
                        log.warn("Error al parsear dato SAIH: {}", e.getMessage());
                    }
                }
            }

        } catch (Exception e) {
            log.warn("Error al obtener datos de estación {}: {}",
                    estacion.getNombre(), e.getMessage());
        }

        return muestras;
    }

    private String descargarJson(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(30000);

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    private Muestra parsearDatoSaih(JsonObject dato, Estacion estacion) {
        try {
            // Extraer fecha
            if (!dato.has("fecha")) {
                return null;
            }

            String fechaStr = dato.get("fecha").getAsString();
            LocalDate fecha = LocalDate.parse(fechaStr, DATE_FORMATTER);

            // Extraer temperaturas y precipitación
            Double minima = extraerValor(dato, "temp_min");
            Double maxima = extraerValor(dato, "temp_max");
            Double precipitacion = extraerValor(dato, "precipitacion");

            // Si no hay ningún dato, no crear muestra
            if (minima == null && maxima == null && precipitacion == null) {
                return null;
            }

            // Crear muestra
            Muestra muestra = dataManager.create(Muestra.class);
            muestra.setEstacion(estacion);
            muestra.setFecha(fecha);
            muestra.setMinima(minima);
            muestra.setMaxima(maxima);
            muestra.setPrecipitacion(precipitacion);

            return muestra;

        } catch (Exception e) {
            log.warn("Error al parsear dato SAIH: {}", e.getMessage());
            return null;
        }
    }

    private Double extraerValor(JsonObject dato, String campo) {
        if (!dato.has(campo)) {
            return null;
        }

        JsonElement elemento = dato.get(campo);
        if (elemento.isJsonNull()) {
            return null;
        }

        try {
            if (elemento.isJsonPrimitive()) {
                JsonPrimitive primitivo = elemento.getAsJsonPrimitive();
                if (primitivo.isNumber()) {
                    return primitivo.getAsDouble();
                } else if (primitivo.isString()) {
                    String valor = primitivo.getAsString();
                    if (valor.isEmpty() || valor.equals("-") || valor.equals("--")) {
                        return null;
                    }
                    return Double.parseDouble(valor.replace(",", "."));
                }
            }
        } catch (NumberFormatException e) {
            log.warn("Error al parsear valor numérico: {}", e.getMessage());
        }

        return null;
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
