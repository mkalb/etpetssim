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
protected AbstractTimedSimulationManager(CON config,
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

1. Should tracking APIs live only on `AbstractTimedSimulationManager`, or should `SimulationManager` expose default empty
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

The existing post-batch `updateStatistics()` call may create a duplicate sample for the final step if sample recording is
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
