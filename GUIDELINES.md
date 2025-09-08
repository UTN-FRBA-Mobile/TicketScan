# TicketScan Code Guidelines

These guidelines define how we structure, write, and review code in TicketScan. They are intended to keep the codebase easy to understand, test, and change.

## Core Principles

- Single Responsibility & Modularity
  - Each class, file, and function has one clear responsibility and a single reason to change.
  - Prefer many small, focused units over large, multipurpose ones.
  - Extract helpers and utilities early when behavior repeats.

- Separation of Concerns (Domain vs View)
  - Domain logic lives under `domain/` and is UI-agnostic. It contains models, use cases/interactors, and interfaces for repositories and services.
  - View/UI logic lives under `ui/` and deals with rendering, user interaction, and state presentation.
  - Data and infrastructure concerns (e.g., network, database, platform APIs) live outside of `domain/`

- No In-Place Custom Styling
  - Do not embed custom styles inline inside screens or ad-hoc views.
  - Any custom styling must be encapsulated as a reusable UI component under `ui/components/`.
  - Shared theme tokens (colors, typography, shapes, spacing) are defined centrally (e.g., `ui/theme/`).

- Favor Composition Over Inheritance
  - Build behavior by composing small, well-defined pieces rather than subclassing.
  - Use interfaces and dependency injection to assemble features.

## Recommended Project Structure

Use packages/directories that reflect responsibility boundaries rather than technical details. Example layout (adjust for your package name):

```
app/src/main/java/com/example/ticketscan/
  domain/
    model/
  ui/
    components/       # reusable visual building blocks (no business logic)
    screens/          # feature screens and their state holders
    theme/            # colors, typography, dimensions, shapes
  utils/               # cross-cutting utilities (e.g. Result, Either, logging)
```

Notes:
- `domain/` contains pure business logic with no Android/UI dependencies.
- `ui/` may include Jetpack Compose components, views, and screen state management.

## UI & Styling Rules

- Centralize theme and design tokens (colors, spacing, typography) under `ui/theme/`.
- **Do not hardcode or directly use `MaterialTheme` or any specific theme implementation in reusable components or screens.**
    - Instead, reference theme values via an abstraction (e.g., `TicketScanTheme` or `LocalTicketScanColors`) so the app can be rethemed easily.
    - If your component needs a color, shape, or typography, always get it from the theme abstraction, not from `MaterialTheme` directly.
    - This ensures the app can switch themes (e.g., light/dark, custom branding) without refactoring all usages.
- When a screen needs a custom look or repeated pattern, extract a dedicated component into `ui/components/` and reuse it.
- Components should accept parameters for configuration (e.g., padding, colors) rather than hard-coding values.
- Avoid duplicating style literals across screens; prefer reusing tokens and components.

## Domain Rules

- Domain models are immutable and serialization-agnostic.
- Use cases encapsulate application-specific business actions; they expose simple, testable APIs.
- Domain code must not import UI or Android-specific classes.

## Data & Integration Rules

- Keep mapping between network/database models and domain models in the data layer.
- Hide implementation details behind domain-defined interfaces.
- Handle failures explicitly (e.g., Result/Either types); do not throw unchecked exceptions through boundaries.

## Composition, Dependencies, and DI

- Compose features by wiring interfaces and implementations; avoid deep inheritance trees.
- Keep side effects at the edges (data/integration), keep `domain/` pure.

## State Management

- UI screens own UI state; expose events and state immutably.
- Business state transformations belong in `domain/` (use cases) and are surfaced to UI as results.

## Testing Strategy

- Unit-test domain use cases and mappers (fast, isolated, no Android deps).
- Unit-test data mappers/adapters with fake I/O where possible.
- UI tests focus on component/screen behavior and accessibility.
- Favor constructor-injected dependencies to enable testing.

## Coding Conventions

- Naming: favor clarity (e.g., `ScanTicketUseCase`, `TicketRepository`).
- Functions should be small and expressive; avoid boolean parameter flags—prefer data classes or sealed types.
- Keep files short and cohesive; split when responsibilities multiply.
- Add KDoc to public types and nontrivial functions.
- **After every code change, run the type checker (e.g., `./gradlew build` or `./gradlew compileDebugKotlin`) to ensure type safety and prevent regressions.**

## Pull Requests & Reviews

- Include a brief description, screenshots for UI changes, and a checklist of impacts.
- Ensure new code adheres to the layout above and these principles.
- Add/maintain tests for new behaviors.
- No inline custom styles; extract `ui/components/*` as needed.

## Definition of Done (DoD)

- Code follows SRP and is modular.
- Domain and view concerns are clearly separated.
- No in-place custom styling; reusable components created as needed.
- Composition is preferred over inheritance.
- Tests added/updated; CI passes.
- Documentation (README or component KDoc) updated when applicable.
