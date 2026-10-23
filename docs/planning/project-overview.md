# Project overview: what GymBuddy means and where it is useful

## What the name means
**Gym + Buddy.** "Buddy" is gym slang for a training partner. GymBuddy combines the two things that keep people training: a record of what they did, and someone to do it with.

## What the project is
An Android app (Kotlin and Jetpack Compose) with a Firebase cloud backend:
- **Workout log:** record each workout (type, duration, notes) and review the history.
- **Partner finder:** create or join open training sessions at a gym.
- **Cloud:** data is stored in Cloud Firestore, so it follows the user to any phone. Firebase Authentication keeps each user's data private to them.

## Where it can be used
| Setting | How GymBuddy helps |
|---|---|
| Individual gym-goers | Keep a simple training history and see weekly progress |
| Friends and training groups | Plan shared sessions and see who joined |
| Newcomers to a gym | Find a partner instead of training alone |
| University sports clubs | Organise open sessions for students and staff |
| Workplace wellness programmes | Let colleagues arrange group workouts |
| Small gyms and fitness studios | Give members a free way to meet training partners |

## Why it matters
- People who track their training can see progress, which keeps them motivated.
- People who train with a partner skip fewer sessions.
- A cloud app avoids losing notes and works across devices.

## Why it fits the course
It uses the full chain the course teaches: a mobile client, cloud authentication, a cloud database with security rules, and server-side logic (a weekly-stats Cloud Function in Lab 3).

## Possible future work
Photos of workouts (Cloud Storage), push reminders, exercise templates, and gym check-ins.
