# Localization Glossary

One term per domain concept and locale for the values of the resource bundles `messages_en_US` and
`messages_de_DE`. The style rules for the bundles are in `.claude/rules/i18n.md` ("Wording and Style").

- The en_US column lists terms in lowercase unless they are proper names or the visible text of a UI element
  (`About`); their position in a value decides the capitalization (sentence case). The de_DE column lists terms with
  German capitalization.
- Use the listed term for the concept in labels, tooltips, descriptions, and notifications, and use it in compounds
  (`grid width`, `Gitterbreite`). German compounds with "Zelle" use `Zell-` (`Zellform`, `Zelltyp`).
- The source names the reference that establishes the term; `general` marks a common word of everyday, mathematical,
  or software vocabulary in its usual meaning, and `project` marks a term that this application chooses or defines.
- `provisional` marks a domain term of a simulation that its simulation review may complete or correct.
- Add a new concept here before using it in a bundle.

## Application

The application name `Extraterrestrial Pets Simulation` is not translated.

| en_US                   | de_DE                    | Source  | Notes                                                      |
|-------------------------|--------------------------|---------|------------------------------------------------------------|
| `start screen`          | `Startbildschirm`        | general |                                                            |
| `About`                 | `Info`                   | general | dialog title and header link text; common German menu term |
| `header`                | `Kopfzeile`              | general | used in the help text                                      |
| `language`              | `Sprache`                | general |                                                            |
| `user interface`        | `Benutzeroberfläche`     | general |                                                            |
| `command-line argument` | `Kommandozeilenargument` | general | `argument`/`Argument` if the context names the full term   |
| `log`                   | `Protokoll`              | general |                                                            |
| `log file`              | `Protokolldatei`         | general |                                                            |
| `logging`               | `Protokollierung`        | general |                                                            |
| `log level`             | `Protokollstufe`         | general |                                                            |
| `console`               | `Konsole`                | general |                                                            |
| `error`                 | `Fehler`                 | general |                                                            |
| `alert`                 | `Meldung`                | general | error dialog                                               |
| `default`               | `Standard`               | general | value of an omitted command-line argument                  |
| `settings file`         | `Einstellungsdatei`      | general | application settings                                       |

## Simulation Names

Proper names keep their capitalization in en_US. de_DE uses the established German name where one exists and otherwise
the English name; a descriptive title is translated (`Außerirdische Haustiere`).

| en_US                   | de_DE                      | Source                 | Notes             |
|-------------------------|----------------------------|------------------------|-------------------|
| `Conway's Game of Life` | `Conways Spiel des Lebens` | [GoL-en], [GoL-de]     |                   |
| `extraterrestrial pets` | `Außerirdische Haustiere`  | project                | descriptive title |
| `forest-fire model`     | `Waldbrandmodell`          | [Forest-en], [SOC-de]  |                   |
| `Langton's ant`         | `Langton-Ameise`           | [Ant-en], [Ant-de]     |                   |
| `rebounding entities`   | `Abprallende Entitäten`    | project                | provisional       |
| `simulation lab`        | `Simulationslabor`         | project                | development tool  |
| `Snake`                 | `Snake`                    | [Snake-en], [Snake-de] |                   |
| `Sugarscape`            | `Sugarscape`               | [Sugar-en]             |                   |
| `Wa-Tor`                | `Wator`                    | [WaTor-en], [WaTor-de] |                   |

## Simulation Categories

| en_US                             | de_DE                                    | Source             | Notes                 |
|-----------------------------------|------------------------------------------|--------------------|-----------------------|
| `agent- and movement-based model` | `agenten- und bewegungsbasiertes Modell` | project            | start screen category |
| `agent-based model`               | `agentenbasiertes Modell`                | [ABM-en], [ABM-de] |                       |
| `cellular automaton`              | `zellulärer Automat`                     | [CA-en], [CA-de]   | start screen category |

## Grid and Cells

| en_US                  | de_DE                 | Source           | Notes                                         |
|------------------------|-----------------------|------------------|-----------------------------------------------|
| `grid`                 | `Gitter`              | [CA-de]          |                                               |
| `cell`                 | `Zelle`               | [CA-en], [CA-de] |                                               |
| `cell shape`           | `Zellform`            | project          |                                               |
| `triangle`             | `Dreieck`             | general          | cell shape                                    |
| `square`               | `Quadrat`             | general          | cell shape                                    |
| `hexagon`              | `Sechseck`            | general          | cell shape                                    |
| `cell edge`            | `Zellkante`           | project          | side of a cell; `Kante` in neighborhoods      |
| `vertex`               | `Eckpunkt`            | general          | corner of a cell                              |
| `cell edge length`     | `Zellkantenlänge`     | project          |                                               |
| `cell display`         | `Zellanzeige`         | project          |                                               |
| `inner circle`         | `Innenkreis`          | general          | cell display                                  |
| `shape`                | `Form`                | general          | cell display                                  |
| `bordered`             | `mit Rand`            | general          | cell display                                  |
| `neighborhood`         | `Nachbarschaft`       | [CA-de]          |                                               |
| `neighbor`             | `Nachbar`             | [GoL-de]         | neighboring cell: `Nachbarzelle`              |
| `neighborhood mode`    | `Nachbarschaftsmodus` | project          | on squares: von Neumann or Moore neighborhood |
| `cell connection type` | `Zellverbindungstyp`  | project          | edge or vertex                                |
| `coordinate`           | `Koordinate`          | general          | position of a cell                            |
| `direction`            | `Richtung`            | general          | compass direction of a moving entity          |
| `pixel`                | `Pixel`               | general          |                                               |
| `grid edge`            | `Gitterrand`          | project          | boundary of the grid                          |
| `edge behavior`        | `Randverhalten`       | project          | behavior per axis                             |
| `grid edge behavior`   | `Gitterrandverhalten` | project          | behavior of the whole grid                    |
| `wrap`                 | `umlaufend`           | project          | opposite grid edges are connected             |
| `block`                | `blockiert`           | project          | grid edge stops movement                      |
| `absorb`               | `absorbierend`        | project          | grid edge removes what crosses it             |
| `selected cell`        | `ausgewählte Zelle`   | project          |                                               |
| `cell type`            | `Zelltyp`             | project          |                                               |
| `cell state`           | `Zellzustand`         | [CA-en], [CA-de] | e.g., empty, tree, or burning                 |

## Simulation Control and Observation

| en_US                   | de_DE                   | Source    | Notes                                        |
|-------------------------|-------------------------|-----------|----------------------------------------------|
| `simulation`            | `Simulation`            | general   |                                              |
| `configuration`         | `Konfiguration`         | general   | simulation parameters                        |
| `structure`             | `Struktur`              | project   | configuration section                        |
| `layout`                | `Layout`                | project   | configuration section                        |
| `initialization`        | `Initialisierung`       | project   | configuration section                        |
| `rules`                 | `Regeln`                | project   | configuration section                        |
| `percentage`            | `Prozentsatz`           | general   | initial share of a cell type                 |
| `seed`                  | `Seed`                  | [Seed-en] | initial value of the random number generator |
| `simulation run`        | `Simulationslauf`       | general   | from start to end or cancel                  |
| `control panel`         | `Bedienfeld`            | general   |                                              |
| `step`                  | `Schritt`               | project   | one update of the grid                       |
| `step count`            | `Schrittanzahl`         | project   |                                              |
| `step duration`         | `Schrittdauer`          | project   |                                              |
| `simulation mode`       | `Simulationsmodus`      | project   |                                              |
| `step by step`          | `Schritt für Schritt`   | project   | mode: steps at the step duration             |
| `jump ahead`            | `vorspringen`           | project   | mode: runs the step count, then pauses       |
| `fast forward`          | `Schnellvorlauf`        | project   | mode: runs step counts until paused          |
| `preset rule`           | `voreingestellte Regel` | project   | predefined rule offered by a simulation      |
| `end of the simulation` | `Simulationsende`       | project   |                                              |
| `editing tools`         | `Bearbeitungswerkzeuge` | general   | toolbar of a simulation                      |
| `select`                | `auswählen`             | general   | a cell or an option                          |
| `metrics`               | `Metriken`              | general   | observation section                          |
| `charts`                | `Diagramme`             | general   | observation section                          |
| `status`                | `Status`                | general   | observation section                          |

## English Terms in de_DE

de_DE keeps these English terms (`.claude/rules/i18n.md`, "English terms in de_DE"); `Seed`, `Layout`, `Respawn`, and
`Highway` are listed in their sections.

| en_US        | de_DE        | Source  | Notes                   |
|--------------|--------------|---------|-------------------------|
| `emoji`      | `Emoji`      | general | cell display            |
| `flag`       | `Flag`       | general | command-line argument   |
| `commit`     | `Commit`     | general | About dialog            |
| `JDK`        | `JDK`        | general | About dialog            |
| `repository` | `Repository` | general | GitHub link             |
| `issue`      | `Issue`      | general | GitHub link             |
| `README`     | `README`     | general | file name; About dialog |

## Conway's Game of Life

| en_US                          | de_DE                 | Source               | Notes                                                      |
|--------------------------------|-----------------------|----------------------|------------------------------------------------------------|
| `alive`                        | `lebend`              | [GoL-en], [GoL-de]   | living cell: `lebende Zelle`                               |
| `dead`                         | `tot`                 | [GoL-en], [GoL-de]   |                                                            |
| `birth`                        | `Geburt`              | [GoL-de]             | a dead cell becomes alive; also the rule checkbox label    |
| `survival`                     | `Überleben`           | [GoL-de]             | a living cell stays alive; also the rule checkbox label    |
| `transition rule`              | `Übergangsregel`      | general              | survival and birth counts                                  |
| `S/B notation`                 | `S/B-Notation`        | [LifeLike-en]        | survival counts, slash, birth counts (`23/3`)              |
| `changed cell`                 | `veränderte Zelle`    | project              | cell whose state changed in the last step                  |
| `pattern`                      | `Muster`              | [LifeWiki]           |                                                            |
| `still life`                   | `Stillleben`          | [LifeWiki], [GoL-de] |                                                            |
| `oscillator`                   | `Oszillator`          | [LifeWiki], [GoL-de] |                                                            |
| `spaceship`                    | `Raumschiff`          | [LifeWiki], [GoL-de] |                                                            |
| `methuselah`                   | `Methusalem`          | [LifeWiki]           |                                                            |
| `replicator`                   | `Replikator`          | [LifeWiki]           |                                                            |
| `block`                        | `Block`               | [LifeWiki], [GoL-de] |                                                            |
| `beehive`                      | `Bienenstock`         | [LifeWiki], [GoL-de] |                                                            |
| `loaf`                         | `Brotlaib`            | [LifeWiki], [GoL-de] |                                                            |
| `boat`                         | `Boot`                | [LifeWiki], [GoL-de] |                                                            |
| `tub`                          | `Kübel`               | [LifeWiki], [GoL-de] |                                                            |
| `blinker`                      | `Blinker`             | [LifeWiki], [GoL-de] |                                                            |
| `toad`                         | `Kröte`               | [LifeWiki], [GoL-de] |                                                            |
| `beacon`                       | `Leuchtfeuer`         | [LifeWiki]           | no German source found                                     |
| `pulsar`                       | `Pulsar`              | [LifeWiki]           |                                                            |
| `pentadecathlon`               | `Pentadecathlon`      | [LifeWiki]           | de.wikipedia calls it `Pulsator`; kept apart from `Pulsar` |
| `glider`                       | `Gleiter`             | [LifeWiki], [GoL-de] |                                                            |
| `lightweight spaceship (LWSS)` | `Segler 1 (LWSS)`     | [LifeWiki], [GoL-de] |                                                            |
| `R-pentomino`                  | `R-Pentomino`         | [LifeWiki], [GoL-de] |                                                            |
| `acorn`                        | `Eichel`              | [LifeWiki]           | no German source found                                     |
| `HighLife replicator`          | `HighLife-Replikator` | [LifeWiki]           |                                                            |

## Extraterrestrial Pets

| en_US      | de_DE       | Source  | Notes                         |
|------------|-------------|---------|-------------------------------|
| `pet`      | `Haustier`  | project |                               |
| `living`   | `lebend`    | project | not dead                      |
| `egg`      | `Ei`        | project | hatches into a pet            |
| `resource` | `Ressource` | project | plant or insect               |
| `plant`    | `Pflanze`   | project |                               |
| `insect`   | `Insekt`    | project |                               |
| `terrain`  | `Gelände`   | project | ground, rock, water, or trail |
| `ground`   | `Boden`     | project |                               |
| `rock`     | `Fels`      | project |                               |
| `water`    | `Wasser`    | project |                               |
| `trail`    | `Spur`      | project |                               |

## Forest-Fire Model

| en_US          | de_DE             | Source      | Notes                                       |
|----------------|-------------------|-------------|---------------------------------------------|
| `tree`         | `Baum`            | [Forest-en] |                                             |
| `burning tree` | `brennender Baum` | [Forest-en] |                                             |
| `empty cell`   | `leere Zelle`     | [Forest-en] |                                             |
| `tree density` | `Baumdichte`      | project     | initial share of trees                      |
| `tree growth`  | `Baumwachstum`    | [Forest-en] | probability per step                        |
| `lightning`    | `Blitz`           | [Forest-en] | lightning chance: `Blitzwahrscheinlichkeit` |

## Langton's Ant

| en_US       | de_DE           | Source             | Notes                                                    |
|-------------|-----------------|--------------------|----------------------------------------------------------|
| `ant`       | `Ameise`        | [Ant-en], [Ant-de] |                                                          |
| `turn rule` | `Drehregel`     | [Ant-en]           | letters L, R, L2, R2, N, U                               |
| `U-turn`    | `Kehrtwende`    | general            | letter U                                                 |
| `no turn`   | `keine Drehung` | general            | letter N                                                 |
| `terrain`   | `Gelände`       | project            | cell state that decides the turn; `color` in the sources |
| `unvisited` | `unbesucht`     | project            |                                                          |
| `visited`   | `besucht`       | project            | counterpart of `unvisited`                               |
| `highway`   | `Highway`       | [Ant-en]           | periodic pattern the ant builds after about 10,000 steps |

## Rebounding Entities

| en_US       | de_DE       | Source  | Notes       |
|-------------|-------------|---------|-------------|
| `rebounder` | `Abpraller` | project | provisional |
| `ground`    | `Boden`     | project |             |
| `wall`      | `Wand`      | project |             |

## Snake

| en_US               | de_DE               | Source                 | Notes                                    |
|---------------------|---------------------|------------------------|------------------------------------------|
| `snake`             | `Schlange`          | [Snake-en], [Snake-de] |                                          |
| `snake head`        | `Schlangenkopf`     | project                |                                          |
| `living`            | `lebend`            | project                | not dead; counterpart of `dead`          |
| `segment`           | `Segment`           | project                | body part of a snake                     |
| `food`              | `Futter`            | [Snake-de]             | the only food type                       |
| `growth`            | `Wachstum`          | project                | segments added after eating              |
| `points`            | `Punkte`            | [Snake-de]             |                                          |
| `bonus per segment` | `Bonus pro Segment` | project                | extra points per segment for eating food |
| `strategy`          | `Strategie`         | project                | movement strategy of a snake             |
| `death mode`        | `Todesmodus`        | project                | permadeath or respawn                    |
| `permadeath`        | `endgültiger Tod`   | project                | death mode                               |
| `respawn`           | `Respawn`           | project                | death mode; gaming term                  |
| `ground`            | `Boden`             | project                |                                          |
| `wall`              | `Wand`              | [Snake-de]             |                                          |

## Sugarscape

| en_US               | de_DE                | Source                   | Notes                                                      |
|---------------------|----------------------|--------------------------|------------------------------------------------------------|
| `agent`             | `Agent`              | [Sugar-en], [ABM-de]     |                                                            |
| `sugar`             | `Zucker`             | [Sugar-en], [Sugar-book] | the resource of a cell                                     |
| `sugar cell`        | `Zuckerzelle`        | project                  | cell with a sugar resource, also with 0 sugar              |
| `sugar peak`        | `Zuckergipfel`       | project                  |                                                            |
| `sugar peak radius` | `Zuckergipfelradius` | project                  | maximum spread radius of a sugar peak                      |
| `sugar capacity`    | `Zuckerkapazität`    | [Sugar-book]             | maximum sugar of a cell                                    |
| `metabolism`        | `Stoffwechsel`       | [Sugar-en], [Sugar-book] | energy an agent uses per step                              |
| `vision`            | `Sichtweite`         | [Sugar-en], [Sugar-book] |                                                            |
| `energy`            | `Energie`            | project                  |                                                            |
| `regeneration`      | `Regeneration`       | project                  | sugar that grows back per step; `growback` in [Sugar-book] |
| `terrain`           | `Gelände`            | project                  | ground under sugar and agents                              |
| `sugar level`       | `Zuckerstufe`        | project                  | low, medium, or high; editing tool                         |

## Wa-Tor

| en_US          | de_DE        | Source                 | Notes                        |
|----------------|--------------|------------------------|------------------------------|
| `fish`         | `Fisch`      | [WaTor-en], [WaTor-de] | count label: `fish`/`Fische` |
| `shark`        | `Hai`        | [WaTor-en], [WaTor-de] | count label: `sharks`/`Haie` |
| `predator`     | `Räuber`     | [WaTor-en], [WaTor-de] | the sharks                   |
| `prey`         | `Beute`      | [WaTor-en], [WaTor-de] | the fish                     |
| `creature`     | `Tier`       | project                | fish or shark                |
| `water`        | `Wasser`     | [WaTor-de]             | empty cell                   |
| `energy`       | `Energie`    | [WaTor-de]             |                              |
| `reproduction` | `Vermehrung` | [WaTor-de]             |                              |
| `age`          | `Alter`      | [WaTor-de]             |                              |

## Simulation Lab

| en_US         | de_DE           | Source  | Notes |
|---------------|-----------------|---------|-------|
| `highlighted` | `hervorgehoben` | project |       |
| `normal`      | `normal`        | project |       |
| `color mode`  | `Farbmodus`     | project |       |

## Sources

- [ABM-de] German Wikipedia, "Agentenbasierte Modellierung"
- [ABM-en] English Wikipedia, "Agent-based model"
- [Ant-de] German Wikipedia, "Ameise (Turingmaschine)"
- [Ant-en] English Wikipedia, "Langton's ant"
- [CA-de] German Wikipedia, "Zellulärer Automat"
- [CA-en] English Wikipedia, "Cellular automaton"
- [Forest-en] English Wikipedia, "Forest-fire model"
- [GoL-de] German Wikipedia, "Conways Spiel des Lebens"
- [GoL-en] English Wikipedia, "Conway's Game of Life"
- [LifeLike-en] English Wikipedia, "Life-like cellular automaton"
- [LifeWiki] LifeWiki, the wiki of the Conway's Game of Life community
- [Seed-en] English Wikipedia, "Random seed"
- [Snake-de] German Wikipedia, "Snake (Computerspiel)"
- [Snake-en] English Wikipedia, "Snake (video game genre)"
- [SOC-de] German Wikipedia, "Selbstorganisierte Kritikalität" (`Drossel-Schwabl-Modell für Waldbrände`)
- [Sugar-book] Joshua M. Epstein, Robert Axtell: "Growing Artificial Societies: Social Science from the Bottom Up",
  Brookings Institution Press and MIT Press, 1996; the link leads to a summary of its rules in JASSS
- [Sugar-en] English Wikipedia, "Sugarscape"
- [WaTor-de] German Wikipedia, "Wator"
- [WaTor-en] English Wikipedia, "Wa-Tor"

[ABM-de]: https://de.wikipedia.org/wiki/Agentenbasierte_Modellierung
[ABM-en]: https://en.wikipedia.org/wiki/Agent-based_model
[Ant-de]: https://de.wikipedia.org/wiki/Ameise_(Turingmaschine)
[Ant-en]: https://en.wikipedia.org/wiki/Langton%27s_ant
[CA-de]: https://de.wikipedia.org/wiki/Zellul%C3%A4rer_Automat
[CA-en]: https://en.wikipedia.org/wiki/Cellular_automaton
[Forest-en]: https://en.wikipedia.org/wiki/Forest-fire_model
[GoL-de]: https://de.wikipedia.org/wiki/Conways_Spiel_des_Lebens
[GoL-en]: https://en.wikipedia.org/wiki/Conway%27s_Game_of_Life
[LifeLike-en]: https://en.wikipedia.org/wiki/Life-like_cellular_automaton
[LifeWiki]: https://conwaylife.com/wiki/
[Seed-en]: https://en.wikipedia.org/wiki/Random_seed
[Snake-de]: https://de.wikipedia.org/wiki/Snake_(Computerspiel)
[Snake-en]: https://en.wikipedia.org/wiki/Snake_(video_game_genre)
[SOC-de]: https://de.wikipedia.org/wiki/Selbstorganisierte_Kritikalit%C3%A4t
[Sugar-book]: https://jasss.soc.surrey.ac.uk/12/1/6/appendixB/EpsteinAxtell1996.html
[Sugar-en]: https://en.wikipedia.org/wiki/Sugarscape
[WaTor-de]: https://de.wikipedia.org/wiki/Wator
[WaTor-en]: https://en.wikipedia.org/wiki/Wa-Tor
