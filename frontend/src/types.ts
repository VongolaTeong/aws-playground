export interface UserDto {
  id: string;
  username: string;
  createdAt: string;
}

export interface HorseDto {
  id: string;
  name: string;
  owner: UserDto;
  speed: number;
  stamina: number;
  power: number;
  guts: number;
  intelligence: number;
  level: number;
  experience: number;
  trainingPoints: number;
  racesWon: number;
  racesRun: number;
  totalEarnings: number;
  sire?: HorseDto;
  dam?: HorseDto;
  createdAt: string;
}

export interface RaceDto {
  id: string;
  name: string;
  scheduledAt: string;
  basePrize: number;
  minLevel: number;
  maxLevel: number;
  maxParticipants: number;
  raceType: string;
  distance: number;
  trackCondition: string;
  isCompleted: boolean;
  createdAt: string;
}

export interface RaceResultDto {
  id: string;
  race: RaceDto;
  horse: HorseDto;
  position: number;
  earnings: number;
  raceTime: number;
  performanceScore: number;
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
  sireId?: string;
  damId?: string;
}

export interface UpdateHorseRequest extends CreateHorseRequest {}

export interface CreateRaceRequest {
  name: string;
  scheduledAt: string;
  basePrize?: number;
  minLevel?: number;
  maxLevel?: number;
  maxParticipants?: number;
  raceType?: string;
  distance?: number;
  trackCondition?: string;
}

export interface UpdateRaceRequest extends CreateRaceRequest {}

export interface TrainHorseRequest {
  statType: string;
  points: number;
}

export interface BreedHorsesRequest {
  sireId: string;
  damId: string;
  foalName: string;
  ownerId: string;
}



