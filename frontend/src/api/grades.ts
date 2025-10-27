import http from './http'

export async function createGrade(payload: any) {
  const res = await http.post('/grades', payload)
  return res.data
}

export async function getStudentGrades(studentId: number, params: { semester?: string; academicYear?: string }) {
  const res = await http.get(`/grades/student/${studentId}`, { params })
  return res.data
}

export async function getStudentGradesByNumber(studentNumber: string, params: { semester?: string; academicYear?: string }) {
  // 确保参数正确编码
  const encodedParams = new URLSearchParams()
  if (params.semester) encodedParams.append('semester', params.semester)
  if (params.academicYear) encodedParams.append('academicYear', params.academicYear)
  
  const queryString = encodedParams.toString()
  const url = `/grades/student/by-number/${studentNumber}${queryString ? '?' + queryString : ''}`
  
  const res = await http.get(url)
  return res.data
}

export async function getStudentGradesByTeacher(studentNumber: string, params: { semester?: string; academicYear?: string }) {
  // 确保参数正确编码
  const encodedParams = new URLSearchParams()
  if (params.semester) encodedParams.append('semester', params.semester)
  if (params.academicYear) encodedParams.append('academicYear', params.academicYear)
  
  const queryString = encodedParams.toString()
  const url = `/grades/teacher/student/${studentNumber}${queryString ? '?' + queryString : ''}`
  
  const res = await http.get(url)
  return res.data
}

export async function importGrades(formData: FormData) {
  const res = await http.post('/files/grades/import', formData, { headers: { 'Content-Type': 'multipart/form-data' } })
  return res.data
}

export async function downloadTemplate(type: 'excel' | 'csv') {
  const res = await http.get(`/files/grades/template?type=${type}`, { 
    responseType: 'blob' 
  })
  return res.data
}


