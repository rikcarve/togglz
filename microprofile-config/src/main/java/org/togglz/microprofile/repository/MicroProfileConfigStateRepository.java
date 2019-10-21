package org.togglz.microprofile.repository;

import java.util.Arrays;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.togglz.core.Feature;
import org.togglz.core.repository.FeatureState;
import org.togglz.core.repository.StateRepository;

/**
 * Using MicroProfile Config api as a state repository. The following scheme is
 * used to configure a feature:
 * 
 * <pre>
 * FEATURE_ONE.enabled=true
 * FEATURE_ONE.strategy=gradual
 * FEATURE_ONE.params=percentage=25&other=12
 * FEATURE_TWO : false
 * </pre>
 * 
 * @author rik
 *
 */
public class MicroProfileConfigStateRepository implements StateRepository {

    @Override
    public FeatureState getFeatureState(Feature feature) {
        Config config = ConfigProvider.getConfig();
        String prefix = feature.name() + ".";
        FeatureState featureState = new FeatureState(feature);
        featureState.setEnabled(config.getOptionalValue(prefix + "enabled", Boolean.class).orElse(false));
        featureState.setStrategyId(config.getOptionalValue(prefix + "strategy", String.class).orElse(""));
        String params = config.getOptionalValue(prefix + "params", String.class).orElse("");
        Arrays.stream(params.split("&"))
                .map((p) -> p.split("="))
                .filter((m) -> m.length > 1)
                .forEach((m) -> featureState.setParameter(m[0], m[1]));
        return featureState;
    }

    @Override
    public void setFeatureState(FeatureState featureState) {
        // do nothing
    }

}
