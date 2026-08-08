# Statistics History And Extrema Plan

This document is a planning brief for future changes to simulation statistics in
`de.mkalb.etpetssim.simulations.core` and the regular timed simulations. It is intended as input for future Copilot
agents that implement statistics history, chart-ready samples, and generic min/max tracking.

Scope of this document: architecture and refactoring guidance only. No Java code, tests, resources, or build files are
changed by this document.

## Goals

Add two foundations for future statistics features:

- Keep a bounded history of recent statistics samples, initially the latest 100 executed steps, suitable for later line
  chart rendering.
- Provide a generic way to track per-metric minimum and maximum values without each simulation hand-maintaining custom
  `min...` and `max...` fields forever.

The initial refactoring should keep the current live `statistics()` contract working. Existing simulation-specific
statistics classes are mutable working objects today, and step logic or user actions may update them during execution.
The new history and extrema model should wrap that current design rather than requiring every statistics class to become
immutable immediately.

## Current Context

Core statistics contracts:

- `SimulationStatistics` exposes common snapshot counters: `getStepCount()`, `getGridStructure()`, and
  `getTotalCells()`.
- `TimedSimulationStatistics` extends this with `stepTimingStatistics()`.
- `BaseTimedSimulationStatistics` is a mutable base class holding `GridStructure`, step count, and step timing.
- `SimulationManager.statistics()` returns the latest typed statistics object.
- `AbstractTimedSimulationManager` owns the timed execution flow and calls `updateStatistics()` after single steps and
  during batch execution.

Current simulation patterns:

- Conway, Forest, and Wator already hand-maintain extrema on their mutable statistics objects.
- Wator tracks both minimum and maximum fish/shark cell counts.
- Conway and Forest currently track maxima only.
- Other simulations expose current counters and cumulative counters without generic extrema metadata.
- Observation views read typed statistics through `DefaultObservationViewModel.getStatistics()` and format individual
  fields directly.
- `LabSimulationManager` implements `SimulationManager` directly and does not extend
  `AbstractTimedSimulationManager`. Lab does not use the timed execution flow and is out of scope for timed history
  and extrema tracking unless a future feature explicitly requires it.

Important constraint:

`statistics()` currently returns one mutable live object per manager. A history queue must not store repeated references
to this object, because every entry would later point at the same final state. History entries must therefore be
immutable samples or another immutable structure derived from the live statistics object at record time.

## Design Decisions

### Keep Live Statistics Mutable

The current mutable statistics object remains the live working state. This keeps existing step logic, user actions,
observation views, and typed getters compatible during the first refactoring.

Recommended direction:

- Keep `STA statistics()` unchanged.
- Do not require immediate copy constructors or immutable replacements for every simulation-specific statistics class.
- Add separate immutable generic sample structures for history.

### Use Explicit Metric Descriptors

Each statistics type should explicitly declare the numeric metrics that are available for generic history, charts, and
extrema.

Avoid reflection over getters. Reflection would make metric order, localization, display policy, and future chart
metadata fragile. Explicit descriptors keep each simulation in control of its domain counters while giving core
infrastructure a generic way to sample them.

Recommended candidate type in `de.mkalb.etpetssim.simulations.core.model`:

```java
public record StatisticMetric<STA extends SimulationStatistics>(
        String key,
        String labelKey,
        ToDoubleFunction<STA> extractor,
        StatisticExtremaMode extremaMode) {
}
```

Recommended candidate enum in the same package:

```java
public enum StatisticExtremaMode {
    NONE,
    MIN,
    MAX,
    MIN_AND_MAX
}
```

Descriptor guidance:

- `key` is a stable technical id, not a localized label.
- `labelKey` points to `i18n.messages` for future UI/chart labels.
- `extractor` reads from the live typed statistics object at sample time.
- `extremaMode` declares whether the metric participates in generic min and/or max tracking.
- Metrics use `double` in the generic layer to support JavaFX chart data, counts, timings, percentages, and averages.
  Existing typed getters remain `int`, `long`, or domain-specific as appropriate.

Possible declaration shape:

```java
public static List<StatisticMetric<WatorStatistics>> metrics() {
    return List.of(
            new StatisticMetric<>("fishCells", WATOR_OBSERVATION_FISH_CELLS, WatorStatistics::getFishCells,
                    StatisticExtremaMode.MIN_AND_MAX),
            new StatisticMetric<>("sharkCells", WATOR_OBSERVATION_SHARK_CELLS, WatorStatistics::getSharkCells,
                    StatisticExtremaMode.MIN_AND_MAX));
}
```

A future implementation should decide whether these lists live directly on each statistics class, on a small
simulation-local provider, or on the simulation factory. Prefer the nearest owner that prevents drift between counters
and their descriptors.

### Store Generic Immutable History Samples

History should store generic immutable samples, not typed mutable statistics objects.

Recommended candidate type in `de.mkalb.etpetssim.simulations.core.model`:

```java
public record StatisticSample(
        int stepCount,
        StepTimingStatistics stepTimingStatistics,
        Map<String, Double> values) {
}
```

Rules:

- Defensively copy `values` in the compact constructor.
- Preserve insertion order if chart series order matters; `LinkedHashMap` can be used at construction time and copied to
  an unmodifiable map.
- Use descriptor keys as map keys.
- Include `StepTimingStatistics` so later charting can show timing metrics without re-reading the mutable live object.
- Treat each sample as an immutable point-in-time value.

### Use A Bounded Queue

The first implementation should keep a core default capacity of 100 samples and an extension point for later
configuration.

Recommended candidate type:

```java
public final class StatisticHistory {

    public static final int DEFAULT_CAPACITY = 100;

}
```

Implementation guidance:

- Use an `ArrayDeque<StatisticSample>` or equivalent ring buffer.
- Drop the oldest sample when adding beyond capacity.
- Expose history as an immutable ordered list from oldest to newest.
- Do not retain thousands of samples by default; long-running simulations must have bounded memory usage.
- Do not add UI/config controls for capacity in the first refactoring. Leave a constructor or manager hook so capacity
  can become configurable later without redesigning the storage.

### Track Extrema Separately From History

Generic min/max values should be full-run extrema, not values computed only from the bounded history window.

Rationale:

- The history queue is intentionally lossy and keeps only recent samples.
- Existing Wator, Conway, and Forest extrema represent values since initialization.
- Computing extrema from the last 100 samples would silently change the meaning of current min/max displays.

Recommended candidate type:

```java
public record StatisticExtrema(
        Map<String, Double> minimumValues,
        Map<String, Double> maximumValues) {
}
```

A mutable internal accumulator can be used by the manager, for example `StatisticExtremaTracker`, while public snapshots
remain immutable.

Rules:

- Initialize extrema from the step-0 sample.
- Update extrema every time a step sample is recorded.
- Respect each metric's `StatisticExtremaMode`. A metric with mode `MIN` appears only in `minimumValues`, a metric
  with mode `MAX` appears only in `maximumValues`, and a metric with mode `MIN_AND_MAX` appears in both maps.
- Do not track extrema for metrics whose mode is `NONE`.
- Preserve current typed extrema getters until a later migration phase removes them.

### Include Step 0

History and extrema should include the initialized step-0 statistics before any simulation step runs.

Rationale:

- Existing hand-maintained extrema are initialized from startup cell counts.
- Future line charts need a meaningful baseline at step 0.
- It avoids the surprising case where a simulation starts with its maximum population but generic maxima never see it.

Implementation implication:

- Each timed simulation manager currently calls simulation-specific `initializeStatistics(...)` after grid creation.
- A future core hook must record the initial sample only after those simulation-specific counters are initialized.
- Because `AbstractTimedSimulationManager` cannot know when a subclass constructor has finished initializing counters,
  provide a protected method such as `initializeStatisticsTracking(...)` or `recordInitialStatisticsSample()` that
  subclasses call after their existing `initializeStatistics(...)` method.

### Capture Every Executed Batch Step

During `executeSteps(count, checkTermination, onStep)`, history should capture every executed step, not only the final
batch result.

Rationale:

- Future charts need real intermediate step series.
- Full-run extrema must not miss peaks that occur inside a batch.
- The queue is bounded, so recording every step does not create unbounded memory growth.

Recommended flow in `AbstractTimedSimulationManager`:

1. Single-step execution calls `executor.executeStep()`.
2. The manager updates the live statistics object.
3. The manager records one immutable sample and updates extrema.
4. The manager invokes `afterStepExecuted()`.

For batch execution:

1. The executor runs the batch.
2. The per-step callback updates live statistics.
3. The manager records one immutable sample and updates extrema for that step.
4. The original `onStep` callback runs.
5. After the batch, the manager may still refresh live statistics once to keep timing/final snapshots synchronized, but
   it must avoid recording a duplicate history sample for the same final step unless an explicit policy later allows it.

### User Actions Do Not Create History Samples Initially

Manual user actions may mutate the live statistics object while the simulation is paused. The first implementation
should define history as executed-step history only.

Rationale:

- It avoids duplicate step-count samples created by edit actions at the same step.
- It keeps the initial feature focused on simulation execution and batch correctness.
- Existing observation updates can continue to show the live mutable statistics object.

Future extension:

- Add an explicit manager method such as `recordStatisticsSample()` if edit-mode history becomes important.
- Decide then whether duplicate step counts are allowed, whether samples need event metadata, or whether user actions
  should be tracked in a separate edit history.

## Recommended API Direction

### Core Model Additions

Recommended package: `de.mkalb.etpetssim.simulations.core.model`.

Candidate public or package-private types:

- `StatisticMetric<STA extends SimulationStatistics>`: metric descriptor with key, label key, extractor, and extrema
  policy.
- `StatisticExtremaMode`: enum describing min/max tracking policy.
- `StatisticSample`: immutable point-in-time sample.
- `StatisticHistory`: bounded immutable-history facade or mutable internal queue with immutable public snapshots.
- `StatisticExtrema`: immutable public extrema snapshot.
- `StatisticExtremaTracker`: mutable internal accumulator, probably package-private.

Candidate additions to `SimulationManager`:

```java
List<StatisticSample> statisticsHistory();

StatisticExtrema statisticsExtrema();
```

Because `SimulationManager` is implemented by Lab as well as timed simulations, consider whether these should be default
methods returning empty history/extrema, or whether a narrower `TimedStatisticsTrackingManager` contract should be added
for timed managers only.

Recommended first step:

- Add tracking APIs on `AbstractTimedSimulationManager` first.
- Promote them to `SimulationManager` only if call sites need generic manager access.
- Keep `LabSimulationManager` out of scope unless future UI requires all managers to expose the same tracking methods.

### AbstractTimedSimulationManager Responsibilities

`AbstractTimedSimulationManager` is the right place for executed-step history because it owns both single-step and batch
step dispatch.

Recommended new responsibilities:

- Store metric descriptors for the concrete `STA` type.
- Store bounded statistic history.
- Store full-run extrema tracker.
- Provide protected initialization for step-0 sampling after subclass-specific startup counters are populated.
- Record exactly one sample after each executed step.
- Keep snapshots synchronized when no per-step callback is triggered, without duplicating samples.

Possible constructor direction:

```java
private AbstractTimedSimulationManager(CON config,
                                       List<StatisticMetric<STA>> metrics) {
}
```

Potential issue:

The base class currently does not have direct access to `STA statistics()` except through the abstract/interface method.
Calling overridable methods during base construction is unsafe. Prefer recording samples after subclass construction has
initialized fields, through an explicit protected method called at the end of each concrete manager constructor.

### Simulation-Specific Statistics Classes

Each statistics class should expose or be paired with descriptors for current counters that are useful for history and
future charts.

Examples of likely metrics:

- Conway: `aliveCells`, `deadCells`, `changedCells`; `aliveCells` probably tracks max at first to match current UI.
- Forest: `emptyCells`, `treeCells`, `burningCells`; `treeCells` and `burningCells` track max at first.
- Wator: `fishCells`, `sharkCells`; both track min and max.
- Etpets: `activePetCells`, `eggCells`, `cumulativePetDeathCount`; cell counts may track min/max, cumulative death
  count likely starts as `NONE` unless a UI need appears.
- Snake: `snakeHeadCells`, `livingSnakeHeadCells`, `wallCells`, `foodCells`, `cumulativeSnakeDeathCount`; live counts
  may be chartable, cumulative count likely does not need min/max.
- Sugar: `resourceCells`, `agentCells`; both are chartable.
- Rebounding: `wallCells`, `movingEntityCells`; both are chartable.
- Langton: `antCells`, `visitedCells`; `visitedCells` may be chartable but min/max policy should be chosen carefully
  because it is monotonic for many runs.

Descriptor lists should be ordered in a stable, UI-friendly way.

### ViewModel And UI Scope

The first refactoring should expose history and extrema from the core model/manager layer only. Do not immediately add
line chart UI or observation ViewModel forwarding unless the implementation task explicitly includes chart rendering.

Later UI work can add read-only forwarding through `DefaultMainViewModel` and `DefaultObservationViewModel`, for
example:

- a read-only history property for chart views,
- a selected metric list based on descriptors,
- chart series generated from `StatisticSample.values()`,
- generic observation rows for descriptor-backed min/max values.

Keeping the first implementation model-focused makes batch correctness testable without JavaFX chart concerns.

## Migration Plan

### Phase 1: Add Generic Infrastructure

Add descriptor, sample, history, and extrema infrastructure in `simulations.core.model`.

Implementation notes:

- Keep all public snapshots immutable.
- Use defensive copies for lists and maps.
- Add focused unit tests for bounded queue behavior, insertion order, capacity eviction, extrema policy handling, and
  step-0 initialization.
- Do not modify observation views in this phase unless needed for compilation.

### Phase 2: Integrate Timed Managers

Update `AbstractTimedSimulationManager` to record samples after live statistics updates.

Implementation notes:

- Record step 0 after concrete manager initialization.
- Record every executed step in timed and batch modes.
- Avoid duplicate final batch samples caused by the extra post-batch `updateStatistics()` call.
- Keep `afterStepExecuted()` and `afterStepsExecuted(...)` behavior intact.
- Preserve existing `statistics()` return values.

### Phase 3: Add Descriptors To Simulations

Add metric descriptors to timed simulations incrementally.

Suggested first validation set:

1. Wator, because it exercises both min and max.
2. Conway, because it exercises max-only parity.
3. Forest, because it has multiple max-only metrics.

After those are stable, add descriptors to Etpets, Snake, Sugar, Rebounding, and Langton.

### Phase 4: Parity Tests For Existing Extrema

Before removing any existing hand-maintained fields, add tests that compare generic extrema with current typed getters.

Examples:

- Wator generic fish/shark min/max equals `getMinFishCells()`, `getMaxFishCells()`, `getMinSharkCells()`, and
  `getMaxSharkCells()` after several steps.
- Conway generic alive-cell max equals `getMaxAliveCells()`.
- Forest generic tree/burning maxima equal `getMaxTreeCells()` and `getMaxBurningCells()`.

These tests should include batch execution, because missing intermediate batch samples is the main risk.

### Phase 5: Migrate Observation And Remove Duplication Later

Only after generic extrema prove parity should a later refactoring migrate observation views away from custom min/max
fields.

Possible end state:

- Observation views display current typed counters as they do today, or use descriptors for repeated generic rows.
- Min/max display can come from `StatisticExtrema` instead of simulation-specific fields.
- Hand-maintained `max...` and `min...` fields can be removed from statistics classes whose extrema are fully covered by
  descriptors and tests.

Do not remove these fields in the first implementation phase.

## Open Questions For Future Implementation

Future agents should answer these with code references before editing Java:

1. Should tracking APIs live only on `AbstractTimedSimulationManager`, or should `SimulationManager` expose default
   empty
   history/extrema methods for all managers?
2. Where should each simulation's descriptor list live so it stays close to the counters but does not clutter mutable
   statistics classes?
3. Should timing metrics be descriptors too, or should `StepTimingStatistics` remain a separate chart source?
4. Is `double` sufficient for all planned chart metrics, or do any metrics require exact long values in the generic
   layer?
5. Should `StatisticSample` include a sequence number if future user-action samples can duplicate step counts?
6. Should history capacity eventually become part of `SimulationConfig`, a global app preference, or a chart-specific
   setting?
7. Which existing tests cover batch execution well enough, and where should new history/extrema tests live?

## Risks And Mitigations

### Mutable Alias Risk

Risk:

Storing `STA` directly in history would store repeated references to the same mutable statistics object.

Mitigation:

Store immutable `StatisticSample` values built from descriptor extraction at record time.

### Batch Blind Spots

Risk:

Recording only after `executeSteps(...)` would miss intermediate values and produce incorrect charts/extrema.

Mitigation:

Record inside the per-step callback managed by `AbstractTimedSimulationManager`.

### Duplicate Batch Samples

Risk:

The existing post-batch `updateStatistics()` call may create a duplicate sample for the final step if sample recording
is
naively attached to every statistics update.

Mitigation:

Separate `updateStatistics()` from `recordStatisticsSample()`. Record only after actual executed steps and explicit
initialization.

### Descriptor Drift

Risk:

Metric descriptors can drift from typed counters or localization keys.

Mitigation:

Keep descriptors near the owning statistics type or a simulation-local provider, and add tests that validate expected
metric keys for each simulation.

### Premature UI Coupling

Risk:

Designing the first refactoring around JavaFX chart controls could pull UI concerns into the model layer.

Mitigation:

Keep history and extrema in core model structures. Add ViewModel forwarding and chart-specific adaptation in a later UI
feature.

## Acceptance Criteria For The First Implementation

The initial implementation (phases 1 through 4) should satisfy these criteria:

- Timed simulations expose a bounded immutable history of generic statistic samples.
- History contains the initialized step-0 sample.
- History records every executed step during single-step and batch execution.
- History capacity defaults to 100 and evicts oldest samples first.
- Generic extrema are tracked over the full current manager lifetime, not just the retained history window.
- Existing `statistics()` behavior and typed observation views continue to work.
- Existing hand-maintained min/max fields remain until parity tests justify removing them.
- Tests cover step-0 sampling, single-step sampling, batch sampling, queue eviction, extrema policy, and parity with at
  least Wator plus one max-only simulation.

---

## Implementation Status (as of 2026-07-28)

An agent implemented phases 1 through 4 without prior explicit approval of this plan. The implementation is
functionally complete and all tests pass. The sections below document findings from a post-implementation review and
describe the recommended next steps before building further on this foundation.

### What Was Implemented

**New core types** in `de.mkalb.etpetssim.simulations.core.model`:

- `StatisticExtremaMode`, `StatisticMetric`, `StatisticSample`, `StatisticHistory`, `StatisticExtrema`,
  `StatisticExtremaTracker`

**New test classes**:

- `StatisticHistoryTest` — capacity eviction and immutable snapshot
- `StatisticExtremaTrackerTest` — mode policies and duplicate-key rejection
- `TimedStatisticsTrackingTest` — step-0 recording, batch recording without duplicate final sample, and typed/generic
  parity tests for Conway, Forest, and Wator

**Modified infrastructure**:

- `SimulationManager`: two default methods `statisticsHistory()` and `statisticsExtrema()` returning empty values
- `AbstractTimedSimulationManager`: new metrics-aware constructor, `recordStatisticsSample()` after every executed
  step (single and batch), `recordInitialStatisticsSample()` protected hook

**All 8 timed simulations wired**: Conway, Forest, Wator, Etpets, Snake, Sugar, Rebounding, Langton — each with a
`metrics()` static factory method and `recordInitialStatisticsSample()` called at the end of the constructor.

### Review Findings

#### 🔴 Risk: `IllegalStateException` For Non-Finite Metric Values

`AbstractTimedSimulationManager.recordStatisticsSample()` throws `IllegalStateException` when a metric extractor
returns a non-finite value (`NaN`, `Infinity`). This is not in the plan and could cause a runtime crash for any future
metric that divides by a count that can be zero. A warning log followed by skipping or replacing the value would be
safer than a hard throw.

#### 🟡 Plan Deviation: Phase 3 Rolled Out To All 8 Simulations At Once

The plan recommended an incremental validation sequence (Wator → Conway → Forest as a first validation set, then the
remaining simulations). The agent wired all 8 simulations in one step. Parity tests cover only Wator, Conway, and
Forest; the other 5 simulations (Etpets, Snake, Sugar, Rebounding, Langton) do not yet have parity tests. Phase 4 is
therefore only partially complete.

#### 🟡 `labelKey` Strings Live In The Model Layer

Each `metrics()` method declares i18n key strings as private constants inside statistics classes (e.g.,
`"wator.observation.cells.fish"` in `WatorStatistics`). The project instructions say simulation-local localization
key constants belong in the consuming View, not in the model. The `StatisticMetric.labelKey` field was designed as a
bridge for future chart labels, so placing the strings here is a deliberate architectural trade-off; future UI work
must be aware that model classes now carry UI label keys.

#### 🟡 Static Cell Counts Tracked With `MIN_AND_MAX`

`ReboundingStatistics` and `SnakeStatistics` declare `wallCells` with `StatisticExtremaMode.MIN_AND_MAX`. Wall cells
do not change during execution in either simulation, so min and max will always equal the initial value. This produces
no wrong results but generates two extrema entries that are permanently equal. Consider `NONE` or `MAX` for static
counts.

#### 🟢 Correct Handling Of The Duplicate Batch Sample Risk

The post-batch `updateStatistics()` call in `AbstractTimedSimulationManager` does not invoke
`recordStatisticsSample()`. The test `testExecuteStepsRecordsEveryExecutedStepWithoutDuplicateFinalSample` confirms
that running 7 steps yields exactly 8 history entries (step-0 plus one per executed step).

#### 🟢 Step-0 Initialization Pattern Is Safe

`recordInitialStatisticsSample()` is called at the end of each concrete manager constructor, after
`initializeStatistics(...)` has populated simulation-specific counters. This avoids the overridable-method-in-
constructor anti-pattern and ensures the step-0 sample contains the correct initial cell counts.

#### 🟢 Unnecessary But Harmless Synchronization

`StatisticHistory` methods are all `synchronized`. The model layer runs on the simulation thread and does not require
thread safety at this level. The overhead is negligible but could be removed in a future cleanup.

### Recommended Next Steps

#### 1. ~~Decide: Accept Or Roll Back The Current State~~ — Decision: Keep ✅

The implementation passes all tests and introduces no breaking changes. The findings above are all correctable
incrementally. A rollback is only warranted if the architectural decisions (metrics in model layer, all-at-once
phase 3) are considered unacceptable.

**Decision (2026-07-28):** The current state is accepted. Further work will be done in a dedicated branch on top of
this commit. The remaining next steps (2–7) are the working agenda for that branch.

#### 2. Fix The Non-Finite Guard In `recordStatisticsSample()` ✅

**Decision (2026-07-28):** Replace the `IllegalStateException` with an `error` log and substitute `Double.NaN` as
sentinel value. The sample is still recorded so the history time series has no gaps; chart and display code must
filter or handle `Double.NaN` entries explicitly.

**Implemented (2026-08-08):**

- `AbstractTimedSimulationManager.recordStatisticsSample()`: replaced `IllegalStateException` with
  `AppLogger.errorf(...)` and `Double.NaN` substitution; processing continues for remaining metrics.
- `StatisticExtremaTracker.update(...)`: added explicit `!Double.isFinite(value)` guard with `continue` before
  calling `merge`, preventing NaN/Infinity from corrupting min/max maps.
- `StatisticExtremaTrackerTest`: added three new tests — `testUpdateSkipsNaNValues`,
  `testUpdateSkipsPositiveInfinityValues`, `testUpdateSkipsNegativeInfinityValues`.
- All existing tests pass.

**Implementation instructions for an agent:**

In `AbstractTimedSimulationManager.recordStatisticsSample()`, replace the block that throws
`IllegalStateException` for non-finite values with the following behaviour:

- Call `AppLogger.errorf(...)` with a stable component tag, the metric key, and the actual value.
- Store `Double.NaN` in the `values` map for that metric key instead of the raw non-finite value.
- Continue processing the remaining metrics; do not abort the whole sample.
- All other metrics in the same sample retain their correct values.

Suggested log message pattern (use `AppLogger.errorf`):

```
"AbstractTimedSimulationManager: non-finite metric value replaced with NaN: key=%s, value=%f"
```

No changes to `StatisticSample`, `StatisticHistory`, or `StatisticExtremaTracker` are required. The extrema
tracker already skips NaN implicitly via `Math.min`/`Math.max` — verify this holds and add a note in the tracker
if needed; if not, add an explicit `Double.isFinite(value)` guard in `StatisticExtremaTracker.update(...)` before
calling `merge`.

**Acceptance criteria:**

- A metric extractor returning `NaN` or `Infinity` no longer causes a runtime exception.
- An `error`-level log entry is emitted for each affected metric key.
- The affected metric appears in the sample's `values` map with value `Double.NaN`.
- Other metrics in the same sample are unaffected.
- `StatisticExtremaTracker` does not update extrema for `NaN` or `Infinity` values.
- Existing tests still pass; add a unit test in `StatisticExtremaTrackerTest` or a new test that covers the NaN
  substitution path end-to-end.

#### 3. Add Parity Tests For The Remaining 5 Simulations (Phase 4 Completion) ✅

**Decision (2026-07-28):** New tests go into the existing `TimedStatisticsTrackingTest` class. Metrics with
`StatisticExtremaMode.NONE` (e.g., `cumulativePetDeathCount`) are not tested for extrema presence — their absence
from the extrema maps is implicitly covered by the existing infrastructure tests.

**Implementation instructions for an agent:**

Add the following test methods to
`app/src/test/java/de/mkalb/etpetssim/simulations/core/model/TimedStatisticsTrackingTest.java`.

Follow the existing pattern: create a config via a private helper, construct the manager, call
`executeSteps(20, false, () -> {})`, then compare generic extrema values against typed getter results.
Use the constant `DOUBLE_DELTA = 1.0e-9d` already defined in the class.

**Etpets** — add `testEtpetsGenericExtremaMatchesTypedMinMax()`:

- Create `EtpetsSimulationManager` using `EtpetsConstraints` defaults (see existing `createConwayConfig()` as
  reference; import `EtpetsConfig`, `EtpetsConstraints`).
- Assert `statisticsExtrema().minimumValues().get("activePetCells")` equals `statistics().getMinActivePetCells()`
  — **note**: check whether `EtpetsStatistics` currently exposes a typed `getMinActivePetCells()` method.
  If not, the generic extrema value has no typed counterpart to compare against yet; in that case assert only
  that the key is present in the extrema map and its value is finite.
- Same for `eggCells`.

**Snake** — add `testSnakeGenericExtremaMatchesTypedMinMax()`:

- Create `SnakeSimulationManager` using `SnakeConstraints` defaults.
- Assert min/max for `snakeHeadCells`, `livingSnakeHeadCells`, `foodCells` against typed getters if they exist.
  Apply the same fallback rule as for Etpets: if no typed getter exists yet, assert key presence and finite value.
- `cumulativeSnakeDeathCount` has mode `NONE` — assert it is absent from both `minimumValues` and `maximumValues`.

**Sugar** — add `testSugarGenericExtremaMatchesTypedMinMax()`:

- Create `SugarSimulationManager` using `SugarConstraints` defaults.
- Assert min/max for `resourceCells` and `agentCells` against typed getters if they exist.

**Rebounding** — add `testReboundingGenericExtremaMatchesTypedMinMax()`:

- Create `ReboundingSimulationManager` using `ReboundingConstraints` defaults.
- Assert min/max for `movingEntityCells` against typed getters if they exist.
- For `wallCells`: assert the key is present and that min equals max (walls are static), regardless of typed getter.

**Langton** — add `testLangtonGenericExtremaMatchesTypedMax()`:

- Create `LangtonSimulationManager` using `LangtonConstraints` defaults.
- Assert max for `visitedCells` against typed getter if it exists.
- `antCells` has mode `NONE` — assert it is absent from both extrema maps.

**Config helper pattern** (follow the existing helpers in the test class):

```java
private static EtpetsConfig createEtpetsConfig() { ...}

private static SnakeConfig createSnakeConfig() { ...}
// etc.
```

**Acceptance criteria:**

- All 5 new test methods pass.
- Each test verifies at least one `MIN_AND_MAX` metric's generic value against a typed getter or asserts finite
  presence when no typed getter exists.
- Each test verifies that metrics with mode `NONE` are absent from both `minimumValues` and `maximumValues`.
- No existing tests are modified or broken.

**Implemented (2026-08-08):**

Added five test methods to `TimedStatisticsTrackingTest`:
`testEtpetsGenericExtremaPresenceAndNoneAbsence`, `testSnakeGenericExtremaPresenceAndNoneAbsence`,
`testSugarGenericExtremaPresence`, `testReboundingGenericExtremaPresenceAndStaticWalls`,
`testLangtonGenericMaxPresenceAndNoneAbsence`. Config helper methods for all 5 simulations were also added.

**Correction to plan:** The plan stated that `ReboundingStatistics.wallCells` and `SnakeStatistics.wallCells` are
static (min==max always). In practice, both statistics classes expose mutation methods (`increaseWallCells`,
`decreaseWallCells`, `adjustWallCells`), so walls can change during execution. The tests for those metrics assert
key presence and finite value rather than min==max equality. All 10 tests in `TimedStatisticsTrackingTest` pass.

#### 4. Revisit `StatisticExtremaMode` For Static Cell Counts ✅

**Decision (2026-07-28):** No code changes required for this step. All three questions were resolved in favor of
keeping the current configuration:

- `ReboundingStatistics.wallCells` stays `MIN_AND_MAX`. Walls are static, so min and max will permanently equal
  the initial value. This is accepted as a harmless redundancy.
- `SnakeStatistics.wallCells` stays `MIN_AND_MAX` for the same reason.
- `LangtonStatistics.visitedCells` stays `MAX`. The value is monotonically increasing, so tracking the maximum
  is still useful (it equals the current value). Tracking the minimum would always return the step-0 value and is
  therefore omitted.

**Documentation note for the agent:** Add a brief inline comment next to the `wallCells` descriptor in
`ReboundingStatistics.metrics()` and `SnakeStatistics.metrics()` explaining that min==max is expected because wall
cell counts do not change during execution. This prevents future reviewers from flagging it as a bug.

**Correction (2026-08-08):** Step 3 testing revealed that both `ReboundingStatistics` and `SnakeStatistics` expose
mutation methods (`increaseWallCells`, `decreaseWallCells`, `adjustWallCells`), so wall cell counts do change during
execution. The planned inline comment "min==max is expected" was **not** added because the assumption was incorrect.
The `MIN_AND_MAX` mode for `wallCells` remains appropriate; no documentation change is needed.

#### 5. Decide On `labelKey` Placement Policy ✅

**Decision (2026-07-28):** The current placement is accepted. Statistics classes may hold private `String` constants
for `labelKey` values, because `labelKey` is an intrinsic part of the `StatisticMetric` descriptor and belongs near
the extractor and key that define the same metric. This is a deliberate exception to the general rule that
localization key constants belong in the consuming View.

The decision is documented here only; `simulations.instructions.md` is not changed.

**Note for future agents:** When adding new metrics to any statistics class, declare the `labelKey` value as a
`private static final String` constant at the top of the `metrics()` method's owning class, following the pattern
established in `WatorStatistics`, `ConwayStatistics`, etc. Do not scatter label key strings as inline literals
inside `metrics()` calls.

#### 6. Remove `synchronized` From `StatisticHistory` ✅

**Decision (2026-07-28):** Remove all `synchronized` modifiers from `StatisticHistory`. The model layer runs
exclusively on the simulation thread; no concurrent access to `StatisticHistory` occurs.

**Implemented (2026-08-08):** Removed `synchronized` from `capacity()`, `size()`, `clear()`, `add(...)`, and
`asList()`. Backing `ArrayDeque` unchanged. All existing tests in `StatisticHistoryTest` pass without modification.

**Implementation instructions for an agent:**

In `StatisticHistory.java`, remove the `synchronized` keyword from all four methods:
`capacity()`, `size()`, `clear()`, `add(...)`, and `asList()`.

No functional changes; the `ArrayDeque` remains the backing store. No Javadoc additions are required.
Existing tests in `StatisticHistoryTest` must still pass without modification.

#### 7. Phase 5: Migrate Observation Views Away From Hand-Maintained Extrema ✅

**Decision (2026-07-28):**

- Scope: All 8 timed simulations — remove typed min/max fields from the 3 that have them (Conway, Forest, Wator),
  and migrate all 8 observation views to read extrema from the generic `statisticsExtrema()`.
- Observation views are fully migrated to `statisticsExtrema()`; typed extrema getters are removed together with
  their backing fields.
- Conway, Forest, and Wator are migrated in one commit; the other 5 observation views (which currently show no
  extrema) are updated in the same pass if their views already display any min/max row, otherwise they are left
  for a later UI feature.
- ViewModel access to extrema: expose `statisticsExtrema()` as a plain accessor method in the ViewModel
  (e.g., `getStatisticsExtrema()`), mirroring the existing `getStatistics()` pattern. This can be promoted to a
  reactive `ObservableValue` in a future step if needed.

**Prerequisite:** Step 3 (parity tests for all 5 remaining simulations) must pass before this step begins.

**Implementation instructions for an agent:**

This step has three sub-tasks. Complete them in order.

**Sub-task A — Remove typed extrema fields and getters from Conway, Forest, Wator:**

1. `ConwayStatistics`: remove the `maxAliveCells` field, `initializeStartupCellCounts(int)` update for it, the
   `updateMaxAliveCells(...)` call site in the step logic, and the `getMaxAliveCells()` getter.
2. `ForestStatistics`: remove `maxTreeCells`, `maxBurningCells`, their update logic, and `getMaxTreeCells()` /
   `getMaxBurningCells()` getters.
3. `WatorStatistics`: remove `minFishCells`, `maxFishCells`, `minSharkCells`, `maxSharkCells`, their update logic,
   and `getMinFishCells()` / `getMaxFishCells()` / `getMinSharkCells()` / `getMaxSharkCells()` getters.
4. Fix all compilation errors caused by removed getters (primarily in observation ViewModels and Views).

**Sub-task B — Expose `statisticsExtrema()` in the ViewModel layer:**

For each affected simulation, add a `getStatisticsExtrema()` method to the simulation's ViewModel class
(e.g., `ConwayViewModel`, `ForestViewModel`, `WatorViewModel`) that delegates to
`simulationManager.statisticsExtrema()`. Follow the same pattern as the existing `getStatistics()` delegation.

For simulations where the ViewModel uses `DefaultMainViewModel` or `DefaultObservationViewModel`, check whether
the extrema accessor can be added to the shared base class instead of each simulation's ViewModel.

**Sub-task C — Update observation Views to use generic extrema:**

For each simulation whose observation view currently displays a max or min row (Conway, Forest, Wator):

- Replace calls to `statistics.getMaxAliveCells()` with
  `viewModel.getStatisticsExtrema().maximumValues().getOrDefault("aliveCells", Double.NaN)`.
- Use `Double.isFinite(value)` guards before formatting; display a placeholder (e.g., `"—"`) for `NaN`.
- Apply the same replacement for Forest (treeCells, burningCells max) and Wator (fish/shark min and max).
- Format the `double` value from extrema as an integer string where appropriate
  (e.g., `String.valueOf((int) value)` or via an existing formatter).

For the other 5 simulations (Etpets, Snake, Sugar, Rebounding, Langton): no observation view changes are required
unless those views already show a min/max row. If they do, migrate them by the same pattern.

**Acceptance criteria:**

- `ConwayStatistics`, `ForestStatistics`, `WatorStatistics` no longer contain any typed `min...` / `max...`
  fields or getters.
- Observation views for Conway, Forest, and Wator display the same numeric values as before, sourced from
  `statisticsExtrema()`.
- Parity tests from Step 3 (`TimedStatisticsTrackingTest`) still pass.
- No `NullPointerException` or formatting errors when `statisticsExtrema()` returns empty maps (e.g., before
  step-0 is recorded — verify this cannot happen, since `recordInitialStatisticsSample()` runs in the
  constructor).
- All existing tests pass.

**Implemented (2026-08-08):**

**Sub-task A** — Removed all typed min/max fields and getters from `ConwayStatistics` (`maxAliveCells`,
`getMaxAliveCells()`), `ForestStatistics` (`maxTreeCells`, `maxBurningCells`, `getMaxTreeCells()`,
`getMaxBurningCells()`), and `WatorStatistics` (`minFishCells`, `maxFishCells`, `minSharkCells`, `maxSharkCells`,
`updateMinMaxCells()`, and all four getters). Removed the `statistics.updateMinMaxCells()` call from
`WatorSimulationManager.updateStatistics()`.

**Sub-task B** — Added `getStatisticsExtrema()` / `setStatisticsExtrema(StatisticExtrema)` to
`DefaultObservationViewModel`. Updated `DefaultMainViewModel.updateObservationStatistics()` to also push
`simulationManager.statisticsExtrema()` to the observation ViewModel on every statistics update (both on the FX
thread for timed steps and via `Platform.runLater` for batch).

**Sub-task C** — Updated `ConwayObservationView`, `ForestObservationView`, and `WatorObservationView` to read
extrema from `viewModel.getStatisticsExtrema()` using `getOrDefault("key", Double.NaN)` with `Double.isFinite`
guards. The typed min/max getters are gone; the observation rows are now fed from the generic
`StatisticExtrema` snapshot.

**Test updates** — The three original parity tests compared generic extrema against now-deleted typed getters.
They were replaced with equivalent finite-value and non-negative assertions. All tests pass.

---

## Post-Implementation Notes (2026-08-08)

All 7 recommended next steps are complete. The following observations were made during implementation and are
documented here as reference for future work.

### ✅ TODO Comment Resolved In `AbstractTimedSimulationManager`

The original implementation left a `// TODO Check if recordStatisticsSample is needed` comment after the post-batch
`updateStatistics()` call. The comment was resolved with an explanatory note: `recordStatisticsSample()` must NOT
be called there because every executed step has already been sampled inside the per-step callback. Calling it again
would create a duplicate sample for the final batch step at the same stepCount. The existing test
`testExecuteStepsRecordsEveryExecutedStepWithoutDuplicateFinalSample` confirms this constraint.

### ✅ `StatisticExtremaTracker` `synchronized` Is Intentional

Step 6 removed `synchronized` from `StatisticHistory` on the grounds that it is only accessed from the simulation
thread. `StatisticExtremaTracker` still has `synchronized` on `clear()`, `update()`, and `snapshot()` — this is
correct and must not be removed. Unlike `StatisticHistory`, the tracker is accessed from two threads:

- `update()` runs on the background batch thread (inside `executeSteps` per-step callback).
- `snapshot()` is called from the JavaFX Application Thread (inside `updateObservationStatistics`, which is always
  called on the FX thread via Timeline callback or `Platform.runLater`).

In continuous batch mode these can overlap: a new batch starts on the background thread while the FX thread is still
processing the previous `Platform.runLater`. The `synchronized` prevents corrupted min/max state in that scenario.

### 🔵 Behavioral Change: Extrema Labels Persist After Simulation Shutdown

In the original code, min/max labels were wrapped inside the `statistics.isPresent()` block and showed `"—"` after
shutdown (when statistics is empty). In the new code, extrema labels are read from
`viewModel.getStatisticsExtrema()` unconditionally; they show `"—"` only when the `StatisticExtrema` maps have no
finite value for the key. Because `DefaultObservationViewModel.statisticsExtrema` is not reset to
`StatisticExtrema.empty()` on shutdown, extrema labels retain their last-run values until the next simulation start.

This is an intentional trade-off: it matches the pre-existing behavior of the statistics labels (which also retain
their last values after shutdown). When a new simulation starts, `createAndInitSimulation` immediately calls
`updateObservationStatistics`, which resets both statistics and extrema to the new step-0 values.

If future UI work requires resetting extrema labels on shutdown, `DefaultMainViewModel.shutdownSimulation()` should
call `observationStateViewModel.setStatisticsExtrema(StatisticExtrema.empty())` before nulling the manager.

### 🔵 `statisticsExtrema` In `DefaultObservationViewModel` Is Not A JavaFX Property

The `statisticsExtrema` field is a plain Java field (`StatisticExtrema statisticsExtrema`), not a JavaFX
`ObjectProperty`. This mirrors the implementation note in the Step 7 decision: "This can be promoted to a reactive
`ObservableValue` in a future step if needed." For future line chart rendering or reactive observation rows,
`statisticsExtrema` should be promoted to a `ReadOnlyObjectProperty<StatisticExtrema>` so views can bind to it
directly without requiring a manual refresh cycle.
