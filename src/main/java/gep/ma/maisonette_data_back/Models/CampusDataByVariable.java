package gep.ma.maisonette_data_back.Models;

import gep.ma.maisonette_data_back.Models.CampusDataByVariableKey;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampusDataByVariable {

    @PrimaryKey
    private CampusDataByVariableKey key;

    @Column("measures")
    private Float measures;
}