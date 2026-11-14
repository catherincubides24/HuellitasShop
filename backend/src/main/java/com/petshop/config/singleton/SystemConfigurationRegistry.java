package com.petshop.config.singleton;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Patrón de Diseño: Singleton (GoF).
 * <p>
 * Centraliza la administración de configuraciones y metadatos del runtime
 * de Huellitas Shop garantizando una única instancia en memoria durante el ciclo de vida del proceso.
 * <p>
 * Implementación:
 * Utiliza el modismo "Initialization-on-demand holder" (Bill Pugh Singleton Pattern),
 * el cual aprovecha las garantías del ClassLoader de Java para lograr:
 * 1. Inicialización perezosa (Lazy loading): la instancia se crea únicamente cuando se invoca {@link #getInstance()}.
 * 2. Seguridad ante hilos (Thread-safe): sin necesidad de bloques synchronized que penalicen el rendimiento.
 * 3. Inmutabilidad de la referencia del singleton.
 */
public class SystemConfigurationRegistry {

    private final Instant startupTime;
    private final Map<String, Object> systemProperties;

    /**
     * Constructor privado para prevenir instanciación directa desde otras clases.
     */
    private SystemConfigurationRegistry() {
        this.startupTime = Instant.now();
        this.systemProperties = new ConcurrentHashMap<>();
        // Propiedades base del sistema
        this.systemProperties.put("appName", "Huellitas Shop");
        this.systemProperties.put("version", "1.0.0");
        this.systemProperties.put("timezone", "America/Bogota");
        this.systemProperties.put("currency", "COP");
        this.systemProperties.put("environment", "Production-Ready");
    }

    /**
     * Clase estática anidada responsable de mantener la instancia única.
     * La JVM no carga esta clase en memoria hasta que no se invoque {@link #getInstance()}.
     */
    private static class SingletonHolder {
        private static final SystemConfigurationRegistry INSTANCE = new SystemConfigurationRegistry();
    }

    /**
     * Punto global de acceso al Singleton.
     *
     * @return la única instancia de {@link SystemConfigurationRegistry}.
     */
    public static SystemConfigurationRegistry getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public Instant getStartupTime() {
        return startupTime;
    }

    public Object getProperty(String key) {
        return systemProperties.get(key);
    }

    public void setProperty(String key, Object value) {
        if (key != null && value != null) {
            systemProperties.put(key, value);
        }
    }

    public Map<String, Object> getAllProperties() {
        return Map.copyOf(systemProperties);
    }
}
