export interface UserDto {
  id: string;
  username: string;
  createdAt: string;
}

export interface HorseDto {
  id: string;
  name: string;
  owner: UserDto; // backend sends nested owner object
  createdAt: string;
}

export interface RaceDto {
  id: string;
  name: string;
  scheduledAt: string;
  createdAt: string;
}

export interface CreateUserRequest {
  username: string;
}

export interface UpdateUserRequest {
  username: string;
}

export interface CreateHorseRequest {
  name: string;
  ownerId: string;
}

export interface UpdateHorseRequest extends CreateHorseRequest {}

export interface CreateRaceRequest {
  name: string;
  scheduledAt: string; // ISO string
}

export interface UpdateRaceRequest extends CreateRaceRequest {}



