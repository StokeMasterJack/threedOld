package com.tms.threed.previewPanel.client.headerPanel;

import com.tms.threed.previewPanel.client.ChatInfo;
import com.tms.threed.threedCore.shared.SeriesInfo;
import com.tms.threed.threedCore.shared.SeriesKey;

public interface ChatPanelDisplay {

    void setSeriesKey(SeriesKey seriesKey);

    void setChatInfo(ChatInfo chatInfo);


}