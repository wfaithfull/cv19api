package io.github.lbandc.cv19api;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Profile("!testData")
@Slf4j
public class FileRetriever {

	private static String URI = "https://www.england.nhs.uk/statistics/wp-content/uploads/sites/2/";

	private final TrustRepository trustRepository;
	private final IngestRepository ingestRepository;

	@PostConstruct
	void onStartup() {
		// get yesterday's file
		this.fetchFile(LocalDate.now().minusDays(1));
	}

	@Scheduled(cron = "0 10,14,17,21 * * * *")
	public void fetchTodaysFile() {
		this.fetchFile(LocalDate.now());
	}

	@Transactional
	public void fetchFile(LocalDate now) {

		try {
			String month = now.getMonthValue() < 10 ? "0" + now.getMonthValue() : String.valueOf(now.getMonthValue());
			String filePath = "COVID-19-daily-announced-deaths-" + now.getDayOfMonth() + "-"
					+ now.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + "-2020.xlsx";
			URL url = new URL(URI + now.getYear() + "/" + month + "/" + filePath);

			Ingest ingest;
			if (!ingestRepository.existsByUrl(url.toString())) {
				ingest = ingestRepository.save(new Ingest(url.toString(), Instant.now()));
			} else {
				log.warn("Already ingested {}. Skipping.", url.toString());
				return;
			}

			List<Trust> models = new TrustSheetParser(url).parse();
			List<Trust> merged = models.stream().map(trust -> {
				if (trustRepository.existsById(trust.getCode())) {
					Trust existing = trustRepository.findById(trust.getCode()).get();
					trust.getDeaths().forEach((k, v) -> existing.getDeaths().merge(k, v, Integer::sum));
				}

				trust.getSources().add(ingest);
				return trust;
			}).collect(Collectors.toList());

			trustRepository.saveAll(merged);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
