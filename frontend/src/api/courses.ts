import http from './http'

export async function listCourses(params: { keyword?: string; department?: string; isActive?: boolean; pageNo?: number; pageSize?: number }) {
  const res = await http.get('/courses', { params })
  return res.data
}

export async function createCourse(payload: any) {
  const res = await http.post('/courses', payload)
  return res.data
}

export async function updateCourse(id: number, payload: any) {
  const res = await http.put(`/courses/${id}`, payload)
  return res.data
}

export async function deleteCourse(id: number) {
  const res = await http.delete(`/courses/${id}`)
  return res.data
}

export async function getCourses() {
  const res = await http.get('/courses')
  return res.data
}

export async function getMyCourses() {
  const res = await http.get('/courses/my-courses')
  return res.data
}

export async function getCourseStudents(courseId: number) {
  const res = await http.get(`/courses/${courseId}/students`)
  return res.data
}


