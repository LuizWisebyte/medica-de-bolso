package br.com.medicadebolso.domain.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ExtratoItemDTO {
    private String tipo; // "PAGAMENTO" ou "RESGATE"
    private LocalDateTime data; // Data do pagamento ou do resgate
    private BigDecimal valor; // Valor bruto para pagamentos, valor total para resgates
    private BigDecimal valorLiquido; // Apenas para pagamentos (após taxa)
    private String status; // Status do pagamento ou do resgate
    private String metodoPagamento; // Apenas para pagamentos (PIX/Cartão)
    private String descricao; // Descrição amigável da transação
} 