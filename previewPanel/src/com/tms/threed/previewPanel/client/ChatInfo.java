package com.tms.threed.previewPanel.client;

import com.tms.threed.util.lang.shared.Path;

public class ChatInfo {

    private final Path vehicleIconUrl;

    private final Path clickActionUrl;

    public ChatInfo() {
        vehicleIconUrl = new Path("http://www.toyota.com/byt/pub/media?id=67938111");
        clickActionUrl = new Path("http://www.google.com");
    }

    public ChatInfo(Path vehicleIconUrl, Path clickActionUrl) {
        this.vehicleIconUrl = vehicleIconUrl;
        this.clickActionUrl = clickActionUrl;
    }

    public ChatInfo(String  vehicleIconUrl, String  clickActionUrl) {
        this.vehicleIconUrl = new Path(vehicleIconUrl);
        this.clickActionUrl = new Path(clickActionUrl);
    }

    public Path getVehicleIconUrl() {
        return vehicleIconUrl;
    }

    public Path getClickActionUrl() {
        return clickActionUrl;
    }
}
