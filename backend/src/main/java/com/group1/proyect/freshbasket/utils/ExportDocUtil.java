package com.group1.proyect.freshbasket.utils;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Element;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ExportDocUtil {

    public static byte[] toPdf(String title, String[] headers, float[] widths, List<List<String>> rows) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate(), 40, 40, 40, 40);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Color navyPrimary = new Color(31, 73, 125);
            Color greenFresh = new Color(46, 139, 87);
            Color greenDarkHeader = new Color(38, 70, 83);
            Color slateDarkText = new Color(44, 62, 80);
            Color greenZebraLight = new Color(240, 245, 241);
            Color grayBorder = new Color(230, 235, 232);

            // Fuentes estandarizadas
            Font fontHeaderTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, navyPrimary);
            Font fontSubtitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, greenFresh);
            Font fontTableHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
            Font fontTableBody = FontFactory.getFont(FontFactory.HELVETICA, 9, slateDarkText);
            Font fontMoneyColumn = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, greenFresh);

            Paragraph companyTitle = new Paragraph("FreshBasket", fontHeaderTitle);
            document.add(companyTitle);

            Paragraph reportSubtitle = new Paragraph(title.toUpperCase(), fontSubtitle);
            reportSubtitle.setSpacingAfter(10);
            document.add(reportSubtitle);

            PdfPTable lineTable = new PdfPTable(1);
            lineTable.setWidthPercentage(100);
            PdfPCell lineCell = new PdfPCell();
            lineCell.setBorder(Rectangle.BOTTOM);
            lineCell.setBorderWidthBottom(3f);
            lineCell.setBorderColor(greenFresh);
            lineTable.addCell(lineCell);
            document.add(lineTable);

            Paragraph spacer = new Paragraph(" ");
            spacer.setSpacingAfter(10);
            document.add(spacer);

            // Inicialización segura de la Tabla Principal
            PdfPTable table = new PdfPTable(headers.length);
            table.setWidthPercentage(100);

            // Validación robusta de anchos de columnas
            if (widths != null && widths.length == headers.length) {
                table.setWidths(widths);
            } else {
                float[] defaultWidths = new float[headers.length];
                Arrays.fill(defaultWidths, 1.0f);
                table.setWidths(defaultWidths);
            }

            // Construcción del Header del PDF
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header.toUpperCase(), fontTableHeader));
                cell.setBackgroundColor(greenDarkHeader);
                cell.setPaddingTop(10f);
                cell.setPaddingBottom(10f);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
            }

            // Construcción del Cuerpo de la Tabla
            int rowIndex = 0;
            for (List<String> row : rows) {
                rowIndex++;
                boolean isEven = (rowIndex % 2 == 0);

                for (int colIndex = 0; colIndex < headers.length; colIndex++) {
                    String cellValue = (colIndex < row.size()) ? row.get(colIndex) : "";
                    if (cellValue == null) cellValue = "";

                    // INTERCEPCIÓN Y FORMATEO DE FECHAS EN PDF (Formato 24 Horas: dd/MM/yyyy HH:mm)
                    if (cellValue.contains("T") && cellValue.split("-").length >= 3) {
                        try {
                            // Remueve nanosegundos (.164910) si vienen de la BD
                            String cleanTarget = cellValue.contains(".") ? cellValue.split("\\.")[0] : cellValue;

                            java.time.LocalDateTime dateTime = java.time.LocalDateTime.parse(cleanTarget);
                            java.time.format.DateTimeFormatter customFormatter =
                                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", java.util.Locale.ENGLISH);

                            cellValue = dateTime.format(customFormatter);
                        } catch (Exception e) {
                        }
                    }

                    // Detección de moneda para aplicar color verde destacado
                    boolean isMoney = cellValue.trim().startsWith("$");
                    Font currentFont = isMoney ? fontMoneyColumn : fontTableBody;

                    PdfPCell cell = new PdfPCell(new Phrase(cellValue, currentFont));
                    cell.setPadding(8f);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

                    String currentHeader = headers[colIndex].toUpperCase();

                    if (isMoney || currentHeader.contains("TOTAL") || currentHeader.contains("PRECIO") || currentHeader.contains("GASTADO")) {
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    } else if (currentHeader.equals("ID") || currentHeader.contains("CÓDIGO") || currentHeader.contains("ENTIDAD")) {
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    } else if (currentHeader.contains("FECHA") || currentHeader.contains("HORA") || currentHeader.contains("ACCIÓN") || currentHeader.contains("ACCIÓN")) {
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    } else {
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT); // Textos y nombres a la izquierda
                    }

                    // Estilo visual de la celda (Cebra y Bordes Inferiores)
                    cell.setBackgroundColor(isEven ? greenZebraLight : Color.WHITE);
                    cell.setBorder(Rectangle.BOTTOM);
                    cell.setBorderColor(grayBorder);
                    cell.setBorderWidthBottom(1f);

                    table.addCell(cell);
                }
            }

            document.add(table);
            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error global al generar el PDF dinámico en FreshBasket", e);
        }
    }


    /**
     * Exporta datos a un archivo Excel altamente dinámico y adaptable.
     */
    public static byte[] toExcel(String sheetName, String[] headers, List<Map<String, Object>> rows) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(sheetName);
            sheet.setDisplayGridlines(true);

            byte[] colorHeader = new byte[]{(byte) 38, (byte) 70, (byte) 83};
            byte[] colorZebra = new byte[]{(byte) 240, (byte) 245, (byte) 241};

            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setFontName("Arial");
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());

            org.apache.poi.ss.usermodel.Font dataFont = workbook.createFont();
            dataFont.setFontName("Arial");

            org.apache.poi.ss.usermodel.Font moneyFont = workbook.createFont();
            moneyFont.setFontName("Arial");
            moneyFont.setBold(true);
            moneyFont.setColor(IndexedColors.SEA_GREEN.getIndex());

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(new XSSFColor(colorHeader, null));
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setFont(headerFont);

            // --- ESTILOS CENTRADOS (Números, IDs, Totales) ---
            CellStyle cellStyleCenterNormal = workbook.createCellStyle();
            cellStyleCenterNormal.setFont(dataFont);
            cellStyleCenterNormal.setAlignment(HorizontalAlignment.CENTER);
            cellStyleCenterNormal.setVerticalAlignment(VerticalAlignment.CENTER);

            CellStyle cellStyleCenterZebra = workbook.createCellStyle();
            cellStyleCenterZebra.setFillForegroundColor(new XSSFColor(colorZebra, null));
            cellStyleCenterZebra.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyleCenterZebra.setFont(dataFont);
            cellStyleCenterZebra.setAlignment(HorizontalAlignment.CENTER);
            cellStyleCenterZebra.setVerticalAlignment(VerticalAlignment.CENTER);

            // --- NUEVOS ESTILOS IZQUIERDA (Nombres, Correos, Entidades) ---
            CellStyle cellStyleLeftNormal = workbook.createCellStyle();
            cellStyleLeftNormal.setFont(dataFont);
            cellStyleLeftNormal.setAlignment(HorizontalAlignment.LEFT);
            cellStyleLeftNormal.setVerticalAlignment(VerticalAlignment.CENTER);

            CellStyle cellStyleLeftZebra = workbook.createCellStyle();
            cellStyleLeftZebra.setFillForegroundColor(new XSSFColor(colorZebra, null));
            cellStyleLeftZebra.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyleLeftZebra.setFont(dataFont);
            cellStyleLeftZebra.setAlignment(HorizontalAlignment.LEFT);
            cellStyleLeftZebra.setVerticalAlignment(VerticalAlignment.CENTER);
            CellStyle moneyStyleNormal = workbook.createCellStyle();
            moneyStyleNormal.setFont(moneyFont);
            moneyStyleNormal.setDataFormat(workbook.createDataFormat().getFormat("$#,##0.00"));
            moneyStyleNormal.setAlignment(HorizontalAlignment.CENTER);
            moneyStyleNormal.setVerticalAlignment(VerticalAlignment.CENTER);

            CellStyle moneyStyleZebra = workbook.createCellStyle();
            moneyStyleZebra.setFillForegroundColor(new XSSFColor(colorZebra, null));
            moneyStyleZebra.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            moneyStyleZebra.setFont(moneyFont);
            moneyStyleZebra.setDataFormat(workbook.createDataFormat().getFormat("$#,##0.00"));
            moneyStyleZebra.setAlignment(HorizontalAlignment.CENTER);
            moneyStyleZebra.setVerticalAlignment(VerticalAlignment.CENTER);

            // Crear fila de encabezados
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
            headerRow.setHeightInPoints(25);
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i].toUpperCase());
                cell.setCellStyle(headerStyle);
            }

            // Procesar Filas
            int rowIdx = 1;
            for (Map<String, Object> rowData : rows) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowIdx++);
                row.setHeightInPoints(20);
                boolean isEven = (rowIdx % 2 == 0);

                Object[] keys = rowData.keySet().toArray();

                for (int colIdx = 0; colIdx < headers.length; colIdx++) {
                    if (colIdx >= keys.length) break;

                    org.apache.poi.ss.usermodel.Cell cell = row.createCell(colIdx);
                    Object val = rowData.get(keys[colIdx]);

                    boolean isMoney = false;
                    String currentHeader = headers[colIdx].toUpperCase();

                    if (val instanceof Number) {
                        cell.setCellValue(((Number) val).doubleValue());

                        if ((currentHeader.contains("PRECIO") || currentHeader.contains("TOTAL") || currentHeader.contains("MONTO") || currentHeader.contains("GASTADO"))
                                && !currentHeader.contains("COMPRAS") && !currentHeader.contains("CANTIDAD") && !currentHeader.contains("UNIDADES")) {
                            isMoney = true;
                        }
                    } else {
                        String textVal = val != null ? val.toString() : "";

                        if (textVal.contains("T") && textVal.split("-").length >= 3) {
                            try {
                                String cleanTarget = textVal.contains(".") ? textVal.split("\\.")[0] : textVal;
                                java.time.LocalDateTime dateTime = java.time.LocalDateTime.parse(cleanTarget);
                                java.time.format.DateTimeFormatter customFormatter =
                                        java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", java.util.Locale.ENGLISH);
                                textVal = dateTime.format(customFormatter);
                            } catch (Exception e) {}
                        }

                        cell.setCellValue(textVal);
                        if (textVal.trim().startsWith("$")) {
                            isMoney = true;
                        }
                    }

                    boolean alignLeft = false;
                    if (currentHeader.contains("NOMBRE") || currentHeader.contains("CORREO") || currentHeader.contains("EMAIL") || currentHeader.contains("DESCRIPCIÓN")) {
                        alignLeft = true;
                    } else if (currentHeader.contains("ENTIDAD") || currentHeader.contains("CLIENTE") || currentHeader.contains("PROVEEDOR")) {
                        alignLeft = true;
                    }

                    CellStyle finalStyle;
                    if (isMoney) {
                        finalStyle = isEven ? moneyStyleZebra : moneyStyleNormal;
                    } else if (alignLeft) {
                        finalStyle = isEven ? cellStyleLeftZebra : cellStyleLeftNormal;
                    } else {
                        finalStyle = isEven ? cellStyleCenterZebra : cellStyleCenterNormal;
                    }

                    cell.setCellStyle(finalStyle);
                }
            }

            // Auto-ajuste de columnas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1200);
            }

            workbook.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Error global al generar el reporte de Excel dinámico en FreshBasket", e);
        }
    }
}