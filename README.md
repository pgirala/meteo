# Sistema de Datos Meteorológicos

Sistema de gestión e integración de datos meteorológicos desarrollado con Jmix 2.7.2 y Java 17.

## Descripción

Este proyecto integra datos meteorológicos de tres fuentes principales de España:

- **AEMET** (Agencia Estatal de Meteorología)
- **Meteoclimatic** (Red de estaciones meteorológicas participativas)
- **SAIH TAJO** (Sistema Automático de Información Hidrológica del Tajo)

## Características

- ✅ Modelos de datos para organizaciones, comunidades, provincias, ubicaciones, estaciones y muestras
- ✅ Interfaz de gestión completa con Jmix UI
- ✅ Visualización de datos con gráficos interactivos (temperaturas y precipitación)
- ✅ Integración automática con fuentes de datos mediante web scraping y APIs
- ✅ Soporte multiidioma (Español, Inglés, Francés)
- ✅ Base de datos HSQLDB
- ✅ Tareas programadas para importación automática de datos

## Requisitos

- Java 17+
- Gradle 8.5+

## Instalación

1. Clonar el repositorio:
```bash
git clone <repository-url>
cd meteo
```

2. Compilar el proyecto:
```bash
./gradlew build
```

3. Ejecutar la aplicación:
```bash
./gradlew bootRun
```

4. Acceder a la aplicación en: http://localhost:8080

## Credenciales por defecto

Al ejecutar la aplicación por primera vez, Jmix creará un usuario administrador por defecto:
- Usuario: `admin`
- Contraseña: `admin`

## Estructura del Proyecto

```
meteo/
├── src/main/java/com/meteo/
│   ├── entity/              # Entidades JPA
│   ├── screen/              # Pantallas Jmix UI
│   ├── service/             # Servicios de negocio e integraciones
│   └── MeteoApplication.java
├── src/main/resources/
│   ├── com/meteo/
│   │   ├── screen/          # Descriptores XML de pantallas
│   │   ├── liquibase/       # Scripts de base de datos
│   │   └── menu.xml         # Configuración del menú
│   ├── messages*.properties # Internacionalización
│   └── application.properties
└── build.gradle
```

## Funcionalidades

### Gestión de Datos

- **Organizaciones**: Gestión de fuentes de datos (AEMET, Meteoclimatic, SAIH)
- **Geografía**: Comunidades autónomas, provincias y ubicaciones
- **Estaciones**: Estaciones meteorológicas de cada organización
- **Muestras**: Datos de temperatura y precipitación

### Visualización

- **Datos Acumulados**: Vista con gráficos interactivos mostrando:
  - Temperaturas mínimas y máximas (líneas)
  - Precipitación total (barras)
  - Filtros temporales: última semana, mes, trimestre, año

### Integración de Datos

Los servicios de integración se ejecutan automáticamente:
- **AEMET**: Cada 6 horas
- **Meteoclimatic**: Cada 12 horas
- **SAIH TAJO**: Cada 6 horas

También se pueden ejecutar manualmente desde la aplicación.

## Configuración

### Base de Datos

El proyecto usa HSQLDB por defecto. La configuración se encuentra en `application.properties`:

```properties
main.datasource.url=jdbc:hsqldb:file:.jmix/hsqldb/meteo
main.datasource.username=sa
main.datasource.password=
```

### Zona Horaria

Configurada para Europe/Madrid en `application.properties`.

### Idiomas

Idiomas disponibles: Español (por defecto), Inglés, Francés.

## Tecnologías

- **Framework**: Jmix 2.7.2
- **Spring Boot**: 3.1.5
- **Java**: 17
- **Base de Datos**: HSQLDB
- **ORM**: EclipseLink (JPA)
- **UI**: Jmix UI (Vaadin)
- **Charts**: Jmix Charts
- **Web Scraping**: JSoup
- **JSON**: Gson

## Migración desde Django

Este proyecto es una migración completa del sistema original desarrollado en Django/Python,
replicando toda su funcionalidad en Java con Jmix.

## Licencia

[Especificar licencia]

## Contacto

[Especificar información de contacto]
