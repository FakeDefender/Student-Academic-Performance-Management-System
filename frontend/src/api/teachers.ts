import http from './http'

export async function listTeachers(params: { keyword?: string; department?: string; pageNo?: number; pageSize?: number }) {
  const res = await http.get('/teachers', { params })
  return res.data
}

export async function createTeacher(payload: any) {
  const res = await http.post('/teachers', payload)
  return res.data
}

export async function updateTeacher(id: number, payload: any) {
  const res = await http.put(`/teachers/${id}`, payload)
  return res.data
}

export async function deleteTeacher(id: number) {
  const res = await http.delete(`/teachers/${id}`)
  return res.data
}


