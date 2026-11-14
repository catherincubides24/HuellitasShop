package com.petshop.service.report;

import com.petshop.dto.report.ExportedReport;
import com.petshop.dto.report.ReportFilter;
import com.petshop.dto.report.ReportFormat;
import com.petshop.dto.report.ReportSummary;
import com.petshop.entity.PetOrder;
import com.petshop.exception.BadRequestException;
import com.petshop.repository.PetOrderRepository;
import com.petshop.service.report.factory.ReportExporterFactory;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ReportService {

    private final PetOrderRepository orderRepository;
    private final ReportAggregator aggregator;
    private final ReportExporterFactory exporterFactory;

    public ReportService(PetOrderRepository orderRepository,
                         ReportAggregator aggregator,
                         ReportExporterFactory exporterFactory) {
        this.orderRepository = orderRepository;
        this.aggregator = aggregator;
        this.exporterFactory = exporterFactory;
    }

    public ReportSummary buildSummary(ReportFilter filter) {
        if (filter.from() != null && filter.to() != null && filter.from().isAfter(filter.to())) {
            throw new BadRequestException("La fecha inicial no puede ser posterior a la final");
        }
        List<PetOrder> orders = orderRepository.findAll(
                OrderReportSpecifications.from(filter),
                Sort.by(Sort.Direction.DESC, "createdAt"));
        return aggregator.aggregate(orders, filter);
    }

    public ExportedReport export(ReportFormat format, ReportFilter filter) {
        ReportExporter exporter = exporterFactory.getExporter(format);
        byte[] content = exporter.export(buildSummary(filter));
        String fileName = "reporte-huellitas-" + LocalDate.now() + "." + format.extension();
        return new ExportedReport(fileName, format.mediaType(), content);
    }
}