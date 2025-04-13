import React, { useState, useEffect, useCallback } from 'react';
import withAuth from '../hocs/withAuth';
import { useAuth } from '../contexts/AuthContext';
import { updateProfile, UserProfile, UpdateProfileData } from '../services/userService'; // Importar updateProfile e tipos
// TODO: Importar serviço para atualizar perfil (ex: userService.updateProfile)

const PerfilPage: React.FC = () => {
    // Obter usuário, estado de loading e função para ATUALIZAR o usuário no contexto
    // Assumindo que AuthContext expõe uma função `setUser` ou `updateAuthUser`
    // Temporariamente, vamos apenas re-buscar o usuário após update.
    const { user, loading: authLoading, logout, fetchUserProfile } = useAuth(); // Pegar fetchUserProfile
    
    const [isEditing, setIsEditing] = useState(false);
    // Inicializar formData com um objeto vazio ou default para evitar erro no primeiro render
    const [formData, setFormData] = useState<UpdateProfileData>({}); 
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState<string | null>(null);

    // Efeito para inicializar/resetar formData quando user carregar ou sair do modo de edição
    useEffect(() => {
        if (user && !isEditing) {
            // Preencher formData com dados atuais, omitindo campos não editáveis
            const { id, email, rating, ...editableData } = user;
            setFormData(editableData);
        }
        // Se entrar em modo de edição e user existir, já estará preenchido
        // Se sair do modo de edição, reseta para os dados atuais do contexto
    }, [user, isEditing]);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setIsSubmitting(true);
        setError(null);
        try {
            console.log('Enviando atualização de perfil:', formData);
            const updatedUser = await updateProfile(formData);
            console.log('Perfil atualizado com sucesso na API:', updatedUser);
            
            // Forçar re-busca do usuário no AuthContext para refletir mudanças
            if (fetchUserProfile) {
                await fetchUserProfile(); 
            }
            // TODO: Idealmente, AuthContext teria uma função setUser(updatedUser)
            // setUser(updatedUser); // Se AuthContext expuser setUser

            setIsEditing(false); // Sair do modo de edição
            setError(null); // Limpar erros

        } catch (err: any) {
            console.error('Erro ao atualizar perfil:', err);
            setError(err.message || 'Falha ao atualizar perfil.');
        } finally {
            setIsSubmitting(false);
        }
    };

    // Handler para o botão Cancelar/Editar
    const handleToggleEdit = () => {
        setIsEditing(!isEditing);
        setError(null); // Limpar erros ao alternar modo
        // O useEffect cuidará de resetar o formData se sair da edição
    };

    if (authLoading || !user) {
        // Mostra loader enquanto autenticação carrega ou se usuário for null (inesperado aqui devido ao withAuth)
        return <div>Carregando perfil...</div>;
    }

    return (
        <div style={{ maxWidth: '600px', margin: '50px auto', padding: '20px', border: '1px solid #ccc', borderRadius: '8px' }}>
            <h1>Perfil do Médico</h1>
            
            {error && <p style={{ color: 'red' }}>{error}</p>}

            <form onSubmit={handleSubmit}>
                {/* Campos do formulário - exibição ou input dependendo de isEditing */} 
                <div style={{ marginBottom: '10px' }}>
                    <label style={{ display: 'block', fontWeight: 'bold' }}>Nome:</label>
                    {isEditing ? (
                        <input 
                            type="text" 
                            name="nome" 
                            value={formData.nome || ''} 
                            onChange={handleChange} 
                            required 
                            disabled={isSubmitting} 
                            style={{ width: '100%', padding: '8px' }} />
                    ) : (
                        <span>{user.nome || 'Não informado'}</span>
                    )}
                </div>

                <div style={{ marginBottom: '10px' }}>
                    <label style={{ display: 'block', fontWeight: 'bold' }}>Email:</label>
                    {/* Email geralmente não é editável */} 
                    <span>{user.email}</span> 
                </div>
                
                <div style={{ marginBottom: '10px' }}>
                     <label style={{ display: 'block', fontWeight: 'bold' }}>CRM:</label>
                    {isEditing ? (
                        <div style={{ display: 'flex', gap: '10px' }}>
                            <input 
                                type="text" 
                                name="crmNumber" 
                                value={formData.crmNumber || ''} 
                                onChange={handleChange} 
                                placeholder="Número" 
                                disabled={isSubmitting} 
                                style={{ padding: '8px', flexGrow: 1 }} />
                             {/* TODO: Usar o select de estados criado no registro? */}
                            <input 
                                type="text" 
                                name="crmState" 
                                value={formData.crmState || ''} 
                                onChange={handleChange} 
                                placeholder="UF" 
                                maxLength={2} 
                                disabled={isSubmitting} 
                                style={{ width: '50px', padding: '8px' }} /> 
                        </div>
                     ) : (
                        <span>{user.crmNumber ? `${user.crmNumber} (${user.crmState || 'N/A'})` : 'Não informado'}</span>
                     )}
                 </div>

                {/* TODO: Adicionar campo para profilePicture (upload?) se editável */} 
                <div style={{ marginBottom: '10px' }}>
                     <label style={{ display: 'block', fontWeight: 'bold' }}>Foto de Perfil:</label>
                     <img src={user.profilePicture || '/default-avatar.png'} alt="Foto de Perfil" width={100} style={{ borderRadius: '50%', marginTop: '5px' }}/>
                     {isEditing && (
                         <input type="text" name="profilePicture" value={formData.profilePicture || ''} onChange={handleChange} disabled={isSubmitting} placeholder="URL da imagem" style={{ width: '100%', padding: '8px', marginTop: '5px' }} />
                         // Idealmente, seria um componente de upload de arquivo
                     )}
                 </div>

                 <div style={{ marginBottom: '10px' }}>
                     <label style={{ display: 'block', fontWeight: 'bold' }}>Rating:</label>
                     {/* Rating não é editável pelo médico */} 
                     <span>{user.rating ?? 'Sem avaliação'}</span> 
                 </div>

                {/* Botões de Ação */} 
                <div style={{ marginTop: '20px' }}>
                    <button type="button" onClick={handleToggleEdit} disabled={isSubmitting}>
                        {isEditing ? 'Cancelar' : 'Editar Perfil'}
                    </button>
                    {isEditing && (
                        <button type="submit" disabled={isSubmitting} style={{ marginLeft: '10px' }}>
                            {isSubmitting ? 'Salvando...' : 'Salvar Alterações'}
                        </button>
                    )}
                </div>
            </form>

             <button 
                onClick={logout} 
                style={{ marginTop: '30px', padding: '10px', cursor: 'pointer', backgroundColor: '#f44336', color: 'white', border: 'none' }}
                disabled={isSubmitting} // Desabilitar logout durante submit
            >
                Sair (Logout)
            </button>
        </div>
    );
};

export default withAuth(PerfilPage); 