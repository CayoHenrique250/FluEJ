package com.example.fluej.data

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

enum class Periodo(val rotulo: String) {
    SEMANAL("Semanal"),
    MENSAL("Mensal"),
    ANUAL("Anual")
}

object ReportDates {
    private fun localIso() = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private fun utcIso() = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    fun intervalo(periodo: Periodo): Pair<String, String> {
        val fmt = localIso()
        val cal = Calendar.getInstance()
        return when (periodo) {
            Periodo.SEMANAL -> {
                val diasDesdeSegunda = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7
                cal.add(Calendar.DAY_OF_YEAR, -diasDesdeSegunda)
                val inicio = fmt.format(cal.time)
                cal.add(Calendar.DAY_OF_YEAR, 6)
                inicio to fmt.format(cal.time)
            }

            Periodo.MENSAL -> {
                cal.set(Calendar.DAY_OF_MONTH, 1)
                val inicio = fmt.format(cal.time)
                cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
                inicio to fmt.format(cal.time)
            }

            Periodo.ANUAL -> {
                val ano = cal.get(Calendar.YEAR)
                "$ano-01-01" to "$ano-12-31"
            }
        }
    }

    fun isoDeMillisUtc(millis: Long): String = utcIso().format(Date(millis))

    fun millisUtcDeIso(iso: String): Long? =
        try {
            utcIso().parse(iso)?.time
        } catch (e: Exception) {
            null
        }

    fun br(iso: String): String {
        val p = iso.split("-")
        return if (p.size == 3) "${p[2]}/${p[1]}/${p[0]}" else iso
    }
}

fun formatarHoras(horas: Double): String {
    val minutosTotais = (horas * 60).toInt()
    val h = minutosTotais / 60
    val m = minutosTotais % 60

    return when {
        h > 0 && m > 0 -> "${h}h ${m}m"
        h > 0 -> "${h}h"
        else -> "${m}m"
    }
}

data class LinhaRelatorio(
    val nome: String,
    val cargo: String,
    val setor: String,
    val pontosPorMes: List<Int>,
    val totalPontos: Int,
    val totalHoras: Double
)

data class Relatorio(
    val inicio: String,
    val fim: String,
    val meses: List<String>,
    val linhas: List<LinhaRelatorio>
) {
    val totalPontos: Int get() = linhas.sumOf { it.totalPontos }
    val totalHoras: Double get() = linhas.sumOf { it.totalHoras }

    private val mostrarMeses: Boolean get() = meses.size > 1

    private fun totaisPorMes(): List<Int> =
        meses.indices.map { i -> linhas.sumOf { it.pontosPorMes[i] } }

    val cabecalho: List<String>
        get() = buildList<String> {
            add("Nome"); add("Cargo"); add("Setor")
            if (mostrarMeses) meses.forEach { add("Pontos $it") }
            add("Total de pontos"); add("Total de horas")
        }

    fun linhasTexto(): List<List<String>> = linhas.map { l ->
        buildList<String> {
            add(l.nome); add(l.cargo); add(l.setor)
            if (mostrarMeses) l.pontosPorMes.forEach { add(it.toString()) }
            add(l.totalPontos.toString()); add(formatarHoras(l.totalHoras))
        }
    }

    fun totalTexto(): List<String> = buildList<String> {
        add("Total"); add(""); add("")
        if (mostrarMeses) totaisPorMes().forEach { add(it.toString()) }
        add(totalPontos.toString()); add(formatarHoras(totalHoras))
    }

    fun tabelaExcel(): List<List<Any>> {
        val corpo = linhas.map { l ->
            buildList<Any> {
                add(l.nome); add(l.cargo); add(l.setor)
                if (mostrarMeses) addAll(l.pontosPorMes)
                add(l.totalPontos); add(l.totalHoras)
            }
        }
        val total = buildList<Any> {
            add("Total"); add(""); add("")
            if (mostrarMeses) addAll(totaisPorMes())
            add(totalPontos); add(totalHoras)
        }
        return listOf<List<Any>>(cabecalho) + corpo + listOf(total)
    }
}

object RelatorioBuilder {
    private val nomesMeses =
        listOf("Jan", "Fev", "Mar", "Abr", "Mai", "Jun", "Jul", "Ago", "Set", "Out", "Nov", "Dez")

    private fun mesesDoIntervalo(inicio: String, fim: String): List<String> {
        var ano = inicio.substring(0, 4).toInt()
        var mes = inicio.substring(5, 7).toInt()
        val anoFim = fim.substring(0, 4).toInt()
        val mesFim = fim.substring(5, 7).toInt()
        val lista = mutableListOf<String>()
        while (ano < anoFim || (ano == anoFim && mes <= mesFim)) {
            lista += String.format(Locale.US, "%04d-%02d", ano, mes)
            mes++
            if (mes > 12) {
                mes = 1; ano++
            }
        }
        return lista
    }

    private fun rotulo(anoMes: String): String =
        "${nomesMeses[anoMes.substring(5, 7).toInt() - 1]}/${anoMes.substring(0, 4)}"

    fun construir(
        inicio: String,
        fim: String,
        membros: List<Member>,
        atividades: List<Activity>
    ): Relatorio {
        val chaves = mesesDoIntervalo(inicio, fim)
        val porMembro = atividades.groupBy { it.memberId }

        val linhas = membros.sortedBy { it.nome.lowercase() }.map { m ->
            val atv = porMembro[m.id].orEmpty()
            LinhaRelatorio(
                nome = m.nome,
                cargo = m.cargo,
                setor = m.setor,
                pontosPorMes = chaves.map { chave ->
                    atv.filter { it.data.startsWith(chave) }.sumOf { it.pontos }
                },
                totalPontos = atv.sumOf { it.pontos },
                totalHoras = atv.sumOf { it.horas }
            )
        }
        return Relatorio(inicio, fim, chaves.map(::rotulo), linhas)
    }
}