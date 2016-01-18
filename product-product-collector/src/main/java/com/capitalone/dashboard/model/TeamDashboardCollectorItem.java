package com.capitalone.dashboard.model;

import org.bson.types.ObjectId;

public class TeamDashboardCollectorItem extends CollectorItem {
    private ObjectId dashboardId;

    public ObjectId getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(ObjectId dashboardId) {
        this.dashboardId = dashboardId;
    }

}