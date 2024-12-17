package gep.ma.maisonette_data_back.Repos;

import gep.ma.maisonette_data_back.Models.CampusDataByVariable;
import gep.ma.maisonette_data_back.Models.CampusDataByVariableKey;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Repository
public interface CampusDataByVariableRepository extends CassandraRepository<CampusDataByVariable, String> {

    @Query("SELECT * FROM campus_data_by_variable WHERE house=?0 AND device_id=?1 AND time>?2 AND time<?3 ALLOW FILTERING")
    List<CampusDataByVariable> findByKeyHouseAndKeyDeviceIdAndKeyTimeBetween(
            String house, String deviceId, Instant startTime, Instant endTime);

    @Query("SELECT * FROM campus_data_by_variable WHERE house=?0 AND device_id=?1 ALLOW FILTERING")
    List<CampusDataByVariable> findByKeyHouseAndKeyDeviceId(String house, String deviceId);

    @Query("SELECT DISTINCT house, device_id, variables FROM campus_data_by_variable")
    List<Map<String, String>> findDistinctHouseDeviceVariables();


}
