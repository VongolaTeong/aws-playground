import type {
  CreateHorseRequest,
  CreateRaceRequest,
  CreateUserRequest,
  HorseDto,
  RaceDto,
  RaceResultDto,
  TrainHorseRequest,
  BreedHorsesRequest,
  UpdateHorseRequest,
  UpdateRaceRequest,
  UpdateUserRequest,
  UserDto,
} from "../types";

const BASE = "/api/v1";

async function request<T>(input: RequestInfo, init?: RequestInit): Promise<T> {
  const res = await fetch(input, {
    headers: { "Content-Type": "application/json" },
    ...init,
  });
  if (!res.ok) {
    const text = await res.text();
    throw new Error(`HTTP ${res.status}: ${text || res.statusText}`);
  }
  return (await res.json()) as T;
}

export const UsersApi = {
  list(): Promise<UserDto[]> {
    return request<UserDto[]>(`${BASE}/users`);
  },
  get(id: string): Promise<UserDto> {
    return request<UserDto>(`${BASE}/users/${id}`);
  },
  create(body: CreateUserRequest): Promise<UserDto> {
    return request<UserDto>(`${BASE}/users`, {
      method: "POST",
      body: JSON.stringify(body),
    });
  },
  update(id: string, body: UpdateUserRequest): Promise<UserDto> {
    return request<UserDto>(`${BASE}/users/${id}`, {
      method: "PUT",
      body: JSON.stringify(body),
    });
  },
  delete(id: string): Promise<void> {
    return request<void>(`${BASE}/users/${id}`, { method: "DELETE" });
  },
};

export const HorsesApi = {
  list(): Promise<HorseDto[]> {
    return request<HorseDto[]>(`${BASE}/horses`);
  },
  get(id: string): Promise<HorseDto> {
    return request<HorseDto>(`${BASE}/horses/${id}`);
  },
  byOwner(ownerId: string): Promise<HorseDto[]> {
    return request<HorseDto[]>(`${BASE}/horses/owner/${ownerId}`);
  },
  create(body: CreateHorseRequest): Promise<HorseDto> {
    return request<HorseDto>(`${BASE}/horses`, {
      method: "POST",
      body: JSON.stringify(body),
    });
  },
  update(id: string, body: UpdateHorseRequest): Promise<HorseDto> {
    return request<HorseDto>(`${BASE}/horses/${id}`, {
      method: "PUT",
      body: JSON.stringify(body),
    });
  },
  delete(id: string): Promise<void> {
    return request<void>(`${BASE}/horses/${id}`, { method: "DELETE" });
  },
};

export const RacesApi = {
  list(): Promise<RaceDto[]> {
    return request<RaceDto[]>(`${BASE}/races`);
  },
  get(id: string): Promise<RaceDto> {
    return request<RaceDto>(`${BASE}/races/${id}`);
  },
  upcoming(): Promise<RaceDto[]> {
    return request<RaceDto[]>(`${BASE}/races/upcoming`);
  },
  create(body: CreateRaceRequest): Promise<RaceDto> {
    return request<RaceDto>(`${BASE}/races`, {
      method: "POST",
      body: JSON.stringify(body),
    });
  },
  update(id: string, body: UpdateRaceRequest): Promise<RaceDto> {
    return request<RaceDto>(`${BASE}/races/${id}`, {
      method: "PUT",
      body: JSON.stringify(body),
    });
  },
  delete(id: string): Promise<void> {
    return request<void>(`${BASE}/races/${id}`, { method: "DELETE" });
  },
};

export const GameApi = {
  simulateRace(raceId: string): Promise<RaceResultDto[]> {
    return request<RaceResultDto[]>(`${BASE}/game/races/${raceId}/simulate`, {
      method: "POST",
    });
  },
  trainHorse(horseId: string, body: TrainHorseRequest): Promise<any> {
    return request<any>(`${BASE}/game/horses/${horseId}/train`, {
      method: "POST",
      body: JSON.stringify(body),
    });
  },
  restHorse(horseId: string): Promise<any> {
    return request<any>(`${BASE}/game/horses/${horseId}/rest`, {
      method: "POST",
    });
  },
  getTrainingRecommendations(horseId: string): Promise<any> {
    return request<any>(`${BASE}/game/horses/${horseId}/training-recommendations`);
  },
  breedHorses(body: BreedHorsesRequest): Promise<HorseDto> {
    return request<HorseDto>(`${BASE}/game/horses/breed`, {
      method: "POST",
      body: JSON.stringify(body),
    });
  },
  getRaceStatistics(raceId: string): Promise<any> {
    return request<any>(`${BASE}/game/races/${raceId}/statistics`);
  },
  getHorseStats(horseId: string): Promise<any> {
    return request<any>(`${BASE}/game/horses/${horseId}/stats`);
  },
};



