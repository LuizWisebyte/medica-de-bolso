import React, { useState, useEffect, useCallback } from 'react';
import withAuth from '../hocs/withAuth';
import { useAuth } from '../contexts/AuthContext';
import { 
    getMeuSaldo, 
    getMeuExtrato, 
    solicitarResgate,
    Saldo,
    ExtratoItem,
    ExtratoParams,
    SolicitarResgateData
} from '../services/financeiroService';
import { format } from 'date-fns'; // Para formatar datas

// Função helper para formatar moeda (simples)
const formatCurrency = (value: number | undefined | null): string => {
    if (value === undefined || value === null) return 'R$ 0,00';
    return value.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
};

const FinanceiroPage: React.FC = () => {
    const { user } = useAuth();
    const [saldo, setSaldo] = useState<Saldo | null>(null);
    const [extrato, setExtrato] = useState<ExtratoItem[]>([]);
    const [loadingSaldo, setLoadingSaldo] = useState(true);
    const [loadingExtrato, setLoadingExtrato] = useState(false); // Carrega sob demanda
    const [errorSaldo, setErrorSaldo] = useState<string | null>(null);
    const [errorExtrato, setErrorExtrato] = useState<string | null>(null);
    const [errorResgate, setErrorResgate] = useState<string | null>(null);

    // Estados para filtros de extrato
    const today = new Date();
    const firstDayOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);
    const [dataInicio, setDataInicio] = useState(format(firstDayOfMonth, 'yyyy-MM-dd'));
    const [dataFim, setDataFim] = useState(format(today, 'yyyy-MM-dd'));

    // Estados para solicitação de resgate
    const [valorResgate, setValorResgate] = useState('');
    const [isSubmittingResgate, setIsSubmittingResgate] = useState(false);
    const [resgateSuccessMessage, setResgateSuccessMessage] = useState<string | null>(null);

    // Buscar saldo inicial
    const fetchSaldo = useCallback(async () => {
        if (!user) return;
        setLoadingSaldo(true);
        setErrorSaldo(null);
        try {
            const saldoData = await getMeuSaldo();
            setSaldo(saldoData);
        } catch (err: any) {
            setErrorSaldo(err.message || 'Erro ao buscar saldo.');
        } finally {
            setLoadingSaldo(false);
        }
    }, [user]);

    useEffect(() => {
        fetchSaldo();
    }, [fetchSaldo]);

    // Buscar extrato sob demanda
    const handleBuscarExtrato = async () => {
        if (!user) return;
        setLoadingExtrato(true);
        setErrorExtrato(null);
        setExtrato([]); // Limpar extrato anterior
        try {
            // Adicionar hora/minuto/segundo para abranger o dia todo
            const params: ExtratoParams = {
                inicio: `${dataInicio}T00:00:00`, 
                fim: `${dataFim}T23:59:59`,
            };
            const extratoData = await getMeuExtrato(params);
            setExtrato(extratoData);
        } catch (err: any) {
            setErrorExtrato(err.message || 'Erro ao buscar extrato.');
        } finally {
            setLoadingExtrato(false);
        }
    };

    // Solicitar resgate
    const handleSolicitarResgate = async (e: React.FormEvent) => {
        e.preventDefault();
        const valorNum = parseFloat(valorResgate);
        if (isNaN(valorNum) || valorNum <= 0) {
            setErrorResgate('Valor inválido para resgate.');
            return;
        }
        if (saldo !== null && valorNum > saldo) {
            setErrorResgate('Valor de resgate maior que o saldo disponível.');
            return;
        }

        setIsSubmittingResgate(true);
        setErrorResgate(null);
        setResgateSuccessMessage(null);
        try {
            const data: SolicitarResgateData = { valor: valorNum };
            const response = await solicitarResgate(data);
            setResgateSuccessMessage(response.mensagem || 'Solicitação de resgate enviada com sucesso!');
            setValorResgate(''); // Limpar campo
            fetchSaldo(); // Re-buscar saldo após solicitar resgate
        } catch (err: any) {
            setErrorResgate(err.message || 'Erro ao solicitar resgate.');
        } finally {
            setIsSubmittingResgate(false);
        }
    };

    return (
        <div style={{ maxWidth: '900px', margin: '50px auto', padding: '20px' }}>
            <h1>Dashboard Financeiro</h1>

            {/* Seção Saldo e Resgate */} 
            <section style={{ marginBottom: '30px', padding: '20px', border: '1px solid #eee', borderRadius: '8px' }}>
                <h2>Saldo Disponível</h2>
                {loadingSaldo && <p>Carregando saldo...</p>}
                {errorSaldo && <p style={{ color: 'red' }}>{errorSaldo}</p>}
                {!loadingSaldo && saldo !== null && (
                    <p style={{ fontSize: '2em', fontWeight: 'bold' }}>{formatCurrency(saldo)}</p>
                )}

                <h3 style={{ marginTop: '20px' }}>Solicitar Resgate</h3>
                <form onSubmit={handleSolicitarResgate}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                        <label htmlFor="valorResgate">Valor (R$):</label>
                        <input 
                            type="number" 
                            id="valorResgate" 
                            value={valorResgate} 
                            onChange={(e) => setValorResgate(e.target.value)} 
                            step="0.01" 
                            min="0.01" 
                            required 
                            disabled={isSubmittingResgate || saldo === null || saldo <= 0}
                            style={{ padding: '8px' }}
                        />
                        <button 
                            type="submit" 
                            disabled={isSubmittingResgate || saldo === null || saldo <= 0}
                            style={{ padding: '8px 15px' }}
                        >
                            {isSubmittingResgate ? 'Solicitando...' : 'Solicitar'}
                        </button>
                    </div>
                    {errorResgate && <p style={{ color: 'red', marginTop: '5px' }}>{errorResgate}</p>}
                     {resgateSuccessMessage && <p style={{ color: 'green', marginTop: '5px' }}>{resgateSuccessMessage}</p>}
                </form>
            </section>

            {/* Seção Extrato */} 
            <section>
                <h2>Extrato</h2>
                <div style={{ marginBottom: '15px', display: 'flex', gap: '15px', alignItems: 'center' }}>
                    <div>
                        <label htmlFor="dataInicio">De:</label>
                        <input 
                            type="date" 
                            id="dataInicio" 
                            value={dataInicio} 
                            onChange={(e) => setDataInicio(e.target.value)} 
                            style={{ marginLeft: '5px', padding: '8px' }}
                        />
                    </div>
                    <div>
                        <label htmlFor="dataFim">Até:</label>
                        <input 
                            type="date" 
                            id="dataFim" 
                            value={dataFim} 
                            onChange={(e) => setDataFim(e.target.value)} 
                            style={{ marginLeft: '5px', padding: '8px' }}
                        />
                    </div>
                    <button onClick={handleBuscarExtrato} disabled={loadingExtrato} style={{ padding: '8px 15px' }}>
                         {loadingExtrato ? 'Buscando...' : 'Buscar Extrato'}
                    </button>
                </div>
                
                {loadingExtrato && <p>Carregando extrato...</p>}
                {errorExtrato && <p style={{ color: 'red' }}>{errorExtrato}</p>}
                
                {!loadingExtrato && !errorExtrato && (
                    extrato.length === 0 ? (
                        <p>Nenhuma transação encontrada para o período selecionado.</p>
                    ) : (
                        <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                            <thead>
                                <tr style={{ borderBottom: '1px solid #ccc' }}>
                                    <th style={{ textAlign: 'left', padding: '8px' }}>Data/Hora</th>
                                    <th style={{ textAlign: 'left', padding: '8px' }}>Tipo</th>
                                    <th style={{ textAlign: 'left', padding: '8px' }}>Descrição</th>
                                    <th style={{ textAlign: 'right', padding: '8px' }}>Valor</th>
                                </tr>
                            </thead>
                            <tbody>
                                {extrato.map(item => (
                                    <tr key={item.id} style={{ borderBottom: '1px solid #eee' }}>
                                        <td style={{ padding: '8px' }}>{format(new Date(item.dataHora), 'dd/MM/yyyy HH:mm')}</td>
                                        <td style={{ padding: '8px' }}>{item.tipo}</td>
                                        <td style={{ padding: '8px' }}>{item.descricao}</td>
                                        <td style={{ padding: '8px', textAlign: 'right', color: item.tipo === 'SAIDA' || item.tipo === 'RESGATE_SOLICITADO' ? 'red' : 'green' }}>
                                            {formatCurrency(item.valor)}
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    )
                )}
            </section>

        </div>
    );
};

export default withAuth(FinanceiroPage); 