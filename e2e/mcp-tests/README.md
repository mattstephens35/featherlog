# Playwright MCP agent tests

These are **not** `@playwright/test` spec files — they're plain-language
checklists meant to be carried out by an AI agent (e.g. Claude Code) that has
the [Playwright MCP server](https://github.com/microsoft/playwright-mcp)
connected, driving a real browser through its tools
(`browser_navigate`, `browser_click`, `browser_type`, `browser_select_option`,
`browser_snapshot`, etc.).

They cover the same three flows as the automated suite in `e2e/tests/`, as a
demonstration of agent-driven UI testing against FeatherLog:

- [`add-sighting.md`](./add-sighting.md) — log a new sighting via the form
- [`sightings-sort-order.md`](./sightings-sort-order.md) — verify the
  sightings list's default sort order
- [`delete-sighting.md`](./delete-sighting.md) — delete a sighting via the
  confirm modal

## Prerequisites

- The FeatherLog stack must already be running and reachable. Default base
  URL is `http://localhost:5173` (Vite dev server); use
  `http://localhost:8081` if you're running the Docker Compose stack instead.
- The Playwright MCP server must be available to the agent. This repo's
  `.mcp.json` configures it via `npx @playwright/mcp@latest` — Claude Code
  will prompt to approve it the first time.

## Running a spec

Ask the agent to follow the steps in one of the `.md` files against the
running app, e.g.:

> Run `e2e/mcp-tests/add-sighting.md` against http://localhost:5173 using the
> Playwright MCP browser tools, and report whether each step passed.

The agent should perform each numbered step in order, using browser snapshots
to confirm the expected state/text before moving on, and report a pass/fail
for the spec as a whole (plus which step failed, if any).

## CI

`.github/workflows/playwright-mcp-tests.yml` runs all three specs on every
push/PR, as a separate parallel job alongside "API Tests" and "Playwright
Tests". It starts the Docker Compose stack, then runs Claude Code
(`anthropics/claude-code-base-action`) with the Playwright MCP server
attached against `http://localhost:8081`. Claude works through each spec and
writes `e2e/mcp-tests/results.json` (`{ "<spec>": { "passed": bool, "details":
"..." } }`); the workflow fails the job if any spec didn't pass, and uploads
`results.json` as an artifact either way.

**Requires an `ANTHROPIC_API_KEY` repo secret** (Settings → Secrets and
variables → Actions → "New repository secret"). Until that secret is added,
this workflow will fail at the "Run Playwright MCP agent tests" step.

This job is intentionally **not** part of `main`'s required status checks —
being LLM-driven, it's inherently less deterministic than the scripted
`e2e/tests/` suite, so it shouldn't block merges on its own.
