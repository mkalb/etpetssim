# Statistics History And Extrema Plan

**Scope:** condensed architecture reference for the statistics **history** and **extrema** feature. The feature is
fully implemented; detailed step-by-step notes and code samples were removed after implementation to save space.
Remaining and follow-on work lives in the backlog section below.

## Purpose

Two foundations were added to `de.mkalb.etpetssim.simulations.core.model` and the timed simulations:

- A **bounded history** of recent statistic samples (default 1000 executed steps), suitable for later line-chart
  rendering.
- A **generic per-metric minimum/maximum tracker**, replacing the hand-maintained `min…`/`max…` fields that Conway,
  Forest, and Wator previously carried.

The live `statistics()` contract was kept unchanged; the generic layer wraps the existing mutable statistics objects
instead of forcing them to become immutable. Extrema are full-run values (not limited to the retained history window).

## Key Types (as implemented, in `simulations.core.model`)

- **`StatisticMetric<STA>`** — descriptor record: technical `key`, `labelKey` (i18n), `ToDoubleFunction` extractor,
  `StatisticExtremaMode`, `StatisticChartGroup`, and `chartWindowSize`. Each statistics class exposes a static
  `metrics()` list. Metrics use `double`; descriptors are explicit (no reflection).
- **`StatisticExtremaMode`** — enum `NONE` / `MIN` / `MAX` / `MIN_AND_MAX`.
- **`StatisticSample`** — immutable point-in-time record: `stepCount`, `StepTimingStatistics`, and an unmodifiable,
  insertion-ordered `Map<String, Double>` of values (defensively copied).
- **`StatisticHistory`** — bounded `ArrayDeque` ring buffer (`DEFAULT_CAPACITY = 1000`), oldest evicted first, exposed
  as an immutable ordered list.
- **`StatisticExtremum`** — immutable record: `double value` and `long stepCount` (step at which the extremum was first
  recorded).
- **`StatisticExtrema`** — immutable public min/max snapshot (`Map<String, StatisticExtremum>`); *
  *`StatisticExtremaTracker`** — package-private mutable
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
observation views now read from `statisticsExtrema()`; and `StatisticHistory` access is synchronized for
background/FX-thread use. Tests live in
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

**Step 5 (2026-08-08):** Extrema modes updated for all cellular automata and static-wall simulations.
Only tracking was added; display will follow in Step 11.

- **Conway** `aliveCells` MAX → MIN_AND_MAX; `changedCells` NONE → MIN_AND_MAX (MIN==0 marks still-life / frozen board).
- **Forest** `emptyCells` NONE → MAX; `treeCells` MAX → MIN_AND_MAX.
- **Langton** `visitedCells` MAX → NONE (monotonically increasing; MAX always equals current value).
- **Snake / Rebounding** `wallCells` MIN_AND_MAX → NONE (walls only change via user edit actions, not during step
  execution).

Golden values updated in `TimedStatisticsTrackingTest` and `ExtremaGoldenValueAnalyzer` to cover the new extrema.

### Step 5 — Add Meaningful Extrema Modes To The Cellular Automata ✓ DONE

> **Type:** Feature · **Priority:** Medium · **Effort:** M · **Risk:** Low-Medium (changes displayed extrema) · *
*Depends on:** Steps 2, 3

The agent-based simulations (Wator, Etpets, Snake, Sugar, Rebounding) already track `MIN_AND_MAX` for their live
population counters. The cellular automata are the gap. The `extremaMode` values below reflect the **current code**
(after the Step 5 changes and subsequent manual adjustments); the Actions list records the original Step 5 intent,
parts of which were later revised:

| Simulation | Metrics and current mode                                                                                                 |
|------------|--------------------------------------------------------------------------------------------------------------------------|
| Conway     | `aliveCells` MIN_AND_MAX, `deadCells` MIN_AND_MAX, `changedCells` MAX                                                    |
| Forest     | `emptyCells` MAX, `treeCells` MIN_AND_MAX, `burningCells` MAX                                                            |
| Langton    | `antCells` NONE, `visitedCells` NONE                                                                                     |
| Wator      | `fishCells` MIN_AND_MAX, `sharkCells` MIN_AND_MAX                                                                        |
| Etpets     | `activePetCells` MIN_AND_MAX, `eggCells` MAX, `cumulativePetDeathCount` NONE                                             |
| Snake      | `snakeHeadCells` NONE, `livingSnakeHeadCells` NONE, `foodCells` NONE, `wallCells` NONE, `cumulativeSnakeDeathCount` NONE |
| Sugar      | `resourceCells` NONE, `agentCells` NONE                                                                                  |
| Rebounding | `wallCells` MAX, `movingEntityCells` MAX                                                                                 |

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

**Step 6 (2026-08-08):** Step index of each extremum is now tracked alongside the value.

New `public record StatisticExtremum(double value, long stepCount)` was added to `simulations.core.model`.
`StatisticExtrema` maps changed from `Map<String, Double>` to `Map<String, StatisticExtremum>`. The tracker's
`update()` now accepts `long stepCount` and stores `StatisticExtremum` objects using a merge strategy instead of
`Math::min` / `Math::max`. `AbstractTimedSimulationManager` passes `sample.stepCount()` to `update()`. The three
existing observation views (Conway, Forest, Wator) were updated to call `.value()` for compilation; the step index
will be displayed in Step 11. All tests updated; `StatisticExtremaTrackerTest` gained step-count assertions.

### Step 6 — Track The Step Index Of Each Extremum ✓ DONE

> **Type:** Feature · **Priority:** Medium · **Effort:** M · **Risk:** Medium (API change to `StatisticExtrema` /
> tracker) · **Depends on:** Step 3

Store the `stepCount` at which each minimum/maximum occurred alongside the value in `StatisticExtremaTracker` and expose
it through `StatisticExtrema`. This turns "peak shark population 128" into "peak shark population 128 @ step 342", which
is far more useful for the observation panel and future chart tooltips. It is cheap to capture next to the existing
`merge` calls, but changes the public `StatisticExtrema` shape, so land it before generic rows (Step 11).

**Step 7 (2026-08-08):** `statisticsHistory` forwarding mirrored the existing extrema forwarding in
`DefaultMainViewModel`.
`DefaultObservationViewModel` gained a `List<StatisticSample> statisticsHistory` field (initialized to `List.of()`),
a `getStatisticsHistory()` accessor, and a `setStatisticsHistory()` mutator. `updateObservationStatistics()` in
`DefaultMainViewModel` now captures `manager.statisticsHistory()` before any `Platform.runLater()` lambda (satisfying
the Step 4 defensive-copy contract — `StatisticHistory.asList()` already returns `List.copyOf(...)`) and forwards it to
`observationStateViewModel.setStatisticsHistory()` on both the FX-thread fast path and the `runLater` path.

### Step 7 — Forward History To The ViewModel Layer ✓ DONE

> **Type:** Feature · **Priority:** Medium · **Effort:** S-M · **Risk:** Low · **Depends on:** Step 4

The bounded `StatisticHistory` is recorded every step but nothing reads it yet. Mirror the extrema forwarding already
in `DefaultMainViewModel`: push `simulationManager.statisticsHistory()` into `DefaultObservationViewModel` on each
update, exposed as a snapshot accessor (`getStatisticsHistory()`). Perform the defensive copy at this forwarding
boundary per the Step 4 contract.

**Step 8 (2026-08-08):** `statisticsExtrema` and `statisticsHistory` in `DefaultObservationViewModel` were promoted from
plain fields to `ReadOnlyObjectWrapper`-backed observable properties. Two new property accessors,
`statisticsExtremaProperty()` and `statisticsHistoryProperty()`, were added; the existing getters now delegate to
`wrapper.get()` and the setters delegate to `wrapper.set()`, keeping all existing call sites (Conway/Forest/Wator
observation views) compile-compatible. `DefaultMainViewModel.shutdownSimulation()` resets both properties to
`StatisticExtrema.empty()` and `List.of()` after setting `simulationManager = null`, preventing stale chart data from
lingering when a new simulation has not yet started. The shutdown decision was resolved: only extrema and history are
reset (statistics label is kept for consistency with prior behavior). A new test class
`DefaultObservationViewModelTest` (5 tests) covers initial values, setter→property and setter→getter round-trips, and
reset-to-empty behavior for both properties.

### Step 8 — Promote Extrema And History To Observable Properties ✓ DONE

> **Type:** Refactor · **Priority:** Medium · **Effort:** M · **Risk:** Low · **Depends on:** Step 7

`DefaultObservationViewModel.statisticsExtrema` and `statisticsHistory` are plain fields updated via setter; views
pull them in `updateObservationLabels()` which fires indirectly on the `statistics` property change. This
pull-on-statistics
pattern works while statistics and extrema/history are always updated together, but breaks for:

- chart series that must react to history independently (Step 9),
- edit-mode user actions that may change extrema without advancing statistics.

**API shape (mirrors the existing `statistics` field):**

```java
// In DefaultObservationViewModel:
private final ReadOnlyObjectWrapper<StatisticExtrema> statisticsExtremaWrapper = new ReadOnlyObjectWrapper<>(StatisticExtrema.empty());
private final ReadOnlyObjectWrapper<List<StatisticSample>> statisticsHistoryWrapper = new ReadOnlyObjectWrapper<>(List.of());

public ReadOnlyObjectProperty<StatisticExtrema> statisticsExtremaProperty() {
    return statisticsExtremaWrapper.getReadOnlyProperty();
}

public ReadOnlyObjectProperty<List<StatisticSample>> statisticsHistoryProperty() {
    return statisticsHistoryWrapper.getReadOnlyProperty();
}

// Setters delegate to wrappers (keep existing method names for call-site compatibility):
public void setStatisticsExtrema(StatisticExtrema extrema) {
    statisticsExtremaWrapper.set(extrema);
}

public void setStatisticsHistory(List<StatisticSample> history) {
    statisticsHistoryWrapper.set(history);
}
// Getters become wrappers().get() under the hood; existing call sites (ConwayObservationView etc.) keep compiling.
```

**Shutdown policy (decision required before Step 9):** Reset extrema to `StatisticExtrema.empty()` and history to
`List.of()` in `DefaultMainViewModel.shutdownSimulation()` immediately after `simulationManager = null`. This prevents
stale values from lingering in the chart (Step 9) when a new simulation has not yet been started. The statistics label
currently persists until the next start; consistency is achieved by resetting those too, or by accepting the asymmetry
and documenting it. **Recommended: reset all three** (statistics, extrema, history) to empty/null on shutdown for a
clean slate — aligned with the SimulationState.SHUTTING_DOWN transition that already clears other UI state.

**Impact on existing views:** Conway/Forest/Wator call `viewModel.getStatisticsExtrema()` in
`updateObservationLabels()`.
No change required there — the getter still works. Step 11 will replace those rows with the generic renderer anyway.

**Tests to add/update:**

- After `setStatisticsExtrema()` / `setStatisticsHistory()`, verify `statisticsExtremaProperty().get()` /
  `statisticsHistoryProperty().get()` return the new value.
- After `shutdownSimulation()`, verify both properties hold `StatisticExtrema.empty()` / `List.of()`.

### Step 9 — Descriptor-Driven Per-Group Line Charts ✓ DONE

> **Type:** Feature · **Priority:** Medium · **Effort:** L · **Risk:** Medium · **Depends on:** Steps 7, 8

Add an optional chart region to the observation area that renders one JavaFX `LineChart<Number, Number>` **per scale
group**, driven entirely by the metric descriptors. `stepCount` is on the X axis and the **raw** metric value on the Y
axis — no normalization. This section is the implementation-ready specification; the decisions below are final.

**1. New descriptor field and enum:**

- New enum `StatisticChartGroup { NONE, PRIMARY, SECONDARY }` in `simulations.core.model` (no `null`; `NONE` = not
  charted). Enum order is the stacking order (`PRIMARY` on top).
- `StatisticMetric` gains a fifth record component `StatisticChartGroup chartGroup` **plus** a secondary four-argument
  constructor that delegates to the canonical constructor with `chartGroup = NONE`. Existing four-argument call sites
  compile unchanged; only the charted metrics of the four charted simulations use the five-argument form.

**2. Group assignment (final scope):** only four simulations declare a non-`NONE` group; every other metric stays
`NONE`.

| Simulation | Group assignment                                      | Sub-charts |
|------------|-------------------------------------------------------|------------|
| Wa-Tor     | `fishCells`, `sharkCells` → `PRIMARY`                 | 1 (shared) |
| Forest     | `treeCells` → `PRIMARY`; `burningCells` → `SECONDARY` | 2          |
| Conway     | `aliveCells` → `PRIMARY`                              | 1          |
| Etpets     | `activePetCells` → `PRIMARY`                          | 1          |

Complement metrics (Conway `deadCells`, Forest `emptyCells`), monotonic metrics (Langton `visitedCells`), and
cumulative counters (`cumulative*DeathCount`) are intentionally excluded. Wa-Tor is the only multi-line chart; its
shared axis preserves the fish/shark amplitude relation, which is the whole point of overlaying them.

**3. The view (`StatisticHistoryChartView` in `simulations/core/view/`):**

- **Not generic.** It works purely key-based (reads `StatisticSample.values().get(key)` and
  `StatisticExtrema.maximumValues().get(key)`), never via the extractor. Constructor takes
  `List<StatisticMetric<?>>`, `ReadOnlyObjectProperty<List<StatisticSample>>` (history, Step 8), and
  `ReadOnlyObjectProperty<StatisticExtrema>` (extrema, Step 8).
- **Build once, then only update.** At construction, create exactly one `LineChart<Number, Number>` per distinct
  non-`NONE` group (groups are static, derived from the metric list), each with its own `NumberAxis` pair, held as
  fields inside a `VBox` in enum order. Also create one `XYChart.Series` per charted metric up front.
- **Update path:** register a listener on `statisticsHistoryProperty` (plus an initial render from the current value).
  On each change, on the FX thread:
    1. Read the extrema snapshot via `statisticsExtremaProperty().get()` (history and extrema are updated together, so
       no
       second listener is needed).
    2. For each group, compute `groupMax = ceil(max over the group's metric keys of extrema.maximumValues().get(key)
     .value())`, treating a missing key as `0`.
    3. Toggle the sub-chart `managed`/`visible` to `groupMax > 0`; toggle the whole `TitledPane` section
       `managed`/`visible` to "at least one group has `groupMax > 0`".
    4. For each **visible** sub-chart: set the Y axis to `[0, groupMax]` (auto-ranging off), set the X axis bounds from
       `history.get(0).stepCount()` to `history.getLast().stepCount()` (auto-ranging off; guard the single-sample case
       so
       lower != upper), rebuild each metric's data list skipping `NaN` values, and apply it atomically via
       `series.setData(newList)` (one scene-graph invalidation per series).
- **Chart config:** `setCreateSymbols(false)`, `setAnimated(false)`, legend shown (series names from
  `AppLocalization.getText(metric.labelKey())`), no sub-chart title, no Y-axis label. Series colors stay JavaFX
  default (series order is deterministic from `metrics()`, so colors are stable).
- **Do not draw until data exists.** Because `groupMax` is a full-run maximum (monotonic non-decreasing), a sub-chart
  appears once its group first exceeds `0` and never disappears afterward (no flicker). Shutdown (history `List.of()`
    + `StatisticExtrema.empty()` from Step 8) hides the whole section automatically. Consequence: Forest's `fire`
      sub-chart appears only once something first burns; Wa-Tor/Conway/Etpets charts are present from the step-0 sample.

**4. Integration into observation views (generic, self-hiding):**

- `AbstractObservationView` gains `buildChartSection()` that uses the already-stored `genericMetrics` field plus the
  view model's history/extrema properties to construct the `StatisticHistoryChartView`, wrapped in an initially
  **collapsed** `TitledPane`. It returns `@Nullable Region` (`null` when no metric has `chartGroup != NONE`).
- `createObservationScrollPane(Region...)` is adjusted to **skip `null`** regions. All eight observation views call
  `buildChartSection()` once and pass the result to the scroll pane; the four non-charted simulations receive `null`
  and show nothing. No chart-specific code lives in any concrete view.

**5. Styling:** add a new `FXStyleClasses` constant (e.g. `OBSERVATION_CHART` → `.observation-chart`) and set sub-chart
height/spacing in the existing observation stylesheet (no inline sizing in Java).

**6. New i18n keys (both bundles, alphabetically sorted, `=` column-aligned):**

- `observation.section.charts` — the `TitledPane` section title.
- `observation.chart.axis.step` — the X-axis label.

**7. Invariant (guard test):** every metric with `chartGroup != NONE` must have `extremaMode ∈ {MAX, MIN_AND_MAX}`;
otherwise no full-run group maximum exists for the Y-axis ceiling. Add a test iterating all simulations' `metrics()`
that asserts this coupling so it cannot drift.

**Timing metrics (optional):** `StatisticSample.stepTimingStatistics()` could be plotted as an additional series
without model changes — the series key is not in `metrics()`, so it needs explicit wiring; deferred to an optional
enhancement.

**Tests:** using [FxTestSupport](../../app/src/test/java/de/mkalb/FxTestSupport.java), unit-test
`StatisticHistoryChartView` with synthetic history + extrema via `SimpleObjectProperty`. Expose a package-private
accessor (e.g. `List<LineChart<Number, Number>> chartsForTest()`) and assert from the mirrored test package
`simulations.core.view`: number of sub-charts = distinct non-`NONE` groups; series per sub-chart; data-point counts;
`NaN` points skipped; `[0, groupMax]` Y bounds; and section/sub-chart visibility toggling on the `groupMax > 0` rule.

**Step 9 (2026-08-11):** Production code fully implemented. `StatisticChartGroup { NONE, PRIMARY, SECONDARY }` added
to `simulations.core.model`; `StatisticMetric` gained a fifth `chartGroup` component with a backward-compatible
four-argument constructor. Conway (`aliveCells → PRIMARY`), Forest (`treeCells → PRIMARY`, `burningCells →
SECONDARY`), Wa-Tor (`fishCells`, `sharkCells → PRIMARY`), and Etpets (`activePetCells → PRIMARY`) declare non-NONE
groups; all other metrics retain `NONE` via the convenience constructor. `StatisticHistoryChartView` (package-private,
`simulations.core.view`) renders one `LineChart<Number, Number>` per distinct non-NONE group, keyed on
`StatisticSample.values()` and `StatisticExtrema.maximumValues()`; it listens on `statisticsHistoryProperty` and
updates Y-axis ceiling, X-axis span, and series data on each change. `AbstractObservationView.buildChartSection()`
constructs the view and wraps it in an initially-collapsed `TitledPane`; `createObservationScrollPane` skips `null`
regions. All 8 observation views call `buildChartSection()`. `FXStyleClasses.OBSERVATION_CHART`, i18n keys
`observation.section.charts` and `observation.chart.axis.step` (both bundles, alphabetically sorted, `=`
column-aligned), and a `.observation-chart` CSS rule (pref-height 140) were added. `SimulationObservationViewModel`
gained two new abstract methods (`statisticsExtremaProperty()`, `statisticsHistoryProperty()`) that
`DefaultObservationViewModel` already satisfies. The invariant guard test `testChartedMetricsHaveMaxExtremaMode` was
added to `StatisticMetricRowTest`. `StatisticHistoryChartViewTest` (10 tests) was added to the mirrored test package
`simulations.core.view`, covering sub-chart count, series count, data-point counts, NaN skipping, Y-axis ceiling,
X-axis single-sample guard, and visibility toggling including shutdown reset. Note: 11 pre-existing test failures in
`StatisticMetricRowTest` and `TimedStatisticsTrackingTest` (extrema-mode mismatches predating Step 9) remain
unresolved.

**Step 11 (2026-08-08):** The per-simulation min/max label wiring in all 8 observation views was replaced by a
generic descriptor-driven renderer in `AbstractObservationView`. Three new localization constants and bundle entries
(`observation.extremum.at.step`, `observation.extremum.max`, `observation.extremum.min`) were added to both locale
bundles (alphabetically sorted, `=` column-aligned). `AbstractObservationView` gained:

- `createGenericMetricSection(String titleKey, List<StatisticMetric<STA>> metrics)` — builds a GridPane with a name
  column, a current-value column, and — only when at least one metric in the list has an extrema mode other than
  `NONE` — separate min and max value columns with a right-aligned header row (`OBSERVATION_EXTREMUM_MIN` /
  `OBSERVATION_EXTREMUM_MAX`, styled via the dedicated `observation-extremum-header-label` CSS class). Sections
  without any tracked extrema render only the name and current-value columns. The current/min/max value columns
  share a common minimum width (`VALUE_COLUMN_MIN_WIDTH`, 60px) so values stay aligned across rows.
- `updateGenericMetricSection(Optional<STA> statistics, StatisticExtrema extrema)` — refreshes all labels; current
  values are cast to `int` after a `Double.isFinite` guard (all metrics are cell counts). Min/max values are shown
  as separate numeric labels (not concatenated text); each carries a tooltip with the step count at which the
  extremum occurred, attached only when a value is present (no empty tooltips before the first sample).

Each of the 8 concrete observation views (`Conway`, `Forest`, `Wator`, `Etpets`, `Snake`, `Sugar`, `Rebounding`,
`Langton`) was rewritten: per-metric private label fields and their localization key constants were removed; the
hand-coded current section plus the separate statistics section were replaced by a single
`createGenericMetricSection(OBSERVATION_SECTION_METRICS, <Sim>Statistics.metrics())` call; `updateObservationLabels()`
now calls `updateGenericMetricSection`. Simulation-specific selected-cell labels (Wator age, Snake head details, Sugar
energy/amount, Rebounding direction) were unchanged. Forest now shows `emptyCells` and its MAX extremum, which was
previously omitted; Snake now shows `livingSnakeHeadCells` and `wallCells`. A new `StatisticMetricRowTest` class
(8 tests) verifies each simulation's `metrics()` list size and each metric's `StatisticExtremaMode`, catching future
mode regressions at compile time. The now-unused `observation.section.current` / `observation.section.statistics`
localization keys were removed from both bundles. The center `SplitPane` divider (`AbstractMainView`,
`CENTER_SPLIT_PANE_DIVIDER_POSITION`) was reduced from `0.75d` to `0.65d`, giving the observation region more width
to accommodate the widened value columns.

### Step 11 — Generic Descriptor-Driven Observation Rows ✓ DONE

> **Type:** Refactor · **Priority:** Low · **Effort:** M · **Risk:** Medium (UI parity) · **Depends on:** Steps 2, 6

Replace the per-simulation min/max label wiring in the observation views with a generic row renderer that iterates
the `metrics()` list and renders current value + extrema (with step index from Step 6). This removes duplicated key
lookups and keeps views automatically in sync when metrics change. **Independent of Steps 8–10** — can land before
or after the chart feature.

**Scope:** All 8 observation views. Conway, Forest, and Wator already show extrema labels; the other 5 (Etpets, Snake,
Sugar, Rebounding, Langton) show only current values. After this step all 8 use the same renderer.

**Column layout** (as implemented): the min and max columns are added to the GridPane only when at least one metric
in the section has `extremaMode() != NONE`; sections without any tracked extrema render just the name and
current-value columns.

| extremaMode   | current column | min column | max column |
|---------------|----------------|------------|------------|
| `NONE`        | value          | —          | —          |
| `MIN`         | value          | value      | —          |
| `MAX`         | value          | —          | value      |
| `MIN_AND_MAX` | value          | value      | value      |

Step index is not shown inline; each populated min/max label carries a tooltip (`observation.extremum.at.step`) with
the step count at which the extremum occurred, attached only once a value exists. Formatting uses the existing
`setFormattedIntegerValue()` helper in `AbstractObservationView`, after a `Double.isFinite` guard (all current metrics
are cell counts).

**Implementation approach (as implemented):**

`AbstractObservationView` gained `createGenericMetricSection(String titleKey, List<StatisticMetric<STA>> metrics)` to
build the label grid and `updateGenericMetricSection(Optional<STA> statistics, StatisticExtrema extrema)` to refresh
it. Each concrete observation view calls `createGenericMetricSection(...)` once to build its metrics section, and its
`updateObservationLabels()` calls `updateGenericMetricSection(...)` with the current statistics optional and the
extrema snapshot.

**Migration path (reduce risk):** migrate one simulation (e.g., Wator, which already has the most extrema rows) first,
run all tests and visually verify layout parity, then migrate the remaining 7 in one batch.

**Tests:**

- Extend `TimedStatisticsTrackingTest` or add a `StatisticMetricRowTest` that verifies each simulation's `metrics()`
  list maps to the expected extrema-mode row layout, catching missing or mismatched keys early.
- Existing observation-view tests (if any) must remain green.

### Optional / Independent Enhancements

Not on the critical path; pick up as needed. Split into two groups: items planned for this branch (small,
independent chart/label polish) and items deferred to a later branch (bigger features or ones with still-open
design questions). The per-metric history capacity idea was replaced by a shared capacity bump plus a separate
per-metric chart display window; the sparkline/tooltip idea was dropped as redundant with the existing per-group
charts; the two event-related items were merged into one; and the per-metric chart selection toggles item was
dropped entirely.

#### Planned for this branch

1. **Drop the trailing colon from localized metric names** ✓ DONE — Refactor · **High** · S · Depends on: —.
   Localized `labelKey` values in `i18n.messages` currently end with a colon (e.g. "Alive cells:"), which is meant
   for the observation table but also leaks into the `StatisticHistoryChartView` legend, where a trailing colon
   after each series name looks wrong. The colon is a UI layout concern, not a translation concern (both `en_US`
   and `de_DE` use `:` as a label separator), so it must not live in the resource bundles at all. Remove the
   trailing colon from **all** metric `labelKey` values in both locale bundles — independent of whether the metric
   is charted — and append the colon explicitly in code only at the one place that needs it: the name column in
   `AbstractObservationView.createGenericMetricSection` / `updateGenericMetricSection`. The
   `StatisticHistoryChartView` legend then shows the plain name with no special-casing.
2. **"Nice" Y-axis ceiling rounding** ✓ DONE — Feature · **High** · S · Depends on: —.
   `StatisticHistoryChartView` currently sets the Y-axis upper bound to the exact `ceil(groupMax)` of the group's
   running maximum, which changes every time a new group maximum is reached and produces awkward, non-round bounds
   (e.g. `12`, `2495`). Round the ceiling up using the standard **1-2-5 sequence**
   (1, 2, 5, 10, 20, 50, 100, 200, 500, 1000, 2000, 5000, ...) — the ceiling is the smallest value of the form
   `{1, 2, 5} × 10^n` that is `>= groupMax` (e.g. `12 → 20`, `2495 → 5000`). Since `groupMax` already changes
   dynamically as history/extrema update, this rounding must be recomputed on every update rather than fixed at
   construction time.
3. **Per-metric configurable history capacity** ✓ DONE — Feature · **High** · M · Depends on: —.
   The original per-metric history-capacity idea (including a `0` opt-out) is dropped — analysis showed the
   memory/CPU cost of a much larger shared history is negligible at this app's scale (roughly ~180 KB extra per
   running simulation going from 100 to 1000 samples; chart series rebuilds stay sub-millisecond). Instead, two
   independent, simpler changes replace it:
    - Raise the single shared `StatisticHistory.DEFAULT_CAPACITY` from 100 to **1000**, so slow-developing
      simulations such as Etpets retain enough history for meaningful trends. This is pure retention and does not
      affect `StatisticExtremaTracker` (already full-run) or require any chart-side change on its own.
    - Add a **per-metric `chartWindowSize`** field to `StatisticMetric` (alongside `chartGroup`), defaulting to a
      value close to the old capacity (e.g. 100) via the existing convenience constructor, so only the charted
      metrics of the 4 charted simulations need to specify it explicitly. `StatisticHistoryChartView` renders only
      the **trailing `chartWindowSize` samples** of the retained history for each sub-chart's series and X-axis
      bounds, instead of the full history — this is a display concern, decoupled from retention. Since metrics
      sharing a `chartGroup` render on one chart with one shared X-axis, they must agree on the same window size;
      add a guard test enforcing that (alongside the existing `chartGroup`/`extremaMode` coupling test). The actual
      per-simulation window values (e.g. Wa-Tor vs. Conway) are chosen later via manual visual testing, since the
      right window depends on how much each simulation's metrics fluctuate.
4. **Window-relative Y-axis scaling** ✓ DONE — Feature · **High** · M · Depends on: Planned item 3 (chart window).
   `StatisticHistoryChartView` currently sets the Y-axis ceiling from the full-run maximum
   (`StatisticExtrema.maximumValues()`), rounded via `niceCeiling`. For simulations that start high and settle much
   lower (e.g. Conway's `aliveCells`, which typically starts near 50% occupancy and dies down to a small stable
   population), this wastes most of the chart's vertical resolution on the long, flat post-settling phase. Replace
   the ceiling source with the trailing `chartWindowSize` window (the same window already used for the X-axis),
   applied uniformly to all charted groups (no new per-metric opt-in):
    - **Growth:** immediate — whenever the windowed max (across all series in the group) exceeds the current
      ceiling, grow to `niceCeiling(windowedMax)` right away.
    - **Shrink:** gated — only re-evaluated when `max(latest value per series in the group) < 20%` of the current
      ceiling (a fixed threshold constant). When triggered, recompute `niceCeiling(windowedMax)` and shrink to it.
      This avoids constant axis "breathing" from every small dip.
    - **State:** each group chart needs a remembered current-ceiling value (mutable per-group state, since the
      ceiling is no longer recomputed from scratch on every update). Reset it to unset whenever history becomes
      empty (simulation shutdown), so a new run starts fresh instead of inheriting the previous run's ceiling — the
      chart view instance is built once per simulation screen and reused across restarts
      (`DefaultMainViewModel.shutdownSimulation()` only resets the `statisticsHistory`/`statisticsExtrema`
      properties, not the view).
    - **Minimum ceiling / visibility:** `niceCeiling(0)` returns `1` instead of `0`, and the existing
      `groupMax > 0` visibility gate is removed entirely — a chart becomes visible as soon as history is non-empty
      (from step 0), even while all its values are `0` (e.g. Forest's burning-cells sub-chart would show
      immediately with a flat `0` line instead of only appearing once something first burns).
    - `StatisticExtrema` is no longer read by the chart at all (it remains the source for the observation table
      rows). The `StatisticMetricRowTest.testChartedMetricsHaveMaxExtremaMode` guard test — which asserted every
      charted metric has extrema mode `MAX`/`MIN_AND_MAX` because the old ceiling needed a full-run max — was
      removed as it is no longer a real invariant.
    - `StatisticHistoryChartViewTest` was rewritten for the new ceiling logic and the always-visible-from-step-0
      behavior.

   **Implementation (2026-08-16):** `StatisticHistoryChartView` gained a `Map<StatisticChartGroup, Double>
   ceilingByGroup` field holding the remembered ceiling per group, and `SHRINK_THRESHOLD_RATIO = 0.2`. `updateCharts`
   now computes `windowedMax` (max finite value across the group's keys over the trailing-window samples) and
   `latestMax` (max finite value across the group's keys in the single most recent sample), then applies: if the
   ceiling is unset (`<= 0.0`) or `windowedMax > currentCeiling` → grow to `niceCeiling(windowedMax)`; else if
   `latestMax < 0.2 * currentCeiling` → shrink to `niceCeiling(windowedMax)`; otherwise the ceiling is unchanged.
   `ceilingByGroup` is cleared whenever `history.isEmpty()`, restoring the fresh/unset state on the next run.
   `niceCeiling` now treats any input `<= 1.0` as `1.0` (previously `<= 0.0` → `0.0`), and the `groupMax > 0`
   visibility gate was removed — `chartVisible` is now simply `!history.isEmpty()`. The `extremaProperty` constructor
   parameter was dropped from `StatisticHistoryChartView` (and the `buildChartSection()` call site in
   `AbstractObservationView`) since the chart no longer reads `StatisticExtrema`. The
   `testChartedMetricsHaveMaxExtremaMode` guard test was removed from `StatisticMetricRowTest`.
   `StatisticHistoryChartViewTest` was rewritten: existing structure/data-point/X-axis tests were adapted to the new
   constructor signature, and new tests cover the always-visible-with-all-zero-values case, the minimum ceiling of
   `1`, immediate growth, the shrink gate holding when the latest value stays above 20% of the ceiling, an actual
   shrink once the prior peak leaves the trailing window and the latest value drops below the gate, and the
   ceiling-reset-on-empty-history contract (verified against the case where a stale ceiling would otherwise survive
   the gate check for a new run's first sample).

#### Deferred

1. **CSV export + manual `*Analyzer`** — Feature · Low · M · Depends on: —.
   Not implemented now. If picked up later, scope is bigger than a dev-only utility: it must work both for devs
   (manual/offline analysis) and for end users via a GUI export action, and must also export the run's
   configuration (not just `stepCount` + one column per metric key), so the exported data is self-describing and
   reproducible. Whether CSV is the right export format (vs. e.g. JSON) is an open question, deliberately left
   unresolved until this item is actually scheduled.
2. **Richer `StatisticMetric` metadata** — Feature · Low · M · Depends on: —.
   Not implemented now; re-scope at the time Deferred item 3 or Deferred item 4 is actually picked up, building
   only the metadata those items concretely need rather than a speculative general formatter/unit/display-policy
   system now. Add an optional value formatter, unit, and display policy (integer / percentage / duration) so
   chart axes and observation rows format consistently, removing per-view casts like `(int) value`. Recommended
   before Deferred item 3 (percentages/ratios need a formatter) and Deferred item 4 (a smoothed series still
   needs consistent display).
3. **Derived / computed metrics** — Feature · Low · M · Depends on: Deferred item 2 (percentage/ratio formatting).
   Not implemented now; deferred alongside Deferred item 2, since an unformatted raw-`double` ratio in the table
   would add little value and would need redoing once that item's formatting lands. Ratios and densities (Wator
   `sharkCells / fishCells`, Forest tree-coverage `%`, Sugar `agentCells / resourceCells`); relies on the existing
   non-finite (divide-by-zero) guard.
4. **Rolling averages / smoothing** — Feature · Low · M · Depends on: Planned item 3 (history capacity + chart
   window). Not implemented now. Planned item 3 (capacity 1000 + per-metric chart window) resolves the original
   blocker (a meaningful window to average over), but this remains a distinct new feature — a derived
   moving-average series per charted metric needs its own design (overlay styling/color, averaging window size,
   whether it's toggleable) rather than being folded into that item's chart-polish work. Revisit as its own
   scoped task once Planned item 3 has landed and the chart windows have been visually tuned.
5. **Stagnation / termination detection** — Feature · Low · M · Depends on: —.
   Not implemented now. Beyond the metric-agnostic detection logic itself (e.g. `changedCells == 0` for K
   consecutive steps), this needs an unresolved product decision — whether it's purely a UI indicator (a
   badge/label) or should actually auto-stop the run, the latter touching the `SimulationState` run/pause
   machinery with real regression risk — plus deciding which metric(s) per simulation qualify and the threshold
   `K`. Left open until scheduled. Can feed into Deferred item 6 (Per-step events & chart annotations) as one of
   the annotated event types.
6. **Per-step events & chart annotations** — Feature · Low · L · Depends on: —.
   Merged from the former separate "Per-step event / result display" and "Event annotations" items, since they
   overlapped. Not implemented now; this is the largest item in the backlog and touches every layer
   (manager-level event capture, history/ViewModel forwarding, chart-annotation rendering, observation-panel
   display) — realistically its own follow-up plan document rather than a paragraph here when it is picked up.
   Capture discrete simulation events and results (e.g. Etpets `HatchEgg` / `Death`, forest ignition, snake death)
   and surface them both inline in the observation panel (as counts or markers) and as annotations on the history
   chart at the step where they occurred (extinction, first reproduction, snake death, ignition); ties into the
   deferred edit-mode history extension.

## Review File Lists

The following two lists enumerate every file touched by this feature branch (`feature/statistic-history-and-extrema`),
relative to its merge base with `main`, as the basis for a code review pass.

### New Files

- ✅ `app/src/main/java/de/mkalb/etpetssim/simulations/core/model/StatisticChartGroup.java`
- ✅ `app/src/main/java/de/mkalb/etpetssim/simulations/core/model/StatisticExtrema.java`
- ✅ `app/src/main/java/de/mkalb/etpetssim/simulations/core/model/StatisticExtremaMode.java`
- ✅ `app/src/main/java/de/mkalb/etpetssim/simulations/core/model/StatisticExtremaTracker.java`
- ✅ `app/src/main/java/de/mkalb/etpetssim/simulations/core/model/StatisticExtremum.java`
- ✅ `app/src/main/java/de/mkalb/etpetssim/simulations/core/model/StatisticHistory.java`
- ✅ `app/src/main/java/de/mkalb/etpetssim/simulations/core/model/StatisticMetric.java`
- ✅ `app/src/main/java/de/mkalb/etpetssim/simulations/core/model/StatisticSample.java`
- ✅ `app/src/main/java/de/mkalb/etpetssim/simulations/core/view/StatisticHistoryChartView.java`
- ✅ `app/src/test/java/de/mkalb/etpetssim/simulations/core/model/ExtremaGoldenValueAnalyzer.java`
- ✅ `app/src/test/java/de/mkalb/etpetssim/simulations/core/model/StatisticChartGroupTest.java`
- ✅ `app/src/test/java/de/mkalb/etpetssim/simulations/core/model/StatisticExtremaModeTest.java`
- ✅ `app/src/test/java/de/mkalb/etpetssim/simulations/core/model/StatisticExtremaTest.java`
- ✅ `app/src/test/java/de/mkalb/etpetssim/simulations/core/model/StatisticExtremaTrackerTest.java`
- ✅ `app/src/test/java/de/mkalb/etpetssim/simulations/core/model/StatisticExtremumTest.java`
- ✅ `app/src/test/java/de/mkalb/etpetssim/simulations/core/model/StatisticHistoryTest.java`
- ✅ `app/src/test/java/de/mkalb/etpetssim/simulations/core/model/StatisticMetricLabelKeyTest.java`
- ✅ `app/src/test/java/de/mkalb/etpetssim/simulations/core/model/StatisticMetricRowTest.java`
- ✅ `app/src/test/java/de/mkalb/etpetssim/simulations/core/model/StatisticMetricTest.java`
- ✅ `app/src/test/java/de/mkalb/etpetssim/simulations/core/model/StatisticSampleTest.java`
- ✅ `app/src/test/java/de/mkalb/etpetssim/simulations/core/model/TimedStatisticsTrackingTest.java`
- ✅ `app/src/test/java/de/mkalb/etpetssim/simulations/core/view/StatisticHistoryChartViewTest.java`
- ✅ `app/src/test/java/de/mkalb/etpetssim/simulations/core/view/package-info.java`
- ✅ `app/src/test/java/de/mkalb/etpetssim/simulations/core/viewmodel/DefaultObservationViewModelTest.java`
- ✅ `app/src/test/java/de/mkalb/etpetssim/simulations/core/viewmodel/package-info.java`

### Changed Files

- `app/src/main/java/de/mkalb/etpetssim/core/AppLocalizationKeys.java`
- ✅ `app/src/main/java/de/mkalb/etpetssim/simulations/conway/model/ConwaySimulationManager.java`
- ✅ `app/src/main/java/de/mkalb/etpetssim/simulations/conway/model/ConwayStatistics.java`
- `app/src/main/java/de/mkalb/etpetssim/simulations/conway/view/ConwayObservationView.java`
- ✅ `app/src/main/java/de/mkalb/etpetssim/simulations/core/model/AbstractTimedSimulationManager.java`
- ✅ `app/src/main/java/de/mkalb/etpetssim/simulations/core/model/SimulationManager.java`
- `app/src/main/java/de/mkalb/etpetssim/simulations/core/view/AbstractDefaultMainView.java`
- `app/src/main/java/de/mkalb/etpetssim/simulations/core/view/AbstractMainView.java`
- `app/src/main/java/de/mkalb/etpetssim/simulations/core/view/AbstractObservationView.java`
- `app/src/main/java/de/mkalb/etpetssim/simulations/core/view/SimulationObservationView.java`
- `app/src/main/java/de/mkalb/etpetssim/simulations/core/viewmodel/AbstractConfigViewModel.java`
- `app/src/main/java/de/mkalb/etpetssim/simulations/core/viewmodel/DefaultMainViewModel.java`
- `app/src/main/java/de/mkalb/etpetssim/simulations/core/viewmodel/DefaultObservationViewModel.java`
- `app/src/main/java/de/mkalb/etpetssim/simulations/core/viewmodel/SimulationObservationViewModel.java`
- ✅ `app/src/main/java/de/mkalb/etpetssim/simulations/etpets/model/EtpetsSimulationManager.java`
- ✅ `app/src/main/java/de/mkalb/etpetssim/simulations/etpets/model/EtpetsStatistics.java`
- `app/src/main/java/de/mkalb/etpetssim/simulations/etpets/view/EtpetsObservationView.java`
- ✅ `app/src/main/java/de/mkalb/etpetssim/simulations/forest/model/ForestSimulationManager.java`
- ✅ `app/src/main/java/de/mkalb/etpetssim/simulations/forest/model/ForestStatistics.java`
- `app/src/main/java/de/mkalb/etpetssim/simulations/forest/view/ForestObservationView.java`
- ✅ `app/src/main/java/de/mkalb/etpetssim/simulations/langton/model/LangtonSimulationManager.java`
- ✅ `app/src/main/java/de/mkalb/etpetssim/simulations/langton/model/LangtonStatistics.java`
- `app/src/main/java/de/mkalb/etpetssim/simulations/langton/view/LangtonObservationView.java`
- ✅ `app/src/main/java/de/mkalb/etpetssim/simulations/rebounding/model/ReboundingSimulationManager.java`
- ✅ `app/src/main/java/de/mkalb/etpetssim/simulations/rebounding/model/ReboundingStatistics.java`
- `app/src/main/java/de/mkalb/etpetssim/simulations/rebounding/view/ReboundingObservationView.java`
- ✅ `app/src/main/java/de/mkalb/etpetssim/simulations/snake/model/SnakeSimulationManager.java`
- ✅ `app/src/main/java/de/mkalb/etpetssim/simulations/snake/model/SnakeStatistics.java`
- `app/src/main/java/de/mkalb/etpetssim/simulations/snake/view/SnakeObservationView.java`
- ✅ `app/src/main/java/de/mkalb/etpetssim/simulations/sugar/model/SugarSimulationManager.java`
- ✅ `app/src/main/java/de/mkalb/etpetssim/simulations/sugar/model/SugarStatistics.java`
- `app/src/main/java/de/mkalb/etpetssim/simulations/sugar/view/SugarObservationView.java`
- ✅ `app/src/main/java/de/mkalb/etpetssim/simulations/wator/model/WatorSimulationManager.java`
- ✅ `app/src/main/java/de/mkalb/etpetssim/simulations/wator/model/WatorStatistics.java`
- `app/src/main/java/de/mkalb/etpetssim/simulations/wator/view/WatorObservationView.java`
- `app/src/main/java/de/mkalb/etpetssim/ui/FXStyleClasses.java`
- `app/src/main/resources/css/scene.css`
- `app/src/main/resources/i18n/messages_de_DE.properties`
- `app/src/main/resources/i18n/messages_en_US.properties`
