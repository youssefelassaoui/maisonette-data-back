package gep.ma.maisonette_data_back.Controllers;

import gep.ma.maisonette_data_back.Services.CampusDataByVariableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/campus")
public class CampusDataByVariableController {

    private final CampusDataByVariableService service;

    @Autowired
    public CampusDataByVariableController(CampusDataByVariableService service) {
        this.service = service;
    }

    @GetMapping("/data")
    public ResponseEntity<?> getOptimizedData(
            @RequestParam("house") String house,
            @RequestParam("deviceId") String deviceId,
            @RequestParam(value = "variables", required = false) List<String> variables,
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime) {

        Instant start = null;
        Instant end = null;

        try {
            if (startTime != null) start = Instant.parse(startTime);
            if (endTime != null) end = Instant.parse(endTime);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid time format. Use ISO-8601: YYYY-MM-DDTHH:MM:SSZ");
        }

        try {
            List<Map<String, Object>> responseData = service.getOptimizedData(house, deviceId, variables, start, end);
            return ResponseEntity.ok(responseData);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Query execution failed: " + e.getMessage());
        }
    }
    @GetMapping("/select-options/grouped")
    public ResponseEntity<?> getGroupedHouseDeviceVariables() {
        try {
            Map<String, Map<String, Set<String>>> responseData = service.getGroupedHouseDeviceVariables();
            return ResponseEntity.ok(responseData);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to fetch grouped options: " + e.getMessage());
        }
    }






}

