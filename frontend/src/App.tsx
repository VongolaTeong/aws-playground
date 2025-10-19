import { useEffect, useState } from 'react'
import './App.css'
import { UsersApi, HorsesApi, RacesApi } from './lib/api'
import type { UserDto, HorseDto, RaceDto } from './types'
import { CreateHorseForm, CreateRaceForm, CreateUserForm } from './components/CreateForms'

function App() {
  const [users, setUsers] = useState<UserDto[]>([])
  const [horses, setHorses] = useState<HorseDto[]>([])
  const [races, setRaces] = useState<RaceDto[]>([])
  const [error, setError] = useState<string | null>(null)
  const [loading, setLoading] = useState(true)

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

  useEffect(() => {
    loadData()
  }, [])

  if (loading) {
    return <div style={{ padding: 24, textAlign: 'center' }}>Loading...</div>
  }

  return (
    <div style={{ padding: 24, maxWidth: 960, margin: '0 auto' }}>
      <h1>Umamusume Admin</h1>
      
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

      <section style={{ marginBottom: 32 }}>
        <h2>Users ({users.length})</h2>
        <CreateUserForm onCreated={loadData} />
        <ul style={{ listStyle: 'none', padding: 0 }}>
          {users.map((user) => (
            <li key={user.id} style={{ 
              padding: 8, 
              border: '1px solid #ddd', 
              margin: '4px 0',
              borderRadius: 4 
            }}>
              <strong>{user.username}</strong> — Created: {new Date(user.createdAt).toLocaleString()}
            </li>
          ))}
        </ul>
      </section>

      <section style={{ marginBottom: 32 }}>
        <h2>Horses ({horses.length})</h2>
        <CreateHorseForm users={users} onCreated={loadData} />
        <ul style={{ listStyle: 'none', padding: 0 }}>
          {horses.map((horse) => (
            <li key={horse.id} style={{ 
              padding: 8, 
              border: '1px solid #ddd', 
              margin: '4px 0',
              borderRadius: 4 
            }}>
              <strong>{horse.name}</strong> — Owner: {horse.owner.username}
            </li>
          ))}
        </ul>
      </section>

      <section>
        <h2>Races ({races.length})</h2>
        <CreateRaceForm onCreated={loadData} />
        <ul style={{ listStyle: 'none', padding: 0 }}>
          {races.map((race) => (
            <li key={race.id} style={{ 
              padding: 8, 
              border: '1px solid #ddd', 
              margin: '4px 0',
              borderRadius: 4 
            }}>
              <strong>{race.name}</strong> — Scheduled: {new Date(race.scheduledAt).toLocaleString()}
            </li>
          ))}
        </ul>
      </section>
    </div>
  )
}

export default App
