import React, { useState, useEffect } from 'react';
import { motion } from 'framer-motion';
import { FiX, FiClock, FiAlertCircle } from 'react-icons/fi';

interface Medicamento {
  id?: string;
  nome: string;
  dosagem: string;
  unidadeMedida: string;
  frequencia: string;
  horarios: string[];
  dataInicio: string;
  dataTermino?: string;
}

interface MedicamentoFormProps {
  onSubmit: (medicamento: Medicamento) => void;
  onClose: () => void;
  medicamento?: Medicamento | null;
}

interface FormErrors {
  nome?: string;
  dosagem?: string;
  unidadeMedida?: string;
  frequencia?: string;
  horarios?: string;
  dataInicio?: string;
  dataTermino?: string;
}

const MedicamentoForm = ({ onSubmit, onClose, medicamento }: MedicamentoFormProps) => {
  const [formData, setFormData] = useState<Medicamento>({
    nome: '',
    dosagem: '',
    unidadeMedida: 'mg',
    frequencia: 'diária',
    horarios: [],
    dataInicio: '',
    dataTermino: '',
  });

  const [errors, setErrors] = useState<FormErrors>({});
  const [horarioInput, setHorarioInput] = useState('');

  useEffect(() => {
    if (medicamento) {
      setFormData(medicamento);
    }
  }, [medicamento]);

  const validateForm = (): boolean => {
    const newErrors: FormErrors = {};
    let isValid = true;

    if (!formData.nome.trim()) {
      newErrors.nome = 'Nome do medicamento é obrigatório';
      isValid = false;
    }

    if (!formData.dosagem.trim()) {
      newErrors.dosagem = 'Dosagem é obrigatória';
      isValid = false;
    }

    if (!formData.unidadeMedida) {
      newErrors.unidadeMedida = 'Unidade de medida é obrigatória';
      isValid = false;
    }

    if (!formData.frequencia) {
      newErrors.frequencia = 'Frequência é obrigatória';
      isValid = false;
    }

    if (formData.horarios.length === 0) {
      newErrors.horarios = 'Pelo menos um horário é obrigatório';
      isValid = false;
    }

    if (!formData.dataInicio) {
      newErrors.dataInicio = 'Data de início é obrigatória';
      isValid = false;
    } else {
      const dataInicio = new Date(formData.dataInicio);
      const hoje = new Date();
      hoje.setHours(0, 0, 0, 0);
      
      if (dataInicio < hoje) {
        newErrors.dataInicio = 'Data de início não pode ser no passado';
        isValid = false;
      }
    }

    if (formData.dataTermino) {
      const dataInicio = new Date(formData.dataInicio);
      const dataTermino = new Date(formData.dataTermino);
      
      if (dataTermino < dataInicio) {
        newErrors.dataTermino = 'Data de término deve ser posterior à data de início';
        isValid = false;
      }
    }

    setErrors(newErrors);
    return isValid;
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
    // Limpa o erro quando o usuário começa a digitar
    if (errors[name as keyof FormErrors]) {
      setErrors(prev => ({ ...prev, [name]: undefined }));
    }
  };

  const handleAddHorario = () => {
    if (!horarioInput.trim()) return;

    const horarioRegex = /^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$/;
    if (!horarioRegex.test(horarioInput)) {
      setErrors(prev => ({ ...prev, horarios: 'Formato de horário inválido. Use HH:MM' }));
      return;
    }

    setFormData(prev => ({
      ...prev,
      horarios: [...prev.horarios, horarioInput]
    }));
    setHorarioInput('');
    setErrors(prev => ({ ...prev, horarios: undefined }));
  };

  const handleRemoveHorario = (index: number) => {
    setFormData(prev => ({
      ...prev,
      horarios: prev.horarios.filter((_, i) => i !== index)
    }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (validateForm()) {
      onSubmit(formData);
    }
  };

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      exit={{ opacity: 0, y: -20 }}
      className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
    >
      <motion.div
        initial={{ scale: 0.9 }}
        animate={{ scale: 1 }}
        exit={{ scale: 0.9 }}
        className="bg-white rounded-lg p-6 w-full max-w-md"
      >
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-xl font-semibold">
            {medicamento ? 'Editar Medicamento' : 'Novo Medicamento'}
          </h2>
          <button
            onClick={onClose}
            className="text-gray-500 hover:text-gray-700"
          >
            <FiX size={24} />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Nome do Medicamento *
            </label>
            <input
              type="text"
              name="nome"
              value={formData.nome}
              onChange={handleChange}
              className={`w-full p-2 border rounded ${
                errors.nome ? 'border-red-500' : 'border-gray-300'
              }`}
              placeholder="Digite o nome do medicamento"
            />
            {errors.nome && (
              <p className="text-red-500 text-sm mt-1 flex items-center">
                <FiAlertCircle className="mr-1" /> {errors.nome}
              </p>
            )}
          </div>

          <div className="flex gap-4">
            <div className="flex-1">
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Dosagem *
              </label>
              <input
                type="text"
                name="dosagem"
                value={formData.dosagem}
                onChange={handleChange}
                className={`w-full p-2 border rounded ${
                  errors.dosagem ? 'border-red-500' : 'border-gray-300'
                }`}
                placeholder="Ex: 500"
              />
              {errors.dosagem && (
                <p className="text-red-500 text-sm mt-1 flex items-center">
                  <FiAlertCircle className="mr-1" /> {errors.dosagem}
                </p>
              )}
            </div>
            <div className="w-24">
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Unidade *
              </label>
              <select
                name="unidadeMedida"
                value={formData.unidadeMedida}
                onChange={handleChange}
                className={`w-full p-2 border rounded ${
                  errors.unidadeMedida ? 'border-red-500' : 'border-gray-300'
                }`}
              >
                <option value="mg">mg</option>
                <option value="ml">ml</option>
                <option value="g">g</option>
                <option value="un">un</option>
              </select>
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Frequência *
            </label>
            <select
              name="frequencia"
              value={formData.frequencia}
              onChange={handleChange}
              className={`w-full p-2 border rounded ${
                errors.frequencia ? 'border-red-500' : 'border-gray-300'
              }`}
            >
              <option value="diária">Diária</option>
              <option value="semanal">Semanal</option>
              <option value="mensal">Mensal</option>
              <option value="sob demanda">Sob Demanda</option>
            </select>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Horários *
            </label>
            <div className="flex gap-2">
              <input
                type="time"
                value={horarioInput}
                onChange={(e) => setHorarioInput(e.target.value)}
                className="w-full p-2 border border-gray-300 rounded"
              />
              <button
                type="button"
                onClick={handleAddHorario}
                className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
              >
                <FiClock />
              </button>
            </div>
            {errors.horarios && (
              <p className="text-red-500 text-sm mt-1 flex items-center">
                <FiAlertCircle className="mr-1" /> {errors.horarios}
              </p>
            )}
            <div className="mt-2 flex flex-wrap gap-2">
              {formData.horarios.map((horario, index) => (
                <span
                  key={index}
                  className="flex items-center gap-1 bg-gray-100 px-2 py-1 rounded"
                >
                  {horario}
                  <button
                    type="button"
                    onClick={() => handleRemoveHorario(index)}
                    className="text-gray-500 hover:text-gray-700"
                  >
                    <FiX size={16} />
                  </button>
                </span>
              ))}
            </div>
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Data de Início *
              </label>
              <input
                type="date"
                name="dataInicio"
                value={formData.dataInicio}
                onChange={handleChange}
                className={`w-full p-2 border rounded ${
                  errors.dataInicio ? 'border-red-500' : 'border-gray-300'
                }`}
              />
              {errors.dataInicio && (
                <p className="text-red-500 text-sm mt-1 flex items-center">
                  <FiAlertCircle className="mr-1" /> {errors.dataInicio}
                </p>
              )}
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Data de Término
              </label>
              <input
                type="date"
                name="dataTermino"
                value={formData.dataTermino}
                onChange={handleChange}
                className={`w-full p-2 border rounded ${
                  errors.dataTermino ? 'border-red-500' : 'border-gray-300'
                }`}
              />
              {errors.dataTermino && (
                <p className="text-red-500 text-sm mt-1 flex items-center">
                  <FiAlertCircle className="mr-1" /> {errors.dataTermino}
                </p>
              )}
            </div>
          </div>

          <div className="flex justify-end gap-4 mt-6">
            <button
              type="button"
              onClick={onClose}
              className="px-4 py-2 border border-gray-300 rounded hover:bg-gray-50"
            >
              Cancelar
            </button>
            <button
              type="submit"
              className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
            >
              {medicamento ? 'Salvar Alterações' : 'Cadastrar'}
            </button>
          </div>
        </form>
      </motion.div>
    </motion.div>
  );
};

export default MedicamentoForm; 