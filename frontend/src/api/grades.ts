import http from './http'

export async function createGrade(payload: any) {
  const res = await http.post('/grades', payload)
  return res.data
}

export async function getStudentGrades(studentId: number, params: { semester?: string; academicYear?: string }) {
  const res = await http.get(`/grades/student/${studentId}`, { params })
  return res.data
}

export async function importGrades(formData: FormData) {
  const res = await http.post('/files/grades/import', formData, { headers: { 'Content-Type': 'multipart/form-data' } })
  return res.data
}


