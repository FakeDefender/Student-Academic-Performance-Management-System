import http from './http'

export async function listStudents(params: { keyword?: string; status?: string; pageNo?: number; pageSize?: number }) {
  const res = await http.get('/students', { params })
  return res.data
}

export async function createStudent(payload: any) {
  const res = await http.post('/students', payload)
  return res.data
}

export async function updateStudent(id: number, payload: any) {
  const res = await http.put(`/students/${id}`, payload)
  return res.data
}

export async function deleteStudent(id: number) {
  const res = await http.delete(`/students/${id}`)
  return res.data
}

export async function getStudentProfile() {
  const res = await http.get('/students/profile')
  return res.data
}

export async function updateStudentProfile(payload: any) {
  const res = await http.put('/students/profile', payload)
  return res.data
}


