package gep.ma.maisonette_data_back.Services;


import gep.ma.maisonette_data_back.Models.CampusDataByVariable;
import gep.ma.maisonette_data_back.Repos.CampusDataByVariableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.*;

@Service
public class CampusDataByVariableService {

    private final CampusDataByVariableRepository repository;

    @Autowired
    public CampusDataByVariableService(CampusDataByVariableRepository repository) {
        this.repository = repository;
    }

    public List<Map<String, Object>> getOptimizedData(String house, String deviceId, List<String> variables, Instant startTime, Instant endTime) {
        List<CampusDataByVariable> data;

        // Récupération des données avec ou sans plage horaire
        if (startTime != null && endTime != null) {
            data = repository.findByKeyHouseAndKeyDeviceIdAndKeyTimeBetween(house, deviceId, startTime, endTime);
        } else {
            data = repository.findByKeyHouseAndKeyDeviceId(house, deviceId);
        }

        // Filtrer par variables si spécifié
        if (variables != null && !variables.isEmpty()) {
            data = data.stream()
                    .filter(item -> variables.contains(item.getKey().getVariables()))
                    .toList();
        }

        // Transformer les données en JSON optimisé
        List<Map<String, Object>> result = new ArrayList<>();
        for (CampusDataByVariable item : data) {
            Map<String, Object> record = new LinkedHashMap<>();
            record.put("time", item.getKey().getTime());
            record.put("house", item.getKey().getHouse());
            record.put("deviceId", item.getKey().getDeviceId());
            record.put("variable", item.getKey().getVariables());
            record.put("measure", item.getMeasures());
            result.add(record);
        }
        return result;
    }
    public Map<String, Map<String, Set<String>>> getGroupedHouseDeviceVariables() {
        List<Map<String, String>> rawData = repository.findDistinctHouseDeviceVariables();

        // Structure des données : {house -> {deviceId -> Set<variables>}}
        Map<String, Map<String, Set<String>>> groupedData = new LinkedHashMap<>();

        for (Map<String, String> item : rawData) {
            String house = item.get("house");
            String deviceId = item.get("device_id");
            String variable = item.get("variables");

            // Ajouter la maison si elle n'existe pas encore
            groupedData.putIfAbsent(house, new LinkedHashMap<>());

            // Ajouter le deviceId si non existant, avec une liste pour les variables
            groupedData.get(house).putIfAbsent(deviceId, new LinkedHashSet<>());

            // Ajouter la variable dans la liste des variables du deviceId
            groupedData.get(house).get(deviceId).add(variable);
        }

        return groupedData;
    }






}