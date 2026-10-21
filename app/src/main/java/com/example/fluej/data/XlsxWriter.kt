package com.example.fluej.data

import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object XlsxWriter {
    const val MIME = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"

    fun escrever(tabela: List<List<Any>>, saida: OutputStream) {
        ZipOutputStream(saida).use { zip ->
            fun parte(nome: String, conteudo: String) {
                zip.putNextEntry(ZipEntry(nome))
                zip.write(conteudo.toByteArray(Charsets.UTF_8))
                zip.closeEntry()
            }
            parte("[Content_Types].xml", CONTENT_TYPES)
            parte("_rels/.rels", RELS)
            parte("xl/workbook.xml", WORKBOOK)
            parte("xl/_rels/workbook.xml.rels", WORKBOOK_RELS)
            parte("xl/styles.xml", STYLES)
            parte("xl/worksheets/sheet1.xml", planilha(tabela))
        }
    }

    private fun planilha(tabela: List<List<Any>>): String {
        val sb = StringBuilder()
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>")
        sb.append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">")

        val colunas = tabela.maxOfOrNull { it.size } ?: 1
        sb.append("<cols>")
        for (c in 1..colunas) {
            val largura = if (c == 1) 28 else 18
            sb.append("<col min=\"$c\" max=\"$c\" width=\"$largura\" customWidth=\"1\"/>")
        }
        sb.append("</cols><sheetData>")

        tabela.forEachIndexed { r, linha ->
            val negrito = if (r == 0 || r == tabela.lastIndex) " s=\"1\"" else ""
            sb.append("<row r=\"${r + 1}\">")
            linha.forEachIndexed { c, valor ->
                val ref = "${nomeColuna(c)}${r + 1}"
                if (valor is Number) {
                    sb.append("<c r=\"$ref\"$negrito><v>$valor</v></c>")
                } else {
                    sb.append("<c r=\"$ref\"$negrito t=\"inlineStr\"><is><t xml:space=\"preserve\">")
                    sb.append(escapar(valor.toString()))
                    sb.append("</t></is></c>")
                }
            }
            sb.append("</row>")
        }
        sb.append("</sheetData></worksheet>")
        return sb.toString()
    }

    private fun nomeColuna(indice: Int): String {
        var i = indice
        val sb = StringBuilder()
        do {
            sb.insert(0, 'A' + (i % 26))
            i = i / 26 - 1
        } while (i >= 0)
        return sb.toString()
    }

    private fun escapar(texto: String): String = texto
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")

    private const val CONTENT_TYPES = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types"><Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/><Default Extension="xml" ContentType="application/xml"/><Override PartName="/xl/workbook.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml"/><Override PartName="/xl/worksheets/sheet1.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml"/><Override PartName="/xl/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml"/></Types>"""

    private const val RELS = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships"><Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="xl/workbook.xml"/></Relationships>"""

    private const val WORKBOOK = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<workbook xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships"><sheets><sheet name="Relatorio" sheetId="1" r:id="rId1"/></sheets></workbook>"""

    private const val WORKBOOK_RELS = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships"><Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet" Target="worksheets/sheet1.xml"/><Relationship Id="rId2" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/></Relationships>"""

    private const val STYLES = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<styleSheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main"><fonts count="2"><font><sz val="11"/><name val="Calibri"/></font><font><b/><sz val="11"/><name val="Calibri"/></font></fonts><fills count="2"><fill><patternFill patternType="none"/></fill><fill><patternFill patternType="gray125"/></fill></fills><borders count="1"><border><left/><right/><top/><bottom/><diagonal/></border></borders><cellStyleXfs count="1"><xf numFmtId="0" fontId="0" fillId="0" borderId="0"/></cellStyleXfs><cellXfs count="2"><xf numFmtId="0" fontId="0" fillId="0" borderId="0" xfId="0"/><xf numFmtId="0" fontId="1" fillId="0" borderId="0" xfId="0" applyFont="1"/></cellXfs></styleSheet>"""
}