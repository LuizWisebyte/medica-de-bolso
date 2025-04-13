import React, { createContext, useState, useContext, useEffect, ReactNode, useCallback } from 'react';
import { useRouter } from 'next/router';
import { login as apiLogin, register as apiRegister } from '../services/authService';
import { getUserProfile, UserProfile } from '../services/userService';
import api from '../services/api'; // Importar a instância axios

// Usando UserProfile importado (ou um tipo local mais simples se preferir)
type User = UserProfile | null; // Ajustado para usar UserProfile

interface AuthContextType {
    isAuthenticated: boolean;
    user: User;
    token: string | null;
    loading: boolean; // Renomeado para clareza (era initialLoading)
    login: (credentials: any) => Promise<void>;
    register: (userData: any) => Promise<void>;
    logout: () => void;
    fetchUserProfile: () => Promise<void>; // Adicionar a função ao tipo
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

interface AuthProviderProps {
    children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
    const [user, setUser] = useState<User>(null);
    const [token, setToken] = useState<string | null>(null);
    const [loading, setLoading] = useState(true); // Flag para carregamento inicial
    const router = useRouter();

    // Função para buscar o perfil do usuário
    const fetchUserProfile = useCallback(async () => {
        console.log('[AuthProvider] Tentando buscar perfil do usuário...');
        try {
            const userProfile = await getUserProfile();
            setUser(userProfile);
            console.log('[AuthProvider] Perfil do usuário carregado:', userProfile);
        } catch (error) {
            console.error('[AuthProvider] Erro ao buscar perfil do usuário:', error);
            setUser(null);
            setToken(null);
            localStorage.removeItem('authToken');
            delete api.defaults.headers.common['Authorization'];
            router.push('/login');
        }
    }, [router]);

    // Efeito para verificar token e buscar usuário na inicialização
    useEffect(() => {
        const storedToken = localStorage.getItem('authToken');
        if (storedToken) {
            console.log('[AuthProvider] Token encontrado. Validando e buscando perfil...');
            setToken(storedToken);
            api.defaults.headers.common['Authorization'] = `Bearer ${storedToken}`;
            fetchUserProfile().finally(() => setLoading(false));
        } else {
            console.log('[AuthProvider] Nenhum token encontrado.');
            setLoading(false); // Nenhuma verificação necessária
        }
    }, [fetchUserProfile]);

    // Função de Login
    const login = async (credentials: any) => {
        try {
            const receivedToken = await apiLogin(credentials);
            setToken(receivedToken);
            localStorage.setItem('authToken', receivedToken);
            api.defaults.headers.common['Authorization'] = `Bearer ${receivedToken}`;
            console.log('[AuthProvider] Login bem-sucedido. Buscando perfil...');
            await fetchUserProfile(); 
            router.push('/'); 
        } catch (error) {
            console.error('[AuthProvider] Erro no login:', error);
            setUser(null);
            setToken(null);
            localStorage.removeItem('authToken');
            throw error; 
        }
    };

    // Função de Registro
    const register = async (userData: any) => {
        try {
            const receivedToken = await apiRegister(userData);
            setToken(receivedToken);
            localStorage.setItem('authToken', receivedToken);
            api.defaults.headers.common['Authorization'] = `Bearer ${receivedToken}`;
            console.log('[AuthProvider] Registro bem-sucedido. Buscando perfil...');
            await fetchUserProfile(); 
            router.push('/'); 
        } catch (error) {
            console.error('[AuthProvider] Erro no registro:', error);
            setUser(null);
            setToken(null);
            localStorage.removeItem('authToken');
            throw error; 
        }
    };

    // Função de Logout
    const logout = useCallback(() => {
        console.log('[AuthProvider] Fazendo logout.');
        setUser(null);
        setToken(null);
        localStorage.removeItem('authToken');
        delete api.defaults.headers.common['Authorization'];
        router.push('/login');
    }, [router]);

    const value = {
        isAuthenticated: !!token && !!user, // Considera autenticado se houver token E usuário
        user,
        token,
        loading, // Expõe o loading inicial
        login,
        register,
        logout,
        fetchUserProfile, // Expor a função no contexto
    };

    // Pode-se mostrar um loader global aqui enquanto loading for true
     if (loading) {
         return <div>Carregando autenticação...</div>; // Ou um spinner/skeleton
     }

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
};

// Hook customizado para usar o contexto
export const useAuth = (): AuthContextType => {
    const context = useContext(AuthContext);
    if (context === undefined) {
        throw new Error('useAuth deve ser usado dentro de um AuthProvider');
    }
    return context;
}; 