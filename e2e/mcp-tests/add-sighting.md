# Add a sighting (MCP agent test)

Verifies that a new sighting can be logged via the form and shows up in the
sightings list.

Base URL: `http://localhost:5173` (override if testing the Docker stack on
`http://localhost:8081`).

## Steps

1. Navigate to `/sightings`.
2. Confirm the page loaded: an element with `data-testid="sightings-list"`
   should be visible.
3. Click the element with `data-testid="add-sighting-button"`.
4. Confirm the URL is now `/sightings/new`.
5. Fill in the form (`data-testid="sighting-form"`):
   - `data-testid="field-bird"` — select the option with value `7` (Mallard).
   - `data-testid="field-location"` — select the option with value `1`
     (Everglades National Park).
   - `data-testid="field-quantity"` — type `3`.
   - `data-testid="field-observer"` — type a unique name, e.g.
     `MCP Agent Tester <current timestamp>`. Remember this value for step 9.
   - `data-testid="field-notes"` — type
     `Logged by a Playwright MCP agent test.`
6. Click `data-testid="submit-sighting"`.
7. Confirm the URL redirected back to `/sightings`.
8. Confirm an element with `data-testid="toast-success"` is visible with the
   text `Sighting logged successfully.`
9. Type the unique observer name from step 5 into
   `data-testid="sightings-search-input"`.
10. Confirm exactly one element matching `.sighting-row` is shown, and that it
    contains the text `Mallard` and `Everglades National Park`.

## Pass criteria

All 10 steps complete as described. If any step's expected state doesn't
match (wrong URL, missing toast, wrong row count/content), report the spec as
failed and note which step diverged.
