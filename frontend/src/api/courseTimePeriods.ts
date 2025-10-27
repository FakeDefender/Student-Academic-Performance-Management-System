import http from './http'

export interface CourseTimePeriod {
  id?: number
  courseId: number
  timePeriodId: number
  createdAt?: string
  updatedAt?: string
}

export interface CourseTimePeriodDetail {
  id?: number
  courseId: number
  timePeriodId: number
  courseCode: string
  courseName: string
  timePeriodName: string
  createdAt?: string
  updatedAt?: string
}

export async function createCourseTimePeriod(data: CourseTimePeriod) {
  const res = await http.post('/course-time-periods', data)
  return res.data
}

export async function deleteCourseTimePeriod(id: number) {
  const res = await http.delete(`/course-time-periods/${id}`)
  return res.data
}

export async function getCourseTimePeriodsByCourse(courseId: number) {
  const res = await http.get(`/course-time-periods/course/${courseId}`)
  return res.data
}

export async function getCourseTimePeriodsByTimePeriod(timePeriodId: number) {
  const res = await http.get(`/course-time-periods/time-period/${timePeriodId}`)
  return res.data
}

export async function isCourseTimePeriodActive(courseId: number) {
  const res = await http.get(`/course-time-periods/course/${courseId}/active`)
  return res.data
}
