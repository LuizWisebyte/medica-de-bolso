import React, { useState, useEffect, useCallback } from 'react';
import { useRouter } from 'next/router'; // Importar useRouter
import withAuth from '../hocs/withAuth';
import { useAuth } from '../contexts/AuthContext';
import { 
    getMeusAtendimentos, 
    iniciarAtendimento, 
    Atendimento 
} from '../services/atendimentoService'; 
// TODO: Importar serviço para iniciar/cancelar/etc atendimento

const AtendimentosPage: React.FC = () => {
    const { user } = useAuth(); // Usado para garantir que user carregou
    const [atendimentos, setAtendimentos] = useState<Atendimento[]>([]);
    const [loadingAtendimentos, setLoadingAtendimentos] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [submittingId, setSubmittingId] = useState<number | null>(null); // Para feedback no botão
    const router = useRouter(); // Instanciar router

    // Mover a função de busca para useCallback para evitar recriação
    const fetchAtendimentos = useCallback(async () => {
        if (!user) return; // Espera usuário carregar
        
        setLoadingAtendimentos(true);
        setError(null);
        try {
            const data = await getMeusAtendimentos();
            setAtendimentos(data);
        } catch (err: any) {
            setError(err.message || 'Erro ao buscar atendimentos.');
        } finally {
            setLoadingAtendimentos(false);
        }
    }, [user]);

    useEffect(() => {
        fetchAtendimentos();
    }, [fetchAtendimentos]); // Usar a função memoizada como dependência

    // Função para lidar com a ação de iniciar atendimento
    const handleIniciarAtendimento = async (id: number) => {
        setSubmittingId(id); // Marcar este botão como carregando
        setError(null); // Limpar erros anteriores
        try {
            const atendimentoAtualizado = await iniciarAtendimento(id);
            // Atualizar o estado local
            setAtendimentos(prevAtendimentos => 
                prevAtendimentos.map(at => 
                    at.id === id ? atendimentoAtualizado : at
                )
            );
             console.log(`Atendimento ${id} iniciado.`);
        } catch (err: any) {
            console.error(`Erro ao iniciar atendimento ${id}:`, err);
            setError(err.message || `Falha ao iniciar atendimento ${id}.`);
        } finally {
            setSubmittingId(null); // Limpar estado de carregamento do botão
        }
    };

    // Filtrar atendimentos para a fila (ex: status AGUARDANDO)
    const fila = atendimentos.filter(a => a.status === 'AGUARDANDO'); // Ajustar status conforme API
    const emAndamento = atendimentos.filter(a => a.status === 'EM_ANDAMENTO'); // Ajustar status
    const historico = atendimentos.filter(a => a.status !== 'AGUARDANDO' && a.status !== 'EM_ANDAMENTO');

    return (
        <div style={{ maxWidth: '800px', margin: '50px auto', padding: '20px' }}>
            <h1>Meus Atendimentos</h1>

            {loadingAtendimentos && <p>Carregando atendimentos...</p>}
            {error && <p style={{ color: 'red' }}>{error}</p>}

            {!loadingAtendimentos && !error && (
                <>
                    {/* Seção Fila de Espera */} 
                    <section style={{ marginBottom: '30px' }}>
                        <h2>Fila de Espera ({fila.length})</h2>
                        {fila.length === 0 ? (
                            <p>Nenhum paciente aguardando no momento.</p>
                        ) : (
                            <ul style={{ listStyle: 'none', padding: 0 }}>
                                {fila.map(at => (
                                    <li key={at.id} style={{ border: '1px solid #eee', padding: '10px', marginBottom: '10px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                                        <span>Paciente: {at.pacienteNome}</span>
                                        {/* Botão "Iniciar Atendimento" */}
                                        <button 
                                            onClick={() => handleIniciarAtendimento(at.id)} 
                                            disabled={submittingId === at.id} // Desabilitar enquanto processa
                                            style={{ marginLeft: '10px', padding: '5px 10px' }}
                                        >
                                            {submittingId === at.id ? 'Iniciando...' : 'Iniciar'}
                                        </button>
                                    </li>
                                ))}
                            </ul>
                        )}
                    </section>

                    {/* Seção Em Andamento com botão Abrir Chat */} 
                    <section style={{ marginBottom: '30px' }}>
                        <h2>Em Andamento ({emAndamento.length})</h2>
                         {emAndamento.length === 0 ? (
                            <p>Nenhum atendimento em andamento.</p>
                        ) : (
                             <ul style={{ listStyle: 'none', padding: 0 }}>
                                {emAndamento.map(at => (
                                    <li key={at.id} style={{ border: '1px solid #eee', padding: '10px', marginBottom: '10px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                                        <span>Paciente: {at.pacienteNome}</span>
                                        {/* Botão "Abrir Chat" que navega para /chat/[id] */}
                                        <button 
                                            onClick={() => router.push(`/chat/${at.id}`)} 
                                            style={{ marginLeft: '10px', padding: '5px 10px' }}
                                        >
                                            Abrir Chat
                                        </button> 
                                    </li>
                                ))}
                            </ul>
                        )}
                    </section>

                    {/* Seção Histórico */} 
                     <section>
                        <h2>Histórico ({historico.length})</h2>
                          {historico.length === 0 ? (
                            <p>Nenhum atendimento no histórico.</p>
                        ) : (
                             <ul style={{ listStyle: 'none', padding: 0 }}>
                                {historico.map(at => (
                                    <li key={at.id} style={{ border: '1px solid #eee', padding: '10px', marginBottom: '10px', backgroundColor: '#f9f9f9' }}>
                                        <span>Paciente: {at.pacienteNome} - Status: {at.status}</span>
                                        {/* TODO: Adicionar botão "Ver Detalhes"? */}
                                    </li>
                                ))}
                            </ul>
                        )}
                    </section>
                </>
            )}
        </div>
    );
};

export default withAuth(AtendimentosPage); 