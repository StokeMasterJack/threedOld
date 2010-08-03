package com.tms.threed.featureModel.server;

import com.tms.threed.featureModel.data.Avalon2010;
import com.tms.threed.featureModel.data.Avalon2011;
import com.tms.threed.featureModel.data.Camry2011;
import com.tms.threed.featureModel.shared.FeatureModel;
import com.tms.threed.threedCore.shared.SeriesKey;
import com.tms.threed.util.lang.shared.Path;

import java.util.HashMap;
import java.util.Map;

public class FeatureModelFactoryJava extends FeatureModelFactory {

    private Map<SeriesKey, SFeatureModel> map = new HashMap<SeriesKey, SFeatureModel>();

    public FeatureModelFactoryJava(Path pngRoot) {
        super(pngRoot);

        put(SeriesKey.CAMRY_2011, new Camry2011());
        put(SeriesKey.AVALON_2011, new Avalon2011());
        put(SeriesKey.AVALON_2010, new Avalon2010());
    }

    private void put(SeriesKey seriesKey, FeatureModel fm) {
        map.put(seriesKey, new SFeatureModel(fm));
    }

    public boolean canCreate(SeriesKey seriesKey) {
        return map.containsKey(seriesKey);
    }

    public FeatureModel createFeatureModel(SeriesKey seriesKey) {
        return map.get(seriesKey).getFeatureModel();
    }


}
