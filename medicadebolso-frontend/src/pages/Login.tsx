import React, { useState } from 'react';
import Link from 'next/link';
// import { useRouter } from 'next/router'; // Não é mais necessário aqui se o AuthProvider redireciona
// import { login as apiLogin } from '../services/authService'; // Removido, useAuth faz isso
import { useAuth } from '../contexts/AuthContext'; // Importar o hook

const LoginPage: React.FC = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [submitError, setSubmitError] = useState<string | null>(null); // Renomeado para evitar conflito
    // const router = useRouter(); // Removido
    const { login, loading } = useAuth(); // Obter função login e estado loading do contexto

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setSubmitError(null);
        // setLoading(true); // O contexto gerencia o loading agora

        try {
            console.log('Componente: Tentando fazer login com:', { email, password });
            await login({ email, password }); // Chamar a função login do contexto
            console.log('Componente: Login chamado com sucesso (redirecionamento no contexto).');
            // O redirecionamento agora acontece dentro do AuthProvider
            // router.push('/'); 

        } catch (err: any) {
            console.error('Componente: Erro no login:', err);
            setSubmitError(err.message || 'Falha ao tentar fazer login. Verifique suas credenciais.');
        } finally {
           // setLoading(false); // O contexto gerencia o loading agora
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
            <div className="max-w-md w-full space-y-8">
                <div>
                    <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
                        Login
                    </h2>
                </div>
                <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
                    <div className="rounded-md shadow-sm -space-y-px">
                        <div>
                            <label htmlFor="email" className="sr-only">Email</label>
                            <input
                                id="email"
                                name="email"
                                type="email"
                                autoComplete="email"
                                required
                                className="appearance-none rounded-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-t-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm"
                                placeholder="Email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                disabled={loading}
                            />
                        </div>
                        <div>
                            <label htmlFor="password" className="sr-only">Senha</label>
                            <input
                                id="password"
                                name="password"
                                type="password"
                                autoComplete="current-password"
                                required
                                className="appearance-none rounded-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 rounded-b-md focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 focus:z-10 sm:text-sm"
                                placeholder="Senha"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                disabled={loading}
                            />
                        </div>
                    </div>

                    {submitError && (
                        <div className="text-red-500 text-sm text-center">
                            {submitError}
                        </div>
                    )}

                    <div className="flex items-center justify-between">
                        <div className="text-sm">
                            <Link href="/registro" className="font-medium text-indigo-600 hover:text-indigo-500">
                                Criar conta
                            </Link>
                        </div>
                        <div className="text-sm">
                            <Link href="/recuperar-senha" className="font-medium text-indigo-600 hover:text-indigo-500">
                                Esqueceu sua senha?
                            </Link>
                        </div>
                    </div>

                    <div>
                        <button
                            type="submit"
                            disabled={loading}
                            className={`group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white ${
                                loading 
                                    ? 'bg-indigo-400 cursor-not-allowed' 
                                    : 'bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500'
                            }`}
                        >
                            {loading ? 'Entrando...' : 'Entrar'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default LoginPage; 