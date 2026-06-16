# Sightings list sort order (MCP agent test)

Verifies that the sightings list is sorted by date, most recent first (the
default server-side sort — there's no sort control in the UI).

Base URL: `http://localhost:5173` (override if testing the Docker stack on
`http://localhost:8081`).

## Steps

1. Navigate to `/sightings`.
2. Confirm an element with `data-testid="sightings-list"` is visible.
3. Take a snapshot of the page and read the date shown in each row (the bold
   text inside `.sighting-row__date`, e.g. "Jun 15, 2026"). There should be
   more than one row.
4. Convert each date to a comparable form (e.g. a `Date` object or
   `YYYY-MM-DD`) in the order they appear top to bottom.
5. Confirm the dates are in non-increasing order — each row's date is the
   same as or earlier than the row above it.

## Pass criteria

There are at least 2 rows, and every row's date is `<=` the previous row's
date (descending order, most recent first). If any row breaks that ordering,
report the spec as failed and note which two rows are out of order.
