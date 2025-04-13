import React, { useState } from 'react';
import Link from 'next/link';
import { useAuth } from '../contexts/AuthContext';

// TODO: Ajustar campos conforme a definição exata do RegistroDTO no backend
interface RegistroFormData {
    nome: string;
    email: string;
    senha: string;
    confirmarSenha: string;
    crm: string;
    crmEstado: string; // Ex: SP, RJ, MG
    // Adicionar outros campos se necessário (ex: especialidade)
}

const RegistroPage: React.FC = () => {
    const [formData, setFormData] = useState<RegistroFormData>({
        nome: '',
        email: '',
        senha: '',
        confirmarSenha: '',
        crm: '',
        crmEstado: '',
    });
    const [submitError, setSubmitError] = useState<string | null>(null);
    const { register, loading } = useAuth();

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        const { name, value } = e.target;
        setFormData(prevState => ({
            ...prevState,
            [name]: value,
        }));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setSubmitError(null);

        if (formData.senha !== formData.confirmarSenha) {
            setSubmitError('As senhas não coincidem.');
            return;
        }

        try {
            console.log('Componente: Tentando registrar com:', formData);
            const { confirmarSenha, ...registroData } = formData;
            await register(registroData);
            console.log('Componente: Registro chamado com sucesso (redirecionamento no contexto).');
        } catch (err: any) {
            console.error('Componente: Erro no registro:', err);
            setSubmitError(err.response?.data?.message || err.message || 'Falha ao tentar registrar. Verifique os dados ou tente novamente.');
        }
    };

    // TODO: Idealmente, obter a lista de estados de uma fonte confiável (API ou constante)
    const estadosBrasileiros = ['AC', 'AL', 'AP', 'AM', 'BA', 'CE', 'DF', 'ES', 'GO', 'MA', 'MT', 'MS', 'MG', 'PA', 'PB', 'PR', 'PE', 'PI', 'RJ', 'RN', 'RS', 'RO', 'RR', 'SC', 'SP', 'SE', 'TO'];

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
            <div className="max-w-md w-full space-y-8">
                <div>
                    <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
                        Criar Conta de Médico
                    </h2>
                    <p className="mt-2 text-center text-sm text-gray-600">
                        Preencha seus dados para criar sua conta
                    </p>
                </div>
                <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
                    <div className="rounded-md shadow-sm -space-y-px">
                        <div className="mb-4">
                            <label htmlFor="nome" className="block text-sm font-medium text-gray-700">Nome Completo</label>
                            <input
                                type="text"
                                id="nome"
                                name="nome"
                                value={formData.nome}
                                onChange={handleChange}
                                required
                                className="mt-1 appearance-none rounded-md relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                                disabled={loading}
                            />
                        </div>

                        <div className="mb-4">
                            <label htmlFor="email" className="block text-sm font-medium text-gray-700">Email</label>
                            <input
                                type="email"
                                id="email"
                                name="email"
                                value={formData.email}
                                onChange={handleChange}
                                required
                                className="mt-1 appearance-none rounded-md relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                                disabled={loading}
                            />
                        </div>

                        <div className="mb-4">
                            <label htmlFor="senha" className="block text-sm font-medium text-gray-700">Senha</label>
                            <input
                                type="password"
                                id="senha"
                                name="senha"
                                value={formData.senha}
                                onChange={handleChange}
                                required
                                className="mt-1 appearance-none rounded-md relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                                disabled={loading}
                            />
                        </div>

                        <div className="mb-4">
                            <label htmlFor="confirmarSenha" className="block text-sm font-medium text-gray-700">Confirmar Senha</label>
                            <input
                                type="password"
                                id="confirmarSenha"
                                name="confirmarSenha"
                                value={formData.confirmarSenha}
                                onChange={handleChange}
                                required
                                className="mt-1 appearance-none rounded-md relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                                disabled={loading}
                            />
                        </div>

                        <div className="mb-4">
                            <label htmlFor="crm" className="block text-sm font-medium text-gray-700">Número do CRM</label>
                            <input
                                type="text"
                                id="crm"
                                name="crm"
                                value={formData.crm}
                                onChange={handleChange}
                                required
                                className="mt-1 appearance-none rounded-md relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                                disabled={loading}
                            />
                        </div>

                        <div className="mb-4">
                            <label htmlFor="crmEstado" className="block text-sm font-medium text-gray-700">Estado do CRM</label>
                            <select
                                id="crmEstado"
                                name="crmEstado"
                                value={formData.crmEstado}
                                onChange={handleChange}
                                required
                                className="mt-1 appearance-none rounded-md relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-gray-900 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                                disabled={loading}
                            >
                                <option value="">Selecione...</option>
                                {estadosBrasileiros.map((estado: string) => (
                                    <option key={estado} value={estado}>{estado}</option>
                                ))}
                            </select>
                        </div>
                    </div>

                    {submitError && (
                        <div className="text-red-500 text-sm text-center">
                            {submitError}
                        </div>
                    )}

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
                            {loading ? 'Registrando...' : 'Criar Conta'}
                        </button>
                    </div>

                    <div className="text-sm text-center">
                        <Link href="/login" className="font-medium text-indigo-600 hover:text-indigo-500">
                            Já tem uma conta? Faça Login
                        </Link>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default RegistroPage; 