// Runs the Firestore Security Rules against the local emulator.
// Start with:  firebase emulators:exec --only firestore --project gymbuddy-rules-test "npm --prefix rules-tests test"
import { readFileSync } from 'node:fs';
import { after, before, beforeEach, describe, it } from 'node:test';
import { assertFails, assertSucceeds, initializeTestEnvironment } from '@firebase/rules-unit-testing';
import {
  Timestamp, addDoc, collection, deleteDoc, doc, getDoc, getDocs, query, serverTimestamp, setDoc, updateDoc, where,
} from 'firebase/firestore';

const rules = readFileSync(new URL('../firestore.rules', import.meta.url), 'utf8');
const START = Timestamp.fromDate(new Date('2026-11-17T18:00:00Z'));
const END = Timestamp.fromDate(new Date('2026-11-17T19:00:00Z'));

// A stored workout (written with rules disabled) used by the read / update / delete scenarios.
const stored = {
  ownerId: 'alice', title: 'Push day', typeId: 'strength', typeName: 'Strength',
  startAt: START, endAt: END, notes: 'Bench 4x8', createdAt: START, updatedAt: START,
};

// What the app sends when it creates a workout.
const newWorkout = (overrides = {}) => ({
  ownerId: 'alice', title: 'Leg day', typeId: 'strength', typeName: 'Strength',
  startAt: START, endAt: END, notes: '', createdAt: serverTimestamp(), updatedAt: serverTimestamp(),
  ...overrides,
});

let env;
const asAlice = () => env.authenticatedContext('alice').firestore();
const asBob = () => env.authenticatedContext('bob').firestore();
const anonymous = () => env.unauthenticatedContext().firestore();

before(async () => {
  env = await initializeTestEnvironment({ projectId: 'gymbuddy-rules-test', firestore: { rules } });
});
after(async () => { await env.cleanup(); });
beforeEach(async () => {
  await env.clearFirestore();
  await env.withSecurityRulesDisabled(async (ctx) => {
    const db = ctx.firestore();
    await setDoc(doc(db, 'workouts', 'w1'), stored);
    await setDoc(doc(db, 'workoutTypes', 'strength'), { name: 'Strength', sortOrder: 1 });
  });
});

describe('required scenarios', () => {
  it('1  Read own record: allowed', async () => {
    await assertSucceeds(getDoc(doc(asAlice(), 'workouts', 'w1')));
  });
  it('2  Read the same record as a different user: denied', async () => {
    await assertFails(getDoc(doc(asBob(), 'workouts', 'w1')));
  });
  it('3  Read without being signed in: denied', async () => {
    await assertFails(getDoc(doc(anonymous(), 'workouts', 'w1')));
  });
  it('4  Create a record whose owner id is another user: denied', async () => {
    await assertFails(addDoc(collection(asAlice(), 'workouts'), newWorkout({ ownerId: 'bob' })));
  });
  it('5  Create a record breaking the logical rule (end before start): denied', async () => {
    await assertFails(addDoc(collection(asAlice(), 'workouts'), newWorkout({ startAt: END, endAt: START })));
  });
  it('6  Update own record, changing its owner id: denied', async () => {
    await assertFails(updateDoc(doc(asAlice(), 'workouts', 'w1'), { ownerId: 'bob', updatedAt: serverTimestamp() }));
  });
  it('7  Create a record in the reference collection: denied', async () => {
    await assertFails(setDoc(doc(asAlice(), 'workoutTypes', 'new'), { name: 'Yoga', sortOrder: 9 }));
  });
  it('8  Read a reference record as a signed-in user: allowed', async () => {
    await assertSucceeds(getDoc(doc(asAlice(), 'workoutTypes', 'strength')));
  });
});

describe('extra checks', () => {
  it('a valid create by the owner is allowed', async () => {
    await assertSucceeds(addDoc(collection(asAlice(), 'workouts'), newWorkout()));
  });
  it('a create with a device-clock createdAt is denied', async () => {
    await assertFails(addDoc(collection(asAlice(), 'workouts'), newWorkout({ createdAt: Timestamp.now() })));
  });
  it('a create with a field that is not allowed is denied', async () => {
    await assertFails(addDoc(collection(asAlice(), 'workouts'), newWorkout({ admin: true })));
  });
  it('a create with a title longer than 60 characters is denied', async () => {
    await assertFails(addDoc(collection(asAlice(), 'workouts'), newWorkout({ title: 'x'.repeat(61) })));
  });
  it('a create with notes longer than 200 characters is denied', async () => {
    await assertFails(addDoc(collection(asAlice(), 'workouts'), newWorkout({ notes: 'n'.repeat(201) })));
  });
  it('a create with a number instead of a text title is denied', async () => {
    await assertFails(addDoc(collection(asAlice(), 'workouts'), newWorkout({ title: 42 })));
  });
  it('the owner can update the editable fields and updatedAt', async () => {
    await assertSucceeds(updateDoc(doc(asAlice(), 'workouts', 'w1'), { title: 'Push day 2', updatedAt: serverTimestamp() }));
  });
  it('an update that changes createdAt is denied', async () => {
    await assertFails(updateDoc(doc(asAlice(), 'workouts', 'w1'), { createdAt: END, updatedAt: serverTimestamp() }));
  });
  it('an update that makes end before start is denied', async () => {
    await assertFails(updateDoc(doc(asAlice(), 'workouts', 'w1'), { endAt: START, startAt: END, updatedAt: serverTimestamp() }));
  });
  it('another user cannot update the record', async () => {
    await assertFails(updateDoc(doc(asBob(), 'workouts', 'w1'), { title: 'Hacked', updatedAt: serverTimestamp() }));
  });
  it('another user cannot delete the record', async () => {
    await assertFails(deleteDoc(doc(asBob(), 'workouts', 'w1')));
  });
  it('the owner can delete the record', async () => {
    await assertSucceeds(deleteDoc(doc(asAlice(), 'workouts', 'w1')));
  });
  it('the live query filtered by owner is allowed', async () => {
    await assertSucceeds(getDocs(query(collection(asAlice(), 'workouts'), where('ownerId', '==', 'alice'))));
  });
  it('a query without the owner filter is denied', async () => {
    await assertFails(getDocs(collection(asAlice(), 'workouts')));
  });
  it('a query for another user\'s records is denied', async () => {
    await assertFails(getDocs(query(collection(asBob(), 'workouts'), where('ownerId', '==', 'alice'))));
  });
  it('the reference collection is not readable without signing in', async () => {
    await assertFails(getDoc(doc(anonymous(), 'workoutTypes', 'strength')));
  });
  it('the reference collection cannot be changed or deleted from the app', async () => {
    await assertFails(updateDoc(doc(asAlice(), 'workoutTypes', 'strength'), { name: 'Hacked' }));
    await assertFails(deleteDoc(doc(asAlice(), 'workoutTypes', 'strength')));
  });
  it('any other collection is closed', async () => {
    await assertFails(getDoc(doc(asAlice(), 'secrets', 'x')));
  });
});
