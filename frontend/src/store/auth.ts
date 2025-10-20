export type UserInfo = { role?: 'ADMIN' | 'TEACHER' | 'STUDENT'; username?: string }

function b64urlToB64(input: string) {
  // convert base64url to base64 and pad
  let output = input.replace(/-/g, '+').replace(/_/g, '/')
  const pad = output.length % 4
  if (pad === 2) output += '=='
  else if (pad === 3) output += '='
  else if (pad !== 0) output += '===' // fallback
  return output
}

function parseJwt(token?: string): UserInfo {
  if (!token) return {}
  try {
    const payload = token.split('.')[1]
    const json = JSON.parse(decodeURIComponent(escape(window.atob(b64urlToB64(payload)))))
    return { role: json.role as any, username: json.username }
  } catch {
    return {}
  }
}

export function getToken(): string | null {
  return localStorage.getItem('token')
}

export function setToken(token: string) {
  localStorage.setItem('token', token)
  window.dispatchEvent(new Event('auth-changed'))
}

export function clearToken() {
  localStorage.removeItem('token')
  window.dispatchEvent(new Event('auth-changed'))
}

export function getUser(): UserInfo {
  return parseJwt(getToken() || undefined)
}

export function hasRole(roles: string[]): boolean {
  const role = getUser().role
  if (!role) return false
  return roles.includes(role)
}

export function isAuthed(): boolean {
  return !!getToken() && !!getUser().role
}


