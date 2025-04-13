import { create, StateCreator } from 'zustand';
import { persist, PersistOptions } from 'zustand/middleware';

interface User {
  id: string;
  nome: string;
  email: string;
}

interface AuthState {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  setUser: (user: User) => void;
  setToken: (token: string) => void;
  logout: () => void;
}

// Tipo para a função set do Zustand
type AuthSetter = (partial: Partial<AuthState> | ((state: AuthState) => Partial<AuthState>)) => void;

// Define o state creator com o tipo explícito para set
const authStateCreator: StateCreator<AuthState, [], [], AuthState> = (set: AuthSetter) => ({
  user: null,
  token: null,
  isAuthenticated: false,
  setUser: (user: User) => set({ user, isAuthenticated: true }),
  setToken: (token: string) => set({ token }),
  logout: () => set({ user: null, token: null, isAuthenticated: false }),
});

// Define as opções de persistência com tipo explícito
const persistOptions: PersistOptions<AuthState> = {
  name: 'auth-storage',
};

export const useAuthStore = create<AuthState>()(
  persist(
    authStateCreator,
    persistOptions
  )
); 