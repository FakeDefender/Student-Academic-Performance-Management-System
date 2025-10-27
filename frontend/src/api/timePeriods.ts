import http from './http'

export async function listActiveTimePeriods() {
  const res = await http.get('/time-periods/active')
  return res.data
}

export async function createTimePeriod(payload: any) {
  const res = await http.post('/time-periods', payload)
  return res.data
}

export async function updateTimePeriod(id: number, payload: any) {
  const res = await http.put(`/time-periods/${id}`, payload)
  return res.data
}

export async function deleteTimePeriod(id: number) {
  const res = await http.delete(`/time-periods/${id}`)
  return res.data
}

export async function getTimePeriods() {
  const res = await http.get('/time-periods')
  return res.data
}


