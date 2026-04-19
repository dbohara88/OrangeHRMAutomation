package com.hybrid.utils;

import com.hybrid.constants.FrameworkConstants;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * CLASS COMMENT TEMPLATE:
 * Purpose     : Reads test data from an Excel workbook and converts it into TestNG-friendly Object arrays.
 * Pattern Used: Utility pattern, because Excel reading is a reusable framework service rather than a page or test concern.
 * Used By     : LoginTest data provider and any future data-driven test class.
 * How to Extend: Add methods for specific files, multiple workbooks, or writing result sheets if the project later needs them.
 */
public final class ExcelUtils {

    // DataFormatter converts Excel cell contents into human-readable text without throwing type mismatch issues.
    private static final DataFormatter DATA_FORMATTER = new DataFormatter();

    private ExcelUtils() {
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Reads all data rows from the requested sheet and returns them as Object[][] for TestNG.
     * @param sheetName : The Excel sheet name to read, such as Login.
     * @return      : Two-dimensional Object array containing row and column data.
     * Why this way : TestNG data providers expect Object[][], so converting Excel rows into this structure
     *                lets one test method run multiple times with different data.
     */
    public static Object[][] getTestData(String sheetName) {
        try (FileInputStream fileInputStream = new FileInputStream(FrameworkConstants.TEST_DATA_FILE_PATH);
             /*
                XSSFWorkbook represents the entire .xlsx workbook file in memory.
                We use it because modern Excel files created by QA teams are usually .xlsx, not legacy .xls.
              */
             XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream)) {

            /*
                XSSFSheet represents one worksheet tab inside the workbook, for example the "Login" sheet.
                Each sheet contains rows, which themselves contain cells.
             */
            XSSFSheet sheet = workbook.getSheet(sheetName);

            if (sheet == null) {
                throw new RuntimeException("Sheet not found in Excel file: " + sheetName);
            }

            int totalRows = sheet.getLastRowNum(); // Excludes header row because row numbering starts at zero.
            int totalColumns = sheet.getRow(0).getLastCellNum();

            Object[][] data = new Object[totalRows][totalColumns];

            for (int rowIndex = 1; rowIndex <= totalRows; rowIndex++) {
                /*
                    XSSFRow represents one row in the sheet.
                    Row 0 is the header row, so our test data starts from row 1.
                 */
                XSSFRow row = sheet.getRow(rowIndex);

                for (int columnIndex = 0; columnIndex < totalColumns; columnIndex++) {
                    data[rowIndex - 1][columnIndex] = getCellValue(row, columnIndex);
                }
            }

            return data;
        } catch (IOException exception) {
            throw new RuntimeException("Unable to read Excel test data from: "
                    + FrameworkConstants.TEST_DATA_FILE_PATH, exception);
        }
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Safely reads a cell value as text and converts empty cells to an empty String.
     * @param row          : The row object containing the target cell.
     * @param columnIndex  : Zero-based column index within the row.
     * @return      : Cell value as String, or empty String when the row/cell is missing.
     * Why this way : Returning `\"\"` instead of null makes data-driven tests simpler and avoids NullPointerException.
     */
    private static String getCellValue(XSSFRow row, int columnIndex) {
        if (row == null) {
            return "";
        }

        /*
            XSSFCell represents one individual cell in the row.
            A cell may contain text, number, date, boolean, blank, or formula content.
         */
        XSSFCell cell = row.getCell(columnIndex, XSSFRow.MissingCellPolicy.RETURN_BLANK_AS_NULL);

        if (cell == null) {
            return "";
        }

        return formatCell(cell);
    }

    /**
     * METHOD COMMENT TEMPLATE:
     * What it does : Converts an Apache POI Cell object into trimmed text.
     * @param cell   : The Excel cell whose value should be converted.
     * @return      : Text representation of the cell value.
     * Why this way : DataFormatter handles different Excel cell types consistently, so tests do not need type-specific parsing.
     */
    private static String formatCell(Cell cell) {
        return DATA_FORMATTER.formatCellValue(cell).trim();
    }
}
