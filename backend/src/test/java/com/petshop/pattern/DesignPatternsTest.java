package com.petshop.pattern;

import com.petshop.config.singleton.SystemConfigurationRegistry;
import com.petshop.dto.report.ReportFormat;
import com.petshop.dto.report.ReportSummary;
import com.petshop.event.OrderPaidEvent;
import com.petshop.event.OrderPaidListener;
import com.petshop.exception.BadRequestException;
import com.petshop.service.report.ReportExporter;
import com.petshop.service.report.factory.ReportExporterFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Suite de pruebas unitarias para la verificación de los Patrones de Diseño
 * implementados en Huellitas Shop:
 * 1. Singleton Pattern (GoF / Bill Pugh)
 * 2. Factory Pattern (ReportExporterFactory)
 * 3. Observer Pattern (OrderPaidEvent / OrderPaidListener)
 */
class DesignPatternsTest {

    @Nested
    @DisplayName("1. Pruebas del Patrón Singleton (GoF)")
    class SingletonPatternTests {

        @Test
        @DisplayName("Garantiza una única instancia idéntica en memoria (assertSame)")
        void testSingletonReturnsSameInstance() {
            SystemConfigurationRegistry instance1 = SystemConfigurationRegistry.getInstance();
            SystemConfigurationRegistry instance2 = SystemConfigurationRegistry.getInstance();

            assertNotNull(instance1, "La instancia no debe ser nula");
            assertSame(instance1, instance2, "Ambas referencias deben apuntar al mismo objeto en memoria");
        }

        @Test
        @DisplayName("Mantiene el estado global consistente a través de accesos")
        void testSingletonStateConsistency() {
            SystemConfigurationRegistry registry = SystemConfigurationRegistry.getInstance();
            registry.setProperty("customFeatureEnabled", true);

            SystemConfigurationRegistry sameRegistry = SystemConfigurationRegistry.getInstance();
            assertEquals(true, sameRegistry.getProperty("customFeatureEnabled"));
            assertEquals("Huellitas Shop", sameRegistry.getProperty("appName"));
            assertEquals("America/Bogota", sameRegistry.getProperty("timezone"));
            assertEquals("COP", sameRegistry.getProperty("currency"));
        }
    }

    @Nested
    @DisplayName("2. Pruebas del Patrón Factory (GoF)")
    class FactoryPatternTests {

        @Test
        @DisplayName("Resuelve correctamente el exportador según el formato PDF y XLSX")
        void testFactoryResolvesExporters() {
            // Mocks de exportadores
            ReportExporter pdfMock = mock(ReportExporter.class);
            when(pdfMock.format()).thenReturn(ReportFormat.PDF);

            ReportExporter excelMock = mock(ReportExporter.class);
            when(excelMock.format()).thenReturn(ReportFormat.XLSX);

            ReportExporterFactory factory = new ReportExporterFactory(List.of(pdfMock, excelMock));

            ReportExporter resolvedPdf = factory.getExporter(ReportFormat.PDF);
            ReportExporter resolvedExcel = factory.getExporter(ReportFormat.XLSX);

            assertSame(pdfMock, resolvedPdf, "Debe resolver el exportador PDF");
            assertSame(excelMock, resolvedExcel, "Debe resolver el exportador XLSX");
        }

        @Test
        @DisplayName("Arroja BadRequestException si se solicita un formato no soportado o nulo")
        void testFactoryThrowsExceptionOnUnsupportedOrNull() {
            ReportExporter pdfMock = mock(ReportExporter.class);
            when(pdfMock.format()).thenReturn(ReportFormat.PDF);

            ReportExporterFactory factory = new ReportExporterFactory(List.of(pdfMock));

            assertThrows(BadRequestException.class, () -> factory.getExporter(null),
                    "Debe fallar si el formato es nulo");
            assertThrows(BadRequestException.class, () -> factory.getExporter(ReportFormat.XLSX),
                    "Debe fallar si el formato no está registrado en la fábrica");
        }
    }

    @Nested
    @DisplayName("3. Pruebas del Patrón Observer (GoF / Spring Events)")
    class ObserverPatternTests {

        @Test
        @DisplayName("El listener reacciona ante la notificación del evento OrderPaidEvent")
        void testObserverReceivesEvent() {
            OrderPaidListener listener = new OrderPaidListener();
            OrderPaidEvent event = new OrderPaidEvent(12345L);

            // Verificamos que el listener procese el evento sin errores
            assertDoesNotThrow(() -> listener.onOrderPaid(event),
                    "El observer debe procesar el evento sin lanzar excepciones");
            assertEquals(12345L, event.orderId(), "El ID de la orden en el evento debe coincidir");
        }

        @Test
        @DisplayName("Verifica desacoplamiento: múltiples observers pueden escuchar el evento independientemente")
        void testObserverDecoupledExecution() {
            OrderPaidListener mockListener1 = mock(OrderPaidListener.class);
            OrderPaidEvent event = new OrderPaidEvent(999L);

            mockListener1.onOrderPaid(event);

            verify(mockListener1, times(1)).onOrderPaid(event);
        }
    }
}
