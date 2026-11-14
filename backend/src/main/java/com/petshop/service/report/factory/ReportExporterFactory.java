package com.petshop.service.report.factory;

import com.petshop.dto.report.ReportFormat;
import com.petshop.exception.BadRequestException;
import com.petshop.service.report.ReportExporter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Patrón de Diseño: Factory (Fábrica de Exportadores de Reportes).
 * <p>
 * Encapsula la lógica de instanciación y resolución dinámica de exportadores
 * de reportes (como PDF y Excel) en base al formato solicitado (ReportFormat).
 * Cumple con los principios de diseño SOLID:
 * - Single Responsibility Principle (SRP): Centraliza la decisión de qué exportador utilizar.
 * - Open/Closed Principle (OCP): Permite incorporar nuevos formatos (ej. CSV, HTML)
 *   registrando nuevos beans de {@link ReportExporter} sin modificar el código de los servicios consumidores.
 */
@Component
public class ReportExporterFactory {

    private final Map<ReportFormat, ReportExporter> exporters;

    /**
     * Inyección por constructor de todos los exportadores disponibles registrados en el contenedor Spring.
     *
     * @param exporterList lista de componentes que implementan {@link ReportExporter}.
     */
    public ReportExporterFactory(List<ReportExporter> exporterList) {
        this.exporters = exporterList.stream()
                .collect(Collectors.toMap(ReportExporter::format, Function.identity()));
    }

    /**
     * Resuelve el exportador apropiado según el formato especificado.
     *
     * @param format formato solicitado (ej. PDF, EXCEL).
     * @return instancia de {@link ReportExporter} correspondiente.
     * @throws BadRequestException si el formato es nulo o no se encuentra soportado.
     */
    public ReportExporter getExporter(ReportFormat format) {
        if (format == null) {
            throw new BadRequestException("El formato de reporte no puede ser nulo");
        }
        ReportExporter exporter = exporters.get(format);
        if (exporter == null) {
            throw new BadRequestException("Formato de reporte no soportado: " + format);
        }
        return exporter;
    }
}
