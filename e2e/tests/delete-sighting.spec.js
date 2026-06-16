import { test, expect } from '@playwright/test';

test('deletes a sighting', async ({ page }) => {
  const observerName = `Playwright Delete Target ${Date.now()}`;

  await page.goto('/sightings/new');
  await page.getByTestId('field-bird').selectOption('7'); // Mallard
  await page.getByTestId('field-location').selectOption('1'); // Everglades National Park
  await page.getByTestId('field-observer').fill(observerName);
  await page.getByTestId('submit-sighting').click();
  await expect(page).toHaveURL(/\/sightings$/);

  await page.getByTestId('sightings-search-input').fill(observerName);
  const row = page.locator('.sighting-row');
  await expect(row).toHaveCount(1);

  const rowTestId = await row.getAttribute('data-testid');
  const sightingId = rowTestId.replace('sighting-row-', '');

  await page.getByTestId(`delete-sighting-${sightingId}`).click();
  await expect(page.getByTestId('confirm-modal')).toBeVisible();
  await page.getByTestId('confirm-modal-confirm').click();

  await expect(page.getByTestId('toast-success').last()).toHaveText('Sighting deleted.');
  await expect(row).not.toBeVisible();
});
