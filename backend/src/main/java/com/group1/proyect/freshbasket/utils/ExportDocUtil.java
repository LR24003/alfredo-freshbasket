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

            Font fontHeaderTitle = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, navyPrimary);
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

            PdfPTable table = new PdfPTable(headers.length);
            table.setWidthPercentage(100);

            if (widths != null && widths.length == headers.length) {
                table.setWidths(widths);
            } else {
                float[] defaultWidths = new float[headers.length];
                Arrays.fill(defaultWidths, 1.0f);
                table.setWidths(defaultWidths);
            }

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

            int rowIndex = 0;
            for (List<String> row : rows) {
                rowIndex++;
                boolean isEven = (rowIndex % 2 == 0);

                for (int colIndex = 0; colIndex < headers.length; colIndex++) {
                    String cellValue = (colIndex < row.size()) ? row.get(colIndex) : "";
                    if (cellValue == null) cellValue = "";

                    if (cellValue.contains("T") && cellValue.split("-").length >= 3) {
                        try {
                            String cleanTarget = cellValue.contains(".") ? cellValue.split("\\.")[0] : cellValue;
                            java.time.LocalDateTime dateTime = java.time.LocalDateTime.parse(cleanTarget);
                            java.time.format.DateTimeFormatter customFormatter =
                                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", java.util.Locale.ENGLISH);
                            cellValue = dateTime.format(customFormatter);
                        } catch (Exception e) {}
                    }

                    boolean isMoney = cellValue.trim().startsWith("$");
                    Font currentFont = isMoney ? fontMoneyColumn : fontTableBody;

                    PdfPCell cell = new PdfPCell(new Phrase(cellValue, currentFont));
                    cell.setPadding(8f);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

                    String currentHeader = headers[colIndex].toUpperCase();

                    // --- AJUSTE DE ALINEACIÓN EN PDF ---
                    if (isMoney || currentHeader.contains("TOTAL") || currentHeader.contains("PRECIO") || currentHeader.contains("GASTADO")) {
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    } else if (currentHeader.equals("ID") || currentHeader.contains("CORREO") || currentHeader.contains("ENTIDAD")|| currentHeader.contains("ROL")) {
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    } else if (currentHeader.contains("FECHA") || currentHeader.contains("HORA") || currentHeader.contains("ACCIÓN")|| currentHeader.contains("PAIS")) {
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    } else if (currentHeader.contains("INVENTARIO") || currentHeader.contains("RAZON") || currentHeader.contains("ESTADO")|| currentHeader.contains("UNIDADES")) {
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    } else {
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    }

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

            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
            headerRow.setHeightInPoints(25);
            for (int i = 0; i < headers.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i].toUpperCase());
                cell.setCellStyle(headerStyle);
            }

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

                        // --- AJUSTE DE DETECCIÓN DE DINERO EN EXCEL ---
                        if ((currentHeader.contains("PRECIO") || currentHeader.contains("TOTAL") || currentHeader.contains("MONTO") || currentHeader.contains("GASTADO"))
                                && !currentHeader.contains("COMPRAS")
                                && !currentHeader.contains("CANTIDAD")
                                && !currentHeader.contains("UNIDADES")
                                && !currentHeader.contains("ENTRADAS")
                                && !currentHeader.contains("SALIDAS")) {  
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