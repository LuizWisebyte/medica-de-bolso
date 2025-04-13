import React, { ComponentType, useEffect } from 'react';
import { useRouter } from 'next/router';
import { useAuth } from '../contexts/AuthContext';

/**
 * HOC para proteger rotas que exigem autenticação.
 * Redireciona para /login se o usuário não estiver autenticado.
 * Mostra um loader enquanto a autenticação está sendo verificada.
 */
const withAuth = <P extends object>(WrappedComponent: ComponentType<P>): React.FC<P> => {
    const AuthenticatedComponent: React.FC<P> = (props) => {
        const { isAuthenticated, loading } = useAuth();
        const router = useRouter();

        useEffect(() => {
            if (!loading && !isAuthenticated) {
                const currentPath = router.pathname;
                if (currentPath !== '/login' && 
                    currentPath !== '/registro' && 
                    currentPath !== '/recuperar-senha') {
                    console.log('[withAuth] Usuário não autenticado, redirecionando para /login');
                    router.replace('/login');
                }
            }
        }, [isAuthenticated, loading, router]);

        // Mostra um loader enquanto verifica a autenticação
        if (loading) {
            // TODO: Usar um componente de loader mais elaborado
            return <div>Carregando...</div>;
        }

        // Se autenticado, renderiza o componente original
        // Só renderiza se isAuthenticated for true após o loading
        return isAuthenticated ? <WrappedComponent {...props} /> : null; // Retorna null ou loader se ainda não autenticado
    };

    // Atribuir um nome de exibição para facilitar a depuração
    AuthenticatedComponent.displayName = `WithAuth(${WrappedComponent.displayName || WrappedComponent.name || 'Component'})`;

    return AuthenticatedComponent;
};

export default withAuth; 