# Statistics History And Extrema Plan

**Scope:** condensed architecture reference for the statistics **history** and **extrema** feature. The feature is
fully implemented; detailed step-by-step notes and code samples were removed after implementation to save space.
Remaining and follow-on work lives in the backlog section below.

## Purpose

Two foundations were added to `de.mkalb.etpetssim.simulations.core.model` and the timed simulations:

- A **bounded history** of recent statistic samples (default 100 executed steps), suitable for later line-chart
  rendering.
- A **generic per-metric minimum/maximum tracker**, replacing the hand-maintained `min…`/`max…` fields that Conway,
  Forest, and Wator previously carried.

The live `statistics()` contract was kept unchanged; the generic layer wraps the existing mutable statistics objects
instead of forcing them to become immutable. Extrema are full-run values (not limited to the retained history window).

## Key Types (as implemented, in `simulations.core.model`)

- **`StatisticMetric<STA>`** — descriptor record: technical `key`, `labelKey` (i18n), `ToDoubleFunction` extractor, and
  `StatisticExtremaMode`. Each statistics class exposes a static `metrics()` list. Metrics use `double`; descriptors are
  explicit (no reflection).
- **`StatisticExtremaMode`** — enum `NONE` / `MIN` / `MAX` / `MIN_AND_MAX`.
- **`StatisticSample`** — immutable point-in-time record: `stepCount`, `StepTimingStatistics`, and an unmodifiable,
  insertion-ordered `Map<String, Double>` of values (defensively copied).
- **`StatisticHistory`** — bounded `ArrayDeque` ring buffer (`DEFAULT_CAPACITY = 100`), oldest evicted first, exposed
  as an immutable ordered list.
- **`StatisticExtrema`** — immutable public min/max snapshot; **`StatisticExtremaTracker`** — package-private mutable
  accumulator honoring each metric's mode.

## Manager Integration

`AbstractTimedSimulationManager` owns tracking: a metrics-aware constructor, `recordStatisticsSample()` after every
executed step (single and batch, via the per-step callback, with no duplicate final-batch sample), and a protected
`recordInitialStatisticsSample()` hook called at the end of each concrete manager constructor to record the step-0
baseline. History and extrema are exposed via `statisticsHistory()` / `statisticsExtrema()` (default-empty on
`SimulationManager`, so `LabSimulationManager` stays out of scope).

## Implementation Status

All originally planned phases and follow-up steps are **complete**: all 8 timed simulations (Conway, Forest, Wator,
Etpets, Snake, Sugar, Rebounding, Langton) declare `metrics()`; the non-finite guard logs and substitutes `Double.NaN`
(skipped by the extrema tracker); the hand-maintained min/max fields were removed from Conway/Forest/Wator and their
observation views now read from `statisticsExtrema()`; and `StatisticHistory` synchronization was removed. Tests live in
`StatisticHistoryTest`, `StatisticExtremaTrackerTest`, and `TimedStatisticsTrackingTest`.

**Step 1 (2026-08-08):** All four broken `labelKey` values were fixed: the Rebounding constant was corrected to
`rebounding.observation.cells.movingentity`; the three missing keys (`forest.observation.cells.empty`,
`snake.observation.cells.livingsnakehead`, `snake.observation.cells.wall`) were added to both locale bundles
(alphabetically sorted, `=` column-aligned). A guard test `StatisticMetricLabelKeyTest` was added that iterates all
8 simulations' `metrics()` lists and asserts each `labelKey` resolves in both the `en_US` and `de_DE` production
bundles, preventing future drift.

**Step 2 (2026-08-08):** Each technical metric key is now defined exactly once as a `public static final String KEY_*`
constant on the owning statistics class (e.g., `ConwayStatistics.KEY_ALIVE_CELLS`). All 8 statistics classes were
updated; `metrics()` factories reference the constant instead of repeating the string literal. The three observation
views that previously re-declared the key as a literal (`ConwayObservationView`, `ForestObservationView`,
`WatorObservationView`) were updated to reference the statistics-class constants, making a rename a compile-time error
going forward.

Open questions retained for future work: descriptor location vs. drift, whether timing should be a descriptor, whether
`double` suffices for all metrics, sample sequence numbers for edit-mode history, and configurable history capacity.

## Implementation Backlog (Consolidated 2026-08-08)

The former "Post-Implementation Notes", "Independent Review Findings", and "Future Enhancement Ideas" sections have
been merged into a single ordered backlog. Already-implemented items (summarized under Implementation Status above) and
purely informational observations were removed. The remaining work is expressed as sequential steps: earlier steps
unblock later ones, and the first four are correctness/quality fixes that should land before any new statistics feature
is built on top.

Each step is tagged with:

- **Type**: Bug, Refactor, Test, Feature, or Docs.
- **Priority**: High, Medium, or Low.
- **Effort**: S (hours), M (about a day), or L (multiple days).
- **Risk**: expected regression / blast radius.
- **Depends on**: prerequisite steps.

### Preserved Invariants (must not regress)

These were established during the initial implementation and must remain intact while working through the backlog:

- `AbstractTimedSimulationManager` records exactly one sample per executed step; the post-batch `updateStatistics()`
  call must not record a duplicate final sample (`testExecuteStepsRecordsEveryExecutedStepWithoutDuplicateFinalSample`).
- The step-0 sample is recorded via `recordInitialStatisticsSample()` at the end of each concrete manager constructor,
  after simulation-specific counters are initialized.
- `StatisticExtremaTracker` stays `synchronized` (`update()` on the batch thread, `snapshot()` on the FX thread); do not
  remove it.
- `StatisticHistory` stays `synchronized` (`add()` on the batch thread, `asList()` on the FX thread); do not remove it.
- Non-finite metric values are logged and stored as the `Double.NaN` sentinel and skipped by the extrema tracker; the
  history time series stays gap-free.



**Step 3 (2026-08-08):** Extrema golden-value regression coverage was restored for Conway, Forest, and Wator.
Three new tests (`testConwayGoldenExtremaExactValues`, `testForestGoldenExtremaExactValues`,
`testWatorGoldenExtremaExactValues`) were added to `TimedStatisticsTrackingTest`. Each test asserts exact `int`
extrema against values captured with seed `1L` and 20 steps using default constraints, and verifies that the batch
path (`executeSteps(20)`) and the single-step path (`executeSteps(1)` × 20) produce identical results. The companion
`ExtremaGoldenValueAnalyzer` utility was added to re-capture values if simulation logic changes.

Golden values (seed=1, 20 steps, default constraints):
- Conway: `maxAliveCells` = 6945
- Forest: `maxTreeCells` = 1037, `maxBurningCells` = 17
- Wator: `minFishCells` = 1900, `maxFishCells` = 6127, `minSharkCells` = 1000, `maxSharkCells` = 3002

### Step 3 — Restore Extrema Regression Coverage (Golden Values) ✓ DONE

> **Type:** Test · **Priority:** Medium · **Effort:** M · **Risk:** Low · **Depends on:** —

Removing the typed getters (`getMaxAliveCells()`, `getMinFishCells()`, …) forced the three original parity tests to be
rewritten as "finite / non-negative / present" assertions in `TimedStatisticsTrackingTest`. These no longer verify that
the generic values equal the previous behavior; a regression in an extractor or in sampling order would pass unnoticed.

**Actions:**

1. Using the deterministic seed already used in the tests (`1L`), run Conway/Forest/Wator for a fixed step count and
   capture the expected exact `int` extrema.
2. Assert exact equality against the generic `StatisticExtrema` values, covering both the single-step and batch paths.

**Step 4 (2026-08-08):** `StatisticHistory` threading contract aligned with `StatisticExtremaTracker`. `add()`,
`asList()`, and `clear()` are now `synchronized`, preventing a `ConcurrentModificationException` from `List.copyOf()`
when a future FX-thread consumer reads `statisticsHistory()` while a background batch thread calls `add()`. The
Javadoc on `StatisticHistory` documents the thread-safety guarantee.

### Step 4 — Align `StatisticHistory` Threading Contract ✓ DONE

> **Type:** Refactor/Bug · **Priority:** Medium (High before Step 7) · **Effort:** S · **Risk:** Low · **Depends on:** —

`StatisticHistory` has no synchronization (single-producer assumption), while `StatisticExtremaTracker` is
`synchronized` because `snapshot()` is read from the FX thread. This is safe only while `statisticsHistory()` is not
read from the FX thread. `statisticsHistory()` is a **public** manager API; the history-charts feature (Step 7+) will
read it from the FX thread while a background batch calls `add()`, which can throw `ConcurrentModificationException`
from `List.copyOf(...)` or return an inconsistent snapshot.

**Actions (choose one, before any FX-thread history consumer exists):**

1. Re-introduce synchronization on `StatisticHistory.add()` / `asList()` to match the tracker; or
2. Explicitly document that `statisticsHistory()` must only be consumed after copying on the producing thread, and
   enforce the copy at the forwarding boundary in Step 7.

### Step 5 — Add Meaningful Extrema Modes To The Cellular Automata

> **Type:** Feature · **Priority:** Medium · **Effort:** M · **Risk:** Low-Medium (changes displayed extrema) · *
*Depends on:** Steps 2, 3

The agent-based simulations (Wator, Etpets, Snake, Sugar, Rebounding) already track `MIN_AND_MAX` for their live
population counters. The cellular automata are the gap. Current `extremaMode` per simulation for reference:

| Simulation | Metrics and current mode                                                                                      |
|------------|---------------------------------------------------------------------------------------------------------------|
| Conway     | `aliveCells` MAX, `deadCells` NONE, `changedCells` NONE                                                       |
| Forest     | `emptyCells` NONE, `treeCells` MAX, `burningCells` MAX                                                        |
| Langton    | `antCells` NONE, `visitedCells` MAX                                                                           |
| Wator      | `fishCells` MIN_AND_MAX, `sharkCells` MIN_AND_MAX                                                             |
| Etpets     | `activePetCells` MIN_AND_MAX, `eggCells` MIN_AND_MAX, `cumulativePetDeathCount` NONE                          |
| Snake      | `snakeHeadCells`/`livingSnakeHeadCells`/`wallCells`/`foodCells` MIN_AND_MAX, `cumulativeSnakeDeathCount` NONE |
| Sugar      | `resourceCells` MIN_AND_MAX, `agentCells` MIN_AND_MAX                                                         |
| Rebounding | `wallCells` MIN_AND_MAX, `movingEntityCells` MIN_AND_MAX                                                      |

**Actions:**

1. **Conway:** add `MIN` for `aliveCells` (population low point; `MIN == 0` signals extinction). Consider `MAX` and
   `MIN` for `changedCells` (peak churn; `MIN == 0` marks a frozen still-life / period-1 board). Leave `deadCells`
   as `NONE` (exact complement of `aliveCells`).
2. **Forest:** add `MIN` for `treeCells` (worst post-fire cover) and `MAX` for `emptyCells` (peak scorched area).
   `burningCells` keeps `MAX` only (its minimum is trivially `0`).
3. **Langton:** keep `antCells` `NONE` (constant). `visitedCells` `MAX` is redundant (monotonic). Optionally add a
   derived `newlyVisitedCellsPerStep` with `MAX` (highway onset) and `MIN` (long revisiting stretches).
4. **Snake / Rebounding:** confirm `wallCells` actually varies via the wall mutation methods in the default modes; if
   effectively static there, downgrade from `MIN_AND_MAX` to `NONE`.
5. Update/extend tests and add any newly displayed i18n rows (both bundles).

### Step 6 — Track The Step Index Of Each Extremum

> **Type:** Feature · **Priority:** Medium · **Effort:** M · **Risk:** Medium (API change to `StatisticExtrema` /
> tracker) · **Depends on:** Step 3

Store the `stepCount` at which each minimum/maximum occurred alongside the value in `StatisticExtremaTracker` and expose
it through `StatisticExtrema`. This turns "peak shark population 128" into "peak shark population 128 @ step 342", which
is far more useful for the observation panel and future chart tooltips. It is cheap to capture next to the existing
`merge` calls, but changes the public `StatisticExtrema` shape, so land it before generic rows (Step 11).

### Step 7 — Forward History To The ViewModel Layer

> **Type:** Feature · **Priority:** Medium · **Effort:** S-M · **Risk:** Low · **Depends on:** Step 4

The bounded `StatisticHistory` is recorded every step but nothing reads it yet. Mirror the extrema forwarding already
in `DefaultMainViewModel`: push `simulationManager.statisticsHistory()` into `DefaultObservationViewModel` on each
update, exposed as a snapshot accessor (`getStatisticsHistory()`). Perform the defensive copy at this forwarding
boundary per the Step 4 contract.

### Step 8 — Promote Extrema And History To Observable Properties

> **Type:** Refactor · **Priority:** Medium · **Effort:** M · **Risk:** Low · **Depends on:** Step 7

`DefaultObservationViewModel.statisticsExtrema` is a plain field, not a JavaFX property. Promote both extrema and
history to `ReadOnlyObjectProperty<StatisticExtrema>` / `ReadOnlyObjectProperty<List<StatisticSample>>` so views can
bind directly instead of relying on a manual refresh cycle. Decide the shutdown policy here as well: optionally reset
extrema/history to empty in `DefaultMainViewModel.shutdownSimulation()` (via
`observationStateViewModel.setStatisticsExtrema(StatisticExtrema.empty())`) so labels do not linger with last-run
values; the current persist-until-next-start behavior is an intentional trade-off that matches the statistics labels.

### Step 9 — Descriptor-Driven Line Chart Region

> **Type:** Feature · **Priority:** Medium · **Effort:** L · **Risk:** Medium · **Depends on:** Steps 7, 8

Add an optional chart to the observation area (e.g., an `AbstractObservationView` helper or a new
`StatisticHistoryChartView`) using a JavaFX `LineChart<Number, Number>` with `stepCount` on the X axis and metric value
on the Y axis. Build one `XYChart.Series` per selected metric, driving the series name from the metric's `labelKey`
(via `AppLocalization`) and the points from `StatisticSample.values().get(key)`. Skip `NaN` points. This is exactly
what the descriptor design was built for; timing series can reuse `StatisticSample.stepTimingStatistics()` with no new
plumbing.

### Step 10 — Metric Selection UI From Descriptors

> **Type:** Feature · **Priority:** Low · **Effort:** M · **Risk:** Low · **Depends on:** Step 9

Because each simulation already declares an ordered `metrics()` list, a generic checkbox/toggle list can let the user
pick which series to plot, with no per-simulation UI code.

### Step 11 — Generic Descriptor-Driven Observation Rows

> **Type:** Refactor · **Priority:** Low · **Effort:** M · **Risk:** Medium (UI parity) · **Depends on:** Steps 2, 6

Replace the per-simulation min/max label wiring in the observation views with a generic row renderer that iterates the
`metrics()` list and renders current value + extrema (plus the extremum step index from Step 6). This removes the
duplicated key lookups and keeps views in sync with descriptors automatically.

### Optional / Independent Enhancements

Not on the critical path; pick up as needed.

- **Richer `StatisticMetric` metadata** — Feature · Low · M. Add an optional value formatter, unit, and display policy
  (integer / percentage / duration) so chart axes and observation rows format consistently, removing per-view casts
  like `(int) value`.
- **Configurable history capacity + decimation** — Feature · Low · M. Promote `StatisticHistory.DEFAULT_CAPACITY`
  (100) to `SimulationConfig`, an app preference, or a chart setting (one of the retained open questions); add
  down-sampling for longer windows.
- **Derived / computed metrics** — Feature · Low · M. Ratios and densities (Wator `sharkCells / fishCells`, Forest
  tree-coverage `%`, Sugar `agentCells / resourceCells`); relies on the existing non-finite (divide-by-zero) guard.
- **Rolling averages / smoothing** — Feature · Low · S-M (depends on Step 9). Moving-average overlay per metric.
- **CSV export + manual `*Analyzer`** — Feature · Low · S-M (depends on Step 7). Export `stepCount` + one column per
  metric key for offline analysis.
- **Event annotations** — Feature · Low · L. Mark notable steps (extinction, first reproduction, snake death,
  ignition) on the history/chart; ties into the deferred edit-mode history extension.
- **Stagnation / termination detection** — Feature · Low · M (depends on Step 7). Detect a stabilized run from the
  history tail (e.g., `changedCells == 0` for K consecutive steps) and optionally stop or badge it.
- **Sparklines / trend tooltips** — Feature · Low · M. Inline mini trend line or a "last N steps" tooltip per
  observation row, fed from the history tail.
