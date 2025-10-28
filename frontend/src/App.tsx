import { useEffect, useState } from 'react'
import './App.css'
import { UsersApi, HorsesApi, RacesApi, GameApi } from './lib/api'
import type { UserDto, HorseDto, RaceDto, RaceResultDto } from './types'
import { CreateHorseForm, CreateRaceForm, CreateUserForm } from './components/CreateForms'

function App() {
  const [users, setUsers] = useState<UserDto[]>([])
  const [horses, setHorses] = useState<HorseDto[]>([])
  const [races, setRaces] = useState<RaceDto[]>([])
  const [selectedHorse, setSelectedHorse] = useState<HorseDto | null>(null)
  const [selectedRace, setSelectedRace] = useState<RaceDto | null>(null)
  const [raceResults, setRaceResults] = useState<RaceResultDto[]>([])
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(true)
  const [activeTab, setActiveTab] = useState<'horses' | 'races' | 'training' | 'breeding'>('horses')

  const loadData = async () => {
    try {
      setLoading(true)
      setError(null)
      const [usersData, horsesData, racesData] = await Promise.all([
        UsersApi.list(),
        HorsesApi.list(), 
        RacesApi.list()
      ])
      setUsers(usersData)
      setHorses(horsesData)
      setRaces(racesData)
    } catch (err: any) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  const simulateRace = async (raceId: string) => {
    try {
      const results = await GameApi.simulateRace(raceId)
      setRaceResults(results)
      await loadData() // Refresh data after race
    } catch (err: any) {
      setError(err.message)
    }
  }

  const trainHorse = async (horseId: string, statType: string, points: number) => {
    try {
      await GameApi.trainHorse(horseId, { statType, points })
      await loadData()
    } catch (err: any) {
      setError(err.message)
    }
  }

  const restHorse = async (horseId: string) => {
    try {
      await GameApi.restHorse(horseId)
      await loadData()
    } catch (err: any) {
      setError(err.message)
    }
  }

  useEffect(() => {
    loadData()
  }, [])

  if (loading) {
    return <div style={{ padding: 24, textAlign: 'center' }}>Loading...</div>
  }

  return (
    <div style={{ padding: 24, maxWidth: 1200, margin: '0 auto' }}>
      <h1>🏇 Umamusume Racing Game</h1>
      
      {error && (
        <div style={{ 
          color: 'white', 
          background: 'crimson', 
          padding: 12, 
          borderRadius: 6, 
          marginBottom: 16 
        }}>
          Error: {error}
        </div>
      )}

      {/* Navigation Tabs */}
      <div style={{ marginBottom: 24, borderBottom: '1px solid #ddd' }}>
        <button 
          onClick={() => setActiveTab('horses')}
          style={{
            padding: '8px 16px',
            marginRight: 8,
            border: 'none',
            background: activeTab === 'horses' ? '#007bff' : '#f8f9fa',
            color: activeTab === 'horses' ? 'white' : 'black',
            cursor: 'pointer',
            borderRadius: '4px 4px 0 0'
          }}
        >
          🐎 Horses ({horses.length})
        </button>
        <button 
          onClick={() => setActiveTab('races')}
          style={{
            padding: '8px 16px',
            marginRight: 8,
            border: 'none',
            background: activeTab === 'races' ? '#007bff' : '#f8f9fa',
            color: activeTab === 'races' ? 'white' : 'black',
            cursor: 'pointer',
            borderRadius: '4px 4px 0 0'
          }}
        >
          🏁 Races ({races.length})
        </button>
        <button 
          onClick={() => setActiveTab('training')}
          style={{
            padding: '8px 16px',
            marginRight: 8,
            border: 'none',
            background: activeTab === 'training' ? '#007bff' : '#f8f9fa',
            color: activeTab === 'training' ? 'white' : 'black',
            cursor: 'pointer',
            borderRadius: '4px 4px 0 0'
          }}
        >
          💪 Training
        </button>
        <button 
          onClick={() => setActiveTab('breeding')}
          style={{
            padding: '8px 16px',
            border: 'none',
            background: activeTab === 'breeding' ? '#007bff' : '#f8f9fa',
            color: activeTab === 'breeding' ? 'white' : 'black',
            cursor: 'pointer',
            borderRadius: '4px 4px 0 0'
          }}
        >
          👶 Breeding
        </button>
      </div>

      {/* Horses Tab */}
      {activeTab === 'horses' && (
        <div>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
            <h2>🐎 Your Horses</h2>
            <CreateHorseForm users={users} onCreated={loadData} />
          </div>
          
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: 16 }}>
            {horses.map((horse) => (
              <div key={horse.id} style={{ 
                border: '1px solid #ddd', 
                borderRadius: 8, 
                padding: 16,
                background: selectedHorse?.id === horse.id ? '#f0f8ff' : 'white'
              }}>
                <h3 style={{ margin: '0 0 8px 0' }}>{horse.name}</h3>
                <p style={{ margin: '4px 0', color: '#666' }}>Owner: {horse.owner.username}</p>
                <p style={{ margin: '4px 0', color: '#666' }}>Level: {horse.level} | Exp: {horse.experience}</p>
                
                <div style={{ margin: '8px 0' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
                    <span>Speed:</span>
                    <span>{horse.speed}/100</span>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
                    <span>Stamina:</span>
                    <span>{horse.stamina}/100</span>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
                    <span>Power:</span>
                    <span>{horse.power}/100</span>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
                    <span>Guts:</span>
                    <span>{horse.guts}/100</span>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 8 }}>
                    <span>Intelligence:</span>
                    <span>{horse.intelligence}/100</span>
                  </div>
                </div>

                <div style={{ margin: '8px 0', padding: 8, background: '#f8f9fa', borderRadius: 4 }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
                    <span>Training Points:</span>
                    <span>{horse.trainingPoints}</span>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
                    <span>Races Won:</span>
                    <span>{horse.racesWon}/{horse.racesRun}</span>
                  </div>
                  <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <span>Earnings:</span>
                    <span>¥{horse.totalEarnings.toLocaleString()}</span>
                  </div>
                </div>

                <button 
                  onClick={() => setSelectedHorse(horse)}
                  style={{
                    width: '100%',
                    padding: '8px',
                    background: selectedHorse?.id === horse.id ? '#28a745' : '#007bff',
                    color: 'white',
                    border: 'none',
                    borderRadius: 4,
                    cursor: 'pointer'
                  }}
                >
                  {selectedHorse?.id === horse.id ? 'Selected' : 'Select for Training'}
                </button>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Races Tab */}
      {activeTab === 'races' && (
        <div>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
            <h2>🏁 Races</h2>
            <CreateRaceForm onCreated={loadData} />
          </div>
          
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: 16 }}>
            {races.map((race) => (
              <div key={race.id} style={{ 
                border: '1px solid #ddd', 
                borderRadius: 8, 
                padding: 16,
                background: race.isCompleted ? '#f8f9fa' : 'white'
              }}>
                <h3 style={{ margin: '0 0 8px 0' }}>{race.name}</h3>
                <p style={{ margin: '4px 0', color: '#666' }}>
                  {race.isCompleted ? '✅ Completed' : '⏰ Scheduled'}: {new Date(race.scheduledAt).toLocaleString()}
                </p>
                <p style={{ margin: '4px 0', color: '#666' }}>Prize: ¥{race.basePrize.toLocaleString()}</p>
                <p style={{ margin: '4px 0', color: '#666' }}>Level: {race.minLevel}-{race.maxLevel}</p>
                <p style={{ margin: '4px 0', color: '#666' }}>Distance: {race.distance}m</p>
                <p style={{ margin: '4px 0', color: '#666' }}>Track: {race.trackCondition}</p>
                
                {!race.isCompleted && (
                  <button 
                    onClick={() => simulateRace(race.id)}
                    style={{
                      width: '100%',
                      padding: '8px',
                      background: '#28a745',
                      color: 'white',
                      border: 'none',
                      borderRadius: 4,
                      cursor: 'pointer',
                      marginTop: 8
                    }}
                  >
                    🏁 Start Race
                  </button>
                )}
              </div>
            ))}
          </div>

          {/* Race Results */}
          {raceResults.length > 0 && (
            <div style={{ marginTop: 32 }}>
              <h3>🏆 Latest Race Results</h3>
              <div style={{ border: '1px solid #ddd', borderRadius: 8, padding: 16 }}>
                {raceResults.map((result, index) => (
                  <div key={result.id} style={{ 
                    display: 'flex', 
                    justifyContent: 'space-between', 
                    alignItems: 'center',
                    padding: '8px 0',
                    borderBottom: index < raceResults.length - 1 ? '1px solid #eee' : 'none'
                  }}>
                    <div>
                      <span style={{ fontWeight: 'bold', marginRight: 8 }}>
                        {result.position === 1 ? '🥇' : result.position === 2 ? '🥈' : result.position === 3 ? '🥉' : '🏃'}
                        #{result.position}
                      </span>
                      <span>{result.horse.name}</span>
                    </div>
                    <div style={{ textAlign: 'right' }}>
                      <div>Time: {result.raceTime.toFixed(2)}s</div>
                      <div>Earnings: ¥{result.earnings.toLocaleString()}</div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      )}

      {/* Training Tab */}
      {activeTab === 'training' && (
        <div>
          <h2>💪 Horse Training</h2>
          {selectedHorse ? (
            <div>
              <div style={{ border: '1px solid #ddd', borderRadius: 8, padding: 16, marginBottom: 16 }}>
                <h3>Training {selectedHorse.name}</h3>
                <p>Training Points: {selectedHorse.trainingPoints}</p>
                
                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: 16, marginTop: 16 }}>
                  {[
                    { stat: 'speed', label: 'Speed', value: selectedHorse.speed },
                    { stat: 'stamina', label: 'Stamina', value: selectedHorse.stamina },
                    { stat: 'power', label: 'Power', value: selectedHorse.power },
                    { stat: 'guts', label: 'Guts', value: selectedHorse.guts },
                    { stat: 'intelligence', label: 'Intelligence', value: selectedHorse.intelligence }
                  ].map(({ stat, label, value }) => (
                    <div key={stat} style={{ border: '1px solid #ddd', borderRadius: 4, padding: 12 }}>
                      <h4 style={{ margin: '0 0 8px 0' }}>{label}</h4>
                      <div style={{ marginBottom: 8 }}>
                        <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                          <span>Current:</span>
                          <span>{value}/100</span>
                        </div>
                        <div style={{ 
                          width: '100%', 
                          height: 8, 
                          background: '#e9ecef', 
                          borderRadius: 4, 
                          marginTop: 4 
                        }}>
                          <div style={{ 
                            width: `${value}%`, 
                            height: '100%', 
                            background: value >= 80 ? '#28a745' : value >= 60 ? '#ffc107' : '#dc3545',
                            borderRadius: 4
                          }}></div>
                        </div>
                      </div>
                      <button 
                        onClick={() => trainHorse(selectedHorse.id, stat, 1)}
                        disabled={selectedHorse.trainingPoints < 1 || value >= 100}
                        style={{
                          width: '100%',
                          padding: '6px',
                          background: selectedHorse.trainingPoints >= 1 && value < 100 ? '#007bff' : '#6c757d',
                          color: 'white',
                          border: 'none',
                          borderRadius: 4,
                          cursor: selectedHorse.trainingPoints >= 1 && value < 100 ? 'pointer' : 'not-allowed'
                        }}
                      >
                        Train (+1) - 1 TP
                      </button>
                    </div>
                  ))}
                </div>
                
                <div style={{ marginTop: 16, textAlign: 'center' }}>
                  <button 
                    onClick={() => restHorse(selectedHorse.id)}
                    style={{
                      padding: '12px 24px',
                      background: '#17a2b8',
                      color: 'white',
                      border: 'none',
                      borderRadius: 4,
                      cursor: 'pointer',
                      marginRight: 8
                    }}
                  >
                    😴 Rest Horse (+2-4 TP)
                  </button>
                  <button 
                    onClick={() => setSelectedHorse(null)}
                    style={{
                      padding: '12px 24px',
                      background: '#6c757d',
                      color: 'white',
                      border: 'none',
                      borderRadius: 4,
                      cursor: 'pointer'
                    }}
                  >
                    Cancel
                  </button>
                </div>
              </div>
            </div>
          ) : (
            <p>Select a horse from the Horses tab to start training!</p>
          )}
        </div>
      )}

      {/* Breeding Tab */}
      {activeTab === 'breeding' && (
        <div>
          <h2>👶 Horse Breeding</h2>
          <p>Breeding feature coming soon! Select two horses to create a new foal.</p>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))', gap: 16 }}>
            {horses.filter(h => h.level >= 5 && h.racesRun >= 3).map((horse) => (
              <div key={horse.id} style={{ 
                border: '1px solid #ddd', 
                borderRadius: 8, 
                padding: 12,
                background: horse.level >= 5 ? '#d4edda' : '#f8f9fa'
              }}>
                <h4 style={{ margin: '0 0 8px 0' }}>{horse.name}</h4>
                <p style={{ margin: '4px 0', fontSize: '14px', color: '#666' }}>
                  Level: {horse.level} | Races: {horse.racesRun}
                </p>
                <p style={{ margin: '4px 0', fontSize: '12px', color: '#666' }}>
                  {horse.level >= 5 && horse.racesRun >= 3 ? '✅ Ready for breeding' : '❌ Not eligible'}
                </p>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}

export default App
