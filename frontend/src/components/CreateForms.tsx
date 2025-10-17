import { useState } from "react";
import { UsersApi, HorsesApi, RacesApi } from "../lib/api";
import type { CreateUserRequest, CreateHorseRequest, CreateRaceRequest, UserDto } from "../types";

export function CreateUserForm({ onCreated }: { onCreated: () => void }) {
  const [username, setUsername] = useState("");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  return (
    <form
      onSubmit={async (e) => {
        e.preventDefault();
        setSubmitting(true);
        setError(null);
        try {
          const body: CreateUserRequest = { username };
          await UsersApi.create(body);
          setUsername("");
          onCreated();
        } catch (err: any) {
          setError(err.message);
        } finally {
          setSubmitting(false);
        }
      }}
      style={{ display: "flex", gap: 8, alignItems: "center" }}
    >
      <input placeholder="new username" value={username} onChange={(e) => setUsername(e.target.value)} />
      <button disabled={!username || submitting} type="submit">Create</button>
      {error && <span style={{ color: "crimson" }}>{error}</span>}
    </form>
  );
}

export function CreateHorseForm({ users, onCreated }: { users: UserDto[]; onCreated: () => void }) {
  const [name, setName] = useState("");
  const [ownerId, setOwnerId] = useState<string>("");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  return (
    <form
      onSubmit={async (e) => {
        e.preventDefault();
        setSubmitting(true);
        setError(null);
        try {
          const body: CreateHorseRequest = { name, ownerId };
          await HorsesApi.create(body);
          setName("");
          setOwnerId("");
          onCreated();
        } catch (err: any) {
          setError(err.message);
        } finally {
          setSubmitting(false);
        }
      }}
      style={{ display: "flex", gap: 8, alignItems: "center" }}
    >
      <input placeholder="horse name" value={name} onChange={(e) => setName(e.target.value)} />
      <select value={ownerId} onChange={(e) => setOwnerId(e.target.value)}>
        <option value="">select owner</option>
        {users.map((u) => (
          <option key={u.id} value={u.id}>{u.username}</option>
        ))}
      </select>
      <button disabled={!name || !ownerId || submitting} type="submit">Create</button>
      {error && <span style={{ color: "crimson" }}>{error}</span>}
    </form>
  );
}

export function CreateRaceForm({ onCreated }: { onCreated: () => void }) {
  const [name, setName] = useState("");
  const [scheduledAt, setScheduledAt] = useState<string>("");
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  return (
    <form
      onSubmit={async (e) => {
        e.preventDefault();
        setSubmitting(true);
        setError(null);
        try {
          const body: CreateRaceRequest = { name, scheduledAt: new Date(scheduledAt).toISOString() };
          await RacesApi.create(body);
          setName("");
          setScheduledAt("");
          onCreated();
        } catch (err: any) {
          setError(err.message);
        } finally {
          setSubmitting(false);
        }
      }}
      style={{ display: "flex", gap: 8, alignItems: "center" }}
    >
      <input placeholder="race name" value={name} onChange={(e) => setName(e.target.value)} />
      <input type="datetime-local" value={scheduledAt} onChange={(e) => setScheduledAt(e.target.value)} />
      <button disabled={!name || !scheduledAt || submitting} type="submit">Create</button>
      {error && <span style={{ color: "crimson" }}>{error}</span>}
    </form>
  );
}



