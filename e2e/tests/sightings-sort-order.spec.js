import { test, expect } from '@playwright/test';

test('sightings list is sorted by date, most recent first', async ({ page }) => {
  await page.goto('/sightings');

  await expect(page.getByTestId('sightings-list')).toBeVisible();

  const dateTexts = await page.locator('.sighting-row__date strong').allTextContents();
  expect(dateTexts.length).toBeGreaterThan(1);

  const dates = dateTexts.map((text) => new Date(text).getTime());
  for (let i = 1; i < dates.length; i++) {
    expect(dates[i]).toBeLessThanOrEqual(dates[i - 1]);
  }
});
