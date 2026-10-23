# Data model (Cloud Firestore)

`ownerId` links a document to its owner. Security Rules (Lab 2) enforce that users only edit their own data.

| Collection | Fields (type) |
|---|---|
| `users` | uid (doc id) · email (string) · displayName (string) · createdAt (timestamp) |
| `workouts` | ownerId (string) · title (string) · type (string) · durationMin (number) · notes (string) · date (timestamp) · createdAt (timestamp) |
| `sessions` | ownerId (string) · title (string) · gym (string) · startsAt (timestamp) · maxPeople (number) · participantIds (array of string) |
