package de.bp2019.pusl.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.server.VaadinSession;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 
 * @param <T>
 * @author Luca Dinies, Leon Chemnitz
 */
public class ExcelExporter<T> {

    private List<ValueProvider<T, String>> valueProviders;
    private List<String> headers;

    private DataProvider<T, ?> dataProvider;

    public ExcelExporter() {
        removeAllColumns();
    }

    public void addColumn(String header, ValueProvider<T, String> valueProvider) {
        headers.add(header);
        valueProviders.add(valueProvider);
    }

    public void removeAllColumns() {
        valueProviders = new ArrayList<ValueProvider<T, String>>();
        headers = new ArrayList<String>();
    }

    public void setDataProvider(DataProvider<T, ?> dataProvider) {
        this.dataProvider = dataProvider;
    }

    public void createResource(OutputStream outputStream, VaadinSession vaadinSession) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet worksheet = workbook.createSheet();

        XSSFCellStyle headerStyle = workbook.createCellStyle();
        XSSFFont headerFont = workbook.createFont();
        headerFont.setFontName("Arial");
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        XSSFRow headerRow = worksheet.createRow(0);
        for (int i = 0; i < headers.size(); i++) {
            XSSFCell cell = headerRow.createCell(i);
            cell.setCellValue(headers.get(i));
            cell.setCellStyle(headerStyle);
        }

        List<T> items = dataProvider.fetch(new Query<>()).collect(Collectors.toList());

        for (int i = 0; i < items.size(); i++) {
            /* +1 because of header */
            XSSFRow row = worksheet.createRow(i + 1);
            for (int j = 0; j < valueProviders.size(); j++) {
                XSSFCell cell = row.createCell(j);

                String value = valueProviders.get(j).apply(items.get(i));
                if(NumberUtils.isDigits(value)){
                    cell.setCellValue(Integer.parseInt(value));
                }else if(NumberUtils.isParsable(value)) { 
                    cell.setCellValue(Double.parseDouble(value));
                }else{
                    cell.setCellValue(value);
                }
            }
        }

        for (int i = 0; i < headers.size(); i++) {
            worksheet.autoSizeColumn(i);
        }

        try {
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}