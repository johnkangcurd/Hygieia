package com.capitalone.dashboard.repository;

import com.capitalone.dashboard.model.TeamDashboardCollectorItem;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface TeamDashboardRepository extends BaseCollectorItemRepository<TeamDashboardCollectorItem>{

    @Query(value="{ 'collectorId' : ?0}")
    List<TeamDashboardCollectorItem> findTeamDashboards(ObjectId collectorId);

}