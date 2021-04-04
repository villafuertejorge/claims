package com.soproen.claimsmodule.app.Utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Component;

import com.soproen.claimsmodule.app.exceptions.ServiceException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CsvUtils {

	
	public InputStream createCsvFile(List<Object[]> dataList, String[] headers) throws ServiceException {
		try {

			Path tempFile = Files.createTempFile("temp-", ".csv");
			BufferedWriter writer = Files.newBufferedWriter(tempFile);
			@SuppressWarnings("resource")
			CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(headers));
			csvPrinter.printRecords(dataList);

			csvPrinter.flush();
			writer.close();
			return  new FileInputStream(tempFile.toFile());

		} catch (IOException e) {
			log.error("createCsvFile = {} ", e.getMessage());
			throw new ServiceException(e.getMessage());
		}
	}
}
