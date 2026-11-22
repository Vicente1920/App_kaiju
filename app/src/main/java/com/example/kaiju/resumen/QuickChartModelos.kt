package com.example.kaiju.resumen

data class QuickChartSolicitud(
    val version: String = "4",
    val format: String = "png",
    val width: Int = 800,
    val height: Int = 450,
    val devicePixelRatio: Int = 1,
    val chart: QuickChartConfig
)

data class QuickChartConfig(
    val type: String = "bar",
    val data: QuickChartData
)

data class QuickChartData(
    val labels: List<String>,
    val datasets: List<QuickChartDataset>
)

data class QuickChartDataset(
    val label: String,
    val data: List<Int>,
    val backgroundColor: List<String>
)

