# Marky

## What we're building

Marky is a local-first Markdown notes app for Android and iOS. People write notes in Markdown, read them formatted, and find any note quickly through search. It runs fully offline: no account, no server, no sync. Users can import or export a single note as a plain `.md` file.

**Specification (source of truth for behavior):** https://markdown-editor.notion.site/

Feature areas:
- **Managing notes** – create, autosave, list sorted by last edited, pin, delete with confirmation.
- **Writing in Markdown** – plain-text editor, formatting toolbar above the keyboard, edit/preview switch, interactive preview (tick checklists, open links).
- **Search** – full-text over titles and text, title matches rank first, snippets with highlighted matches.
- **Import and export** – one `.md` file at a time.
- **General** – sample notes on first launch, light/dark theme following the system, helpful empty states.

Screens: Notes list (home), Note, Search, plus a delete confirmation dialog.

Everything in the spec's "Out of scope" section must not be built (accounts, sync, note links, bulk import/export, attachments, templates, version history, tags, folders, encryption).

## Never assume

- Never invent product behavior, UI, or requirements that aren't in the spec or the designs.
- If something is unclear or missing, stop and ask before building it.
- If a small assumption is unavoidable to keep going, make it, and list it at the end of your reply under a heading **"Assumptions made"**: what you assumed, why, and what the alternative was. The user decides whether to keep it.

## Designs are the single source of truth for UI

All designs live locally in `designs/highlighter/` (open the HTML files in a browser or read the source):

- `index.html` – the design system: color tokens (light and dark), typography (Bricolage Grotesque, Figtree, JetBrains Mono), spacing, corner radii, elevation, icons, every component, Markdown node styles, writing rules. Raw token values are in `ds.css`.
- `notes-list.html`, `note.html`, `search.html` – screen designs with all their states.
- `designs/directions.html` holds old exploration drafts. It is not a reference.

Rules:
- Never make up UI. Every screen, component, state, color, size, and font comes from these designs.
- Feature code never uses stock Material or Compose components or their default styling. It only uses components from `:core:designsystem`, which are built from these designs.
- Material 3 is allowed only as the internal base of `:core:designsystem` components, restyled to match the designs exactly.
- If a screen, state, component, icon, or token you need is not in the designs, flag it and ask how to proceed. Don't improvise.

## Project structure

The repo is still the KMP template (`:shared`, `:androidApp`, `iosApp/`). The target layout follows the `kmp-module-structure` skill: `:shared` becomes `:composeApp`, and Gradle setup moves into `build-logic` convention plugins. Read that skill before adding a module.

| Module | Contains |
|---|---|
| `:composeApp` | App shell: nav host, Koin start, theme wiring, iOS `MainViewController` |
| `:androidApp` | Thin Android shell: Activity, manifest |
| `iosApp/` | Xcode entry point only |
| `:core:domain` | `Note` model, repository interfaces, `Result` / `DataError` |
| `:core:database` | Room database, entities, DAOs |
| `:core:data` | Repository implementations, mappers |
| `:core:files` | Our own file picking and saving for `.md` import/export (`expect`/`actual`: Storage Access Framework on Android, `UIDocumentPickerViewController` on iOS) |
| `:core:presentation` | `UiText`, `ObserveAsEvents`, shared UI helpers |
| `:core:designsystem` | Theme, tokens, fonts, icon object, all reusable components, Markdown rendering components |
| `:feature:notes:{domain,data,presentation}` | Notes list, note screen (edit/preview, toolbar), pin, delete, import/export |
| `:feature:search:{domain,data,presentation}` | Search screen |

`:core:files` is its own module on purpose: it is reusable logic a library could provide, but we want full control over it. Features use it only through its interface.

Rules:
- `presentation` → `domain` ← `data`. Domain depends on nothing but `:core:domain`.
- Features never depend on each other. Code needed by two or more features goes into a `core` module.
- Strings, drawables, and fonts are Compose Multiplatform resources in the owning module's `composeResources/`. No hardcoded user-facing text.

## Skills

Project skills live in `.claude/skills/`. Use the matching skill before writing the code it covers.

| When you… | Use |
|---|---|
| add a module, change Gradle, convention plugins, the version catalog, or decide where code goes | `kmp-module-structure` |
| build any composable that isn't a Root/Screen, including design-system components | `kmp-compose-components` |
| structure a screen: state ownership, side effects, recomposition, lazy lists, text fields | `kmp-compose-architecture` |
| create a ViewModel, State/Action/Event, Root/Screen composables, `UiText` | `kmp-presentation-mvi` |
| add routes or navigate between screens | `kmp-navigation` |
| write repositories, Room entities, DAOs, mappers | `kmp-data-layer` |
| define error types or use `Result` | `kmp-error-handling` |
| add Koin modules or inject dependencies | `kmp-di-koin` |
| write or review unit tests | `kmp-unit-testing` |

The skills mention things this project doesn't use (networking, preferences storage, secrets, integration/UI testing, coverage). Skip those parts.

## Tech stack

- Kotlin Multiplatform, targets Android and iOS only. Gradle 9 with the AGP KMP library plugin, version catalog (`gradle/libs.versions.toml`), `build-logic` convention plugins.
- Compose Multiplatform for all UI, Compose Multiplatform resources for strings, drawables, and fonts. Material 3 only inside `:core:designsystem`.
- JetBrains Lifecycle (`ViewModel`, `SavedStateHandle`, `collectAsStateWithLifecycle`) and JetBrains Navigation Compose with type-safe routes.
- KotlinX Serialization (navigation routes).
- Kotlin Coroutines and Flow.
- Koin for dependency injection.
- Room KMP with the bundled SQLite driver and KSP for storing and searching notes.
- JetBrains Markdown (`org.jetbrains:markdown`) for parsing. Rendering is our own Compose renderer that walks the AST and uses design-system components.
- Kermit for logging.
- Own `expect`/`actual` code in `:core:files` for file import/export. No file picker library.
- Tests: `kotlin.test`, AssertK, Turbine, `kotlinx-coroutines-test`, all in `commonTest`.

Adding any library not on this list needs the user's approval first.

## Testing scope

- Only local unit tests, written in `commonTest` following `kmp-unit-testing`, running on the Android host JVM and the iOS simulator.
- UI tests, instrumented tests, integration tests, and end-to-end tests are out of scope. Don't write them and don't set up their infrastructure.

## Definition of done

A unit of work is done only when all of these hold:

1. Every composable (components and screens) has a `@Preview` for each distinct state it can be in (for example empty, filled, pinned, light, dark). The user checks the UI through these previews. Automated visual checks against the designs are out of scope.
2. Android builds clean: `./gradlew :androidApp:assembleDebug`
3. iOS builds clean: `./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64` (`:shared:` until the module is renamed) and `xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp -destination 'generic/platform=iOS Simulator' build`
4. Unit tests pass on both platforms: `./gradlew allTests` (or per module `testAndroidHostTest` and `iosSimulatorArm64Test`). New logic (ViewModels, parsing, mapping, search ranking, and so on) has unit tests.
5. Any assumptions are listed under "Assumptions made".
6. The work is committed.

## Committing

- When a unit of work meets the definition of done, commit it right away without asking.
- One logical change per commit. Use Conventional Commits, matching the existing history: `feat(notes): …`, `fix(search): …`, `build: …`, `docs: …`, `test: …`.
- Never commit a failing build or failing tests. Don't push.
