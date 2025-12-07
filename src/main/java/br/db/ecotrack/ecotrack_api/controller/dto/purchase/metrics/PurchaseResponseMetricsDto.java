package br.db.ecotrack.ecotrack_api.controller.dto.purchase.metrics;

import java.util.Map;

public record PurchaseResponseMetricsDto(
    int totalQuantityCurrentMonth,
    Map<String, Integer> materialAmountSummary) {
}
