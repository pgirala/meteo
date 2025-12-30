package com.meteo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Programador de tareas para importación automática de datos
 */
@Component
public class DataImportScheduler {

    private static final Logger log = LoggerFactory.getLogger(DataImportScheduler.class);

    @Autowired
    private AemetIntegrationService aemetService;

    @Autowired
    private MeteoclimaticIntegrationService meteoclimaticService;

    @Autowired
    private SaihIntegrationService saihService;

    /**
     * Importa datos de AEMET cada 6 horas
     */
    @Scheduled(cron = "0 0 */6 * * *")
    public void importarDatosAemet() {
        log.info("Tarea programada: Importación AEMET");
        try {
            aemetService.importarDatosAemet();
        } catch (Exception e) {
            log.error("Error en tarea programada de AEMET", e);
        }
    }

    /**
     * Importa datos de Meteoclimatic cada 12 horas
     */
    @Scheduled(cron = "0 30 */12 * * *")
    public void importarDatosMeteoclimatic() {
        log.info("Tarea programada: Importación Meteoclimatic");
        try {
            meteoclimaticService.importarDatosMeteoclimatic();
        } catch (Exception e) {
            log.error("Error en tarea programada de Meteoclimatic", e);
        }
    }

    /**
     * Importa datos de SAIH cada 6 horas
     */
    @Scheduled(cron = "0 15 */6 * * *")
    public void importarDatosSaih() {
        log.info("Tarea programada: Importación SAIH");
        try {
            saihService.importarDatosSaih();
        } catch (Exception e) {
            log.error("Error en tarea programada de SAIH", e);
        }
    }
}
