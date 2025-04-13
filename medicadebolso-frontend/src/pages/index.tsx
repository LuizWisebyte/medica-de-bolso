import React from 'react';
import withAuth from '../hocs/withAuth';
import { useAuth } from '../contexts/AuthContext';

const HomePage: React.FC = () => {
    const { user, logout } = useAuth();

    return (
        <div>
            <h1>Página Inicial Segura</h1>
            {user ? (
                <p>Bem-vindo, {user.nome || user.email}!</p>
            ) : (
                <p>Carregando dados do usuário...</p>
            )}
            
            <p>Este é um conteúdo visível apenas para usuários autenticados.</p>
            
            <button 
                onClick={logout} 
                style={{ marginTop: '20px', padding: '10px', cursor: 'pointer' }}
            >
                Sair
            </button>
        </div>
    );
};

export default withAuth(HomePage); 