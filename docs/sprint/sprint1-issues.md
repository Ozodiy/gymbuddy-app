# Sprint 1 issues (copy into GitHub Issues; labels in brackets)

Create labels first: user-story, android, firebase, ui, bug, docs.

---
**As a user, I want to register and log in with email and password** [user-story, android, firebase]

**User story**
As a gym-goer, I want to register and log in with my email and password, so that I can access my own workouts.

**Acceptance criteria**
- [ ] Valid credentials open the home screen
- [ ] Wrong credentials show an error message
- [ ] A logged-in user skips the login screen after restarting the app
- [ ] Logout returns to login and Back does not return to home

---
**As a user, I want to see a list of my workouts** [user-story, android, ui]

**User story**
As a gym-goer, I want to see my workouts in a list, so that I can review what I trained.

**Acceptance criteria**
- [ ] List shows only my own workouts, newest first
- [ ] Empty state is shown when I have none

---
**As a user, I want to add a workout** [user-story, android, firebase]

**User story**
As a gym-goer, I want to add a workout with title, type, duration and notes, so that I keep a record.

**Acceptance criteria**
- [ ] Required fields are validated
- [ ] The workout appears in the list after saving
- [ ] The document is stored in Firestore with my ownerId

---
**As a user, I want to edit or delete a workout** [user-story, android, firebase]

**User story**
As a gym-goer, I want to edit or delete a workout, so that I can fix mistakes.

**Acceptance criteria**
- [ ] Detail screen shows all fields
- [ ] Changes are saved to Firestore
- [ ] Delete asks for confirmation

---
Backlog (not Sprint 1): weekly stats Cloud Function, training sessions / find a partner.

---
**Retro – Lab 1** [docs]: every member comments went well / did not go well / we will change; put 1–2 action items in the description.
