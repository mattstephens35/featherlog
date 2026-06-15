import { test, expect } from '@playwright/test';

test('logs a new sighting and shows it in the list', async ({ page }) => {
  const observerName = `Playwright Tester ${Date.now()}`;

  await page.goto('/sightings');

  await page.getByTestId('add-sighting-button').click();
  await expect(page).toHaveURL(/\/sightings\/new$/);

  await page.getByTestId('field-bird').selectOption('7'); // Mallard
  await page.getByTestId('field-location').selectOption('1'); // Everglades National Park
  await page.getByTestId('field-quantity').fill('3');
  await page.getByTestId('field-observer').fill(observerName);
  await page.getByTestId('field-notes').fill('Logged by an automated Playwright test.');

  await page.getByTestId('submit-sighting').click();

  await expect(page).toHaveURL(/\/sightings$/);
  await expect(page.getByTestId('toast-success')).toHaveText('Sighting logged successfully.');

  await page.getByTestId('sightings-search-input').fill(observerName);
  const rows = page.locator('.sighting-row');
  await expect(rows).toHaveCount(1);
  await expect(rows).toContainText('Mallard');
  await expect(rows).toContainText('Everglades National Park');
});
