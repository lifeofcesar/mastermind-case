export interface LoginDTO {
  login: string;
  password?: string;
}

export interface RegisterDTO {
  username: string;
  email: string;
  password?: string;
}

export interface AuthResponse {
  token?: string;
  message?: string;
  username?: string;
}