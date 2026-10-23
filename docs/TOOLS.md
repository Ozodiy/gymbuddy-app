# Tools used

## Planning and setup
| Step | Tool | Why |
|---|---|---|
| Problem statement, MVP | Markdown in `docs/planning/`, team dot voting | Simple, versioned with the code |
| Architecture, data model | Mermaid diagrams (rendered by GitHub), Markdown tables | Diagrams live in the README, no extra tool |
| Backlog and sprint board | GitHub Issues, GitHub Projects (Board) | User stories, labels, assignees, columns |
| Repository | GitHub, Git, GitHub CLI (`gh`) | Protected `main`, PR template, pull requests with review |

## Design
| Step | Tool | Why |
|---|---|---|
| Screen mockups | Python + Pillow (`docs/design/build_mockups.py`) | Reproducible PNG concept screens |

## Development
| Area | Tool |
|---|---|
| IDE | Android Studio, Gradle (Kotlin DSL, version catalog) |
| Language and UI | Kotlin, Jetpack Compose, Material 3 |
| Navigation and state | Navigation Compose, ViewModel, StateFlow, Kotlin Coroutines |

## Cloud backend (Firebase, Spark plan)
| Service | Used for |
|---|---|
| Firebase Authentication | Email/password register, login, logout, auto-login |
| Cloud Firestore | Workouts, sessions, users (Lab 2) |
| Security Rules | Users only access their own data (Lab 2) |
| Cloud Functions | Weekly stats (Lab 3) |

## Testing
| Tool | Used for |
|---|---|
| JUnit | Unit tests for `AuthValidator` (more in Lab 3) |
| Android emulator (with Google Play) | Manual testing of register, login, logout, auto-login |

## Documentation and presentation
| Tool | Used for |
|---|---|
| python-pptx / PowerPoint | Lab 1 presentation (`docs/presentation/`) |
