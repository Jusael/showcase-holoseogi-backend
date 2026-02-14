package com.standalonejhgl.holoseogiapi.projection;

public interface NotificationSummaryProjection {
    Long getSuccessCnt();
    Long getReadyCnt();
    Long getFailedCnt();
}
