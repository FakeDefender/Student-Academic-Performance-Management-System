import http from './http'

export async function login(username: string, password: string) {
  const res = await http.post('/auth/login', { username, password })
  return res.data.data as { token: string }
}


