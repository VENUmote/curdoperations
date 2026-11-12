// All calls to the Spring Boot REST API live in this one file.
const BASE_URL = "/api/students";

// Error that carries the backend's field errors, e.g. { email: "Email must be valid" }
export class ApiError extends Error {
  constructor(message, status, fieldErrors = {}) {
    super(message);
    this.status = status;
    this.fieldErrors = fieldErrors;
  }
}

async function request(url, options = {}) {
  let response;
  try {
    response = await fetch(url, {
      headers: { "Content-Type": "application/json" },
      ...options,
    });
  } catch {
    throw new ApiError("Cannot reach the backend. Is Spring Boot running on port 8080?", 0);
  }

  if (response.status === 204) return null; // DELETE returns no body

  const body = await response.json().catch(() => null);
  if (!response.ok) {
    if (!body && response.status >= 500) {
      throw new ApiError("Cannot reach the backend. Is Spring Boot running on port 8080?", response.status);
    }
    throw new ApiError(body?.message || `Request failed (${response.status})`, response.status, body?.fieldErrors || {});
  }
  return body;
}

// READ (all, optional search)
export function getStudents(search = "") {
  const query = search.trim() ? `?search=${encodeURIComponent(search.trim())}` : "";
  return request(BASE_URL + query);
}

// CREATE
export function createStudent(student) {
  return request(BASE_URL, { method: "POST", body: JSON.stringify(student) });
}

// UPDATE
export function updateStudent(id, student) {
  return request(`${BASE_URL}/${id}`, { method: "PUT", body: JSON.stringify(student) });
}

// DELETE
export function deleteStudent(id) {
  return request(`${BASE_URL}/${id}`, { method: "DELETE" });
}
