package gep.ma.maisonette_data_back.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyClass
public class CampusDataByVariableKey implements Serializable {

    @PrimaryKeyColumn(name = "house", type = PrimaryKeyType.PARTITIONED)
    private String house;

    @PrimaryKeyColumn(name = "device_id", ordinal = 0, type = PrimaryKeyType.CLUSTERED)
    private String deviceId;

    @PrimaryKeyColumn(name = "time", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private Instant time;

    @PrimaryKeyColumn(name = "variables", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
    private String variables;
}
