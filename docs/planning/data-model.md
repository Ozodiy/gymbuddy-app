# Data model (Cloud Firestore)

Lab 2 version. The main collection is `workouts` (users create, edit and delete). The reference collection is `workoutTypes` (read-only for users). The full table with the "set by" column is in the README.

| Collection | Fields (type) |
|---|---|
| `workouts` | ownerId (string) · typeId (string) · typeName (string) · title (string) · startAt (timestamp) · endAt (timestamp) · notes (string) · createdAt (timestamp) · updatedAt (timestamp) |
| `workoutTypes` | name (string) · sortOrder (number) |

`ownerId` links a workout to its owner. It comes from the signed-in session, never from user input. `createdAt` and `updatedAt` come from the server clock. Security Rules (`firestore.rules`) enforce owner-only access and validate the fields on the server.

The `users` and `sessions` collections from Lab 1 are not used yet. `sessions` returns when the partner finder is built.
