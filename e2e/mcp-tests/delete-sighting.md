# Delete a sighting (MCP agent test)

Verifies that a sighting can be deleted via the confirm modal.

Base URL: `http://localhost:5173` (override if testing the Docker stack on
`http://localhost:8081`).

## Steps

1. Navigate to `/sightings/new`.
2. Fill in the form (`data-testid="sighting-form"`):
   - `data-testid="field-bird"` — select the option with value `7` (Mallard).
   - `data-testid="field-location"` — select the option with value `1`
     (Everglades National Park).
   - `data-testid="field-observer"` — type a unique name, e.g.
     `MCP Agent Delete Target <current timestamp>`. Remember this value for
     step 4.
3. Click `data-testid="submit-sighting"` and confirm the URL redirects to
   `/sightings`.
4. Type the unique observer name from step 2 into
   `data-testid="sightings-search-input"`.
5. Confirm exactly one element matching `.sighting-row` is shown. Read its
   `data-testid` attribute — it will be `sighting-row-<id>` — and note the
   `<id>`.
6. Click the element with `data-testid="delete-sighting-<id>"` (using the id
   from step 5).
7. Confirm an element with `data-testid="confirm-modal"` is visible.
8. Click `data-testid="confirm-modal-confirm"`.
9. Confirm a `data-testid="toast-success"` element shows the text
   `Sighting deleted.`
10. Confirm the row from step 5 (`.sighting-row` matching the unique observer
    name / id) is no longer present on the page.

## Pass criteria

All 10 steps complete as described. If any step's expected state doesn't
match (wrong URL, modal doesn't appear, missing toast, row still present),
report the spec as failed and note which step diverged.
