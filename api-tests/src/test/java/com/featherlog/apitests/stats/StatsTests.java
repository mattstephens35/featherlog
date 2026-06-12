package com.featherlog.apitests.stats;

import com.featherlog.apitests.clients.StatsClient;
import com.featherlog.apitests.model.ActivityResponse;
import com.featherlog.apitests.model.ConservationBreakdownResponse;
import com.featherlog.apitests.model.ConservationStatus;
import com.featherlog.apitests.model.LifersResponse;
import com.featherlog.apitests.model.StatsSummaryResponse;
import com.featherlog.apitests.model.TimelineResponse;
import com.featherlog.apitests.support.BaseApiTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class StatsTests extends BaseApiTest {

    @Test
    public void summaryHasExpectedShape() {
        StatsSummaryResponse summary = StatsClient.summary()
                .then().statusCode(200)
                .extract().as(StatsSummaryResponse.class);

        Assert.assertTrue(summary.totalSightings() >= 32);
        Assert.assertTrue(summary.speciesRecorded() >= 1);
        Assert.assertTrue(summary.locationsVisited() >= 1);
        Assert.assertTrue(summary.totalSpeciesCatalog() >= 25);
        Assert.assertTrue(summary.totalLocationsCatalog() >= 18);
        Assert.assertTrue(summary.sightingsThisMonth() >= 0);
        Assert.assertTrue(summary.recentSightings().size() <= 5);
        Assert.assertTrue(summary.topBirds().size() <= 5);
        Assert.assertTrue(summary.topLocations().size() <= 5);
    }

    @Test
    public void timelineHasExpectedShape() {
        TimelineResponse timeline = StatsClient.timeline()
                .then().statusCode(200)
                .extract().as(TimelineResponse.class);

        Assert.assertFalse(timeline.months().isEmpty());

        for (TimelineResponse.MonthlyCount month : timeline.months()) {
            Assert.assertTrue(month.yearMonth().matches("\\d{4}-\\d{2}"));
            Assert.assertTrue(month.sightingCount() > 0);
        }

        for (int i = 0; i < timeline.months().size() - 1; i++) {
            String current = timeline.months().get(i).yearMonth();
            String next = timeline.months().get(i + 1).yearMonth();
            Assert.assertTrue(current.compareTo(next) < 0);
        }
    }

    @Test
    public void conservationBreakdownCoversEveryStatusOnce() {
        ConservationBreakdownResponse breakdown = StatsClient.conservation()
                .then().statusCode(200)
                .extract().as(ConservationBreakdownResponse.class);

        Assert.assertEquals(breakdown.statuses().size(), ConservationStatus.values().length);

        Set<ConservationStatus> seen = new HashSet<>();
        for (ConservationBreakdownResponse.StatusCount statusCount : breakdown.statuses()) {
            Assert.assertTrue(seen.add(statusCount.status()), "Duplicate status: " + statusCount.status());
            Assert.assertTrue(statusCount.speciesCount() >= 0);
            Assert.assertTrue(statusCount.sightingCount() >= 0);
        }

        Set<ConservationStatus> expected = Set.of(ConservationStatus.values());
        Assert.assertEquals(seen, expected);
    }

    @Test
    public void lifersAreOrderedByFirstSeenDate() {
        LifersResponse lifers = StatsClient.lifers()
                .then().statusCode(200)
                .extract().as(LifersResponse.class);

        Assert.assertFalse(lifers.lifers().isEmpty());

        for (LifersResponse.Lifer lifer : lifers.lifers()) {
            Assert.assertNotNull(lifer.bird());
            Assert.assertNotNull(lifer.firstSeenDate());
            Assert.assertNotNull(lifer.firstSeenLocation());
        }

        for (int i = 0; i < lifers.lifers().size() - 1; i++) {
            LocalDate current = lifers.lifers().get(i).firstSeenDate();
            LocalDate next = lifers.lifers().get(i + 1).firstSeenDate();
            Assert.assertFalse(current.isAfter(next));
        }

        Set<Long> birdIds = lifers.lifers().stream()
                .map(l -> l.bird().id())
                .collect(Collectors.toSet());
        Assert.assertEquals(birdIds.size(), lifers.lifers().size());
    }

    @Test
    public void activityHasExpectedShape() {
        ActivityResponse activity = StatsClient.activity()
                .then().statusCode(200)
                .extract().as(ActivityResponse.class);

        Assert.assertFalse(activity.days().isEmpty());

        for (ActivityResponse.DayCount day : activity.days()) {
            Assert.assertTrue(day.sightingCount() > 0);
        }

        for (int i = 0; i < activity.days().size() - 1; i++) {
            LocalDate current = activity.days().get(i).date();
            LocalDate next = activity.days().get(i + 1).date();
            Assert.assertTrue(current.isBefore(next));
        }

        Assert.assertTrue(activity.longestStreak() >= 1);
        Assert.assertTrue(activity.longestStreak() <= activity.days().size());
        Assert.assertNotNull(activity.longestStreakStart());
        Assert.assertNotNull(activity.longestStreakEnd());
        Assert.assertFalse(activity.longestStreakStart().isAfter(activity.longestStreakEnd()));
    }
}
