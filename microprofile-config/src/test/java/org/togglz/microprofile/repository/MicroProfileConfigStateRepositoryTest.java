package org.togglz.microprofile.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.togglz.core.Feature;
import org.togglz.microprofile.TestConfigSourceProvider;

public class MicroProfileConfigStateRepositoryTest {
    public enum TestFeatures implements Feature {
        FEATURE_ONE
    }

    private MicroProfileConfigStateRepository repo;

    @Before
    public void setup() {
        repo = new MicroProfileConfigStateRepository();
    }

    @After
    public void tearDown() {
        TestConfigSourceProvider.TestConfigSource.INSTANCE.clearProperties();
    }

    @Test
    public void testIsEnabled() {
        TestConfigSourceProvider.TestConfigSource.INSTANCE.putProperty("FEATURE_ONE.enabled", String.valueOf(true));
        assertTrue(repo.getFeatureState(TestFeatures.FEATURE_ONE).isEnabled());
    }

    @Test
    public void testIsDisabledDefault() {
        assertFalse(repo.getFeatureState(TestFeatures.FEATURE_ONE).isEnabled());
    }

    @Test
    public void testIsEnabled_withStrategy() {
        TestConfigSourceProvider.TestConfigSource.INSTANCE.putProperty("FEATURE_ONE.enabled", String.valueOf(true));
        TestConfigSourceProvider.TestConfigSource.INSTANCE.putProperty("FEATURE_ONE.strategy", "gradual");
        TestConfigSourceProvider.TestConfigSource.INSTANCE.putProperty("FEATURE_ONE.params", "percentage=5");
        assertEquals("gradual", repo.getFeatureState(TestFeatures.FEATURE_ONE).getStrategyId());
        assertEquals("5", repo.getFeatureState(TestFeatures.FEATURE_ONE).getParameter("percentage"));
    }

    @Test
    public void testIsEnabled_withStrategy_multipleParams() {
        TestConfigSourceProvider.TestConfigSource.INSTANCE.putProperty("FEATURE_ONE.enabled", String.valueOf(true));
        TestConfigSourceProvider.TestConfigSource.INSTANCE.putProperty("FEATURE_ONE.strategy", "release-date");
        TestConfigSourceProvider.TestConfigSource.INSTANCE.putProperty("FEATURE_ONE.params", "date=2019-10-22&time=11:00:00");
        assertEquals("release-date", repo.getFeatureState(TestFeatures.FEATURE_ONE).getStrategyId());
        assertEquals("2019-10-22", repo.getFeatureState(TestFeatures.FEATURE_ONE).getParameter("date"));
        assertEquals("11:00:00", repo.getFeatureState(TestFeatures.FEATURE_ONE).getParameter("time"));
    }

}
