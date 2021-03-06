package io.github.lbandc.cv19api;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.Getter;

@RestController
@RequestMapping(value = "/api/v1", produces = "application/json")
@AllArgsConstructor
public class ApiV1Controller {

	private final TrustRepository trustRepository;
	private final FileRetriever fileRetriever;

	@GetMapping(value = { "deaths", "deaths/trusts" })
	public DeathSummaryResponse<String> deathsByTrust(
			@RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		date = todayIfNull(date);

		Collection<TrustRepository.TrustDeaths> deathsByTrust = trustRepository.deathsByTrust(date);
		Map<String, Integer> deathsByTrustMap = deathsByTrust.stream().collect(
				Collectors.toMap(TrustRepository.TrustDeaths::getTrust, TrustRepository.TrustDeaths::getDeaths));

		return new DeathSummaryResponse<>(date, deathsByTrustMap);
	}

	@GetMapping("deaths/regions")
	public DeathSummaryResponse<Region> deathsByRegion(
			@RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		date = todayIfNull(date);

		Collection<TrustRepository.RegionDeaths> deathsByRegion = trustRepository.deathsByRegion(date);
		Map<Region, Integer> deathsByRegionMap = deathsByRegion.stream().collect(
				Collectors.toMap(TrustRepository.RegionDeaths::getRegion, TrustRepository.RegionDeaths::getDeaths));

		return new DeathSummaryResponse<>(date, deathsByRegionMap);
	}

	@GetMapping("deaths/{from}/{to}")
	public DeathSummaryResponse<LocalDate> deathsByDay(
			@PathVariable(value = "from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
			@PathVariable(value = "to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

		Collection<TrustRepository.DailyDeaths> dailyDeaths = trustRepository.deathsByDay(from, to);
		Map<LocalDate, Integer> deathsByRegionMap = dailyDeaths.stream().collect(
				Collectors.toMap(TrustRepository.DailyDeaths::getDate, TrustRepository.DailyDeaths::getDeaths));

		return new DeathSummaryResponse<>(to, deathsByRegionMap);
	}

	@PostMapping("admin/ingests/{fileDate}")
	public CommandResponse ingests(
			@PathVariable(value = "fileDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fileDate) {
		this.fileRetriever.fetchFile(fileDate);
		return CommandResponse.OK();
	}

	private static LocalDate todayIfNull(LocalDate param) {
		return param == null ? LocalDate.now() : param;
	}

	@AllArgsConstructor
	@Getter
	private final static class DeathSummaryResponse<T> {
		private final LocalDate date;
		private final Map<T, Integer> deaths;

		public int getTotalDeaths() {
			return deaths.values().stream().mapToInt(i -> i).sum();
		}
	}

	@AllArgsConstructor
	@Getter
	private final static class CommandResponse {
		private final String status;

		static CommandResponse OK() {
			return new CommandResponse("OK");
		}
	}

}
