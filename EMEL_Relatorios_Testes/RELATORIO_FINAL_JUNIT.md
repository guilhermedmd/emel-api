# Relatório Final de Testes Unitários (JUnit 5 / Maven Surefire)

- **Projeto:** `emel-condominios-api` (`br.edu.ifps:emel-condominios-api:0.0.1-SNAPSHOT`)
- **Comando executado:** `mvn clean test` e `mvn surefire-report:report-only`
- **Data de execução:** 27/11/2026 23:19 (-03:00)
- **Java / Maven Surefire:** JDK 25.0.2 / Maven Surefire 3.5.5 (JUnit Jupiter 6.0.3)

## Resumo Geral (JUnit)

| Métrica | Valor |
| --- | --- |
| Total de testes executados | **94** |
| Sucessos | **94 (100%)** |
| Falhas (`Failures`) | **0** |
| Erros (`Errors`) | **0** |
| Ignorados (`Skipped`) | **0** |
| Status do build | `BUILD SUCCESS` |

## Resultados por Suíte de Teste

| Classe de Teste | Testes | Sucesso | Falhas | Erros | Ignorados | Tempo (XML) |
| --- | ---: | ---: | ---: | ---: | ---: | ---: |
| `EmelCondominiosApiApplicationTests` | 1 | 1 | 0 | 0 | 0 | 0.092 s |
| `model.AreaLazerTest` | 2 | 2 | 0 | 0 | 0 | 0.027 s |
| `model.MoradorTest` | 3 | 3 | 0 | 0 | 0 | 0.021 s |
| `model.NotificacaoTest` | 2 | 2 | 0 | 0 | 0 | 0.012 s |
| `model.ReservaTest` | 13 | 13 | 0 | 0 | 0 | 0.066 s |
| `service.AreaLazerServiceTest` | 8 | 8 | 0 | 0 | 0 | 1.752 s |
| `service.ReservaServiceTest` | 65 | 65 | 0 | 0 | 0 | 0.429 s |
| **Total** | **94** | **94** | **0** | **0** | **0** | **2.399 s** |

### Detalhamento dos Grupos `@Nested`

#### `model.ReservaTest` (13 testes)
- Construtores manuais: **3/3**
- Métodos `getAreaLazerId` e `setAreaLazerId`: **5/5**
- Métodos `getMoradorId` e `setMoradorId`: **5/5**

#### `service.ReservaServiceTest` (65 testes)
- Segurança e propriedade das reservas: **6/6**
- Cobertura estrutural completa de `ReservaService`: **10/10**
- Dados obrigatórios e consulta por área de lazer: **5/5**
- Bloqueio de áreas indisponíveis: **8/8**
- Recursos inexistentes: **3/3**
- RN01 – Consistência Cronológica e Antecedência: **10/10**
- RN02 – Impedimento de Conflito de Horários: **1/1**
- RN03 – Limite de Capacidade de Convidados: **6/6**
- RN04 – Cota de Reservas Ativas por Morador: **2/2**
- RN06 – Cancelamento de Reservas: **8/8**
- Fluxo principal de criação: **6/6**

> **Observação sobre os arquivos `.txt` do Surefire:** nas suítes que utilizam `@Nested` (`ReservaTest` e `ReservaServiceTest`), o arquivo `.txt` da classe externa exibe `Tests run: 0`, enquanto os respectivos relatórios XML (`TEST-*.xml`) e o relatório HTML (`junit-report/surefire.html`) contabilizam integralmente os 13 e 65 testes executados.

## Resumo da Cobertura (JaCoCo 0.8.14)

- **Escopo analisado:** 35 classes do bundle `emel-condominios-api`
- **Instruções:** 918 / 2.576 cobertas (35,64%)
- **Branches:** 96 / 172 cobertos (55,81%)
- **Linhas:** 234 / 595 cobertas (39,33%)
- **Complexidade:** 94 / 249 coberta (37,75%)
- **Métodos:** 46 / 163 cobertos (28,22%)
- **Classes:** 11 / 35 cobertas (31,43%)

### Classes com 100% de Cobertura
- `service.ReservaService`: 633/633 instruções (100%), 84/84 branches (100%), 138/138 linhas (100%), 18/18 métodos (100%)
- `service.AreaLazerService`: 73/73 instruções (100%), 16/16 linhas (100%), 9/9 métodos (100%)
- `model.Reserva`: 108/108 instruções (100%), 12/12 branches (100%), 35/35 linhas (100%), 7/7 métodos (100%)
- `model.AreaLazer`: 21/21 instruções (100%), 9/9 linhas (100%), 2/2 métodos (100%)
- `model.Morador`: 39/39 instruções (100%), 16/16 linhas (100%), 3/3 métodos (100%)
- `model.Notificacao`: 24/24 instruções (100%), 10/10 linhas (100%), 2/2 métodos (100%)
- Exceções (`BadRequestException`, `BusinessRuleException`, `ConflictException`, `ForbiddenException`, `ResourceNotFoundException`): 4/4 instruções (100%), 2/2 linhas (100%), 1/1 método (100%) cada

## Artefatos Gerados

- **ZIP do JaCoCo:** `EMEL_Relatorios_Testes/jacoco.zip`
- **Relatório HTML do JaCoCo:** `EMEL_Relatorios_Testes/jacoco/index.html` (e `target/site/jacoco/index.html`)
- **Relatório HTML do JUnit/Surefire:** `EMEL_Relatorios_Testes/junit-report/surefire.html` (e `target/reports/surefire.html`)
- **Relatórios XML/TXT do JUnit/Surefire:** `EMEL_Relatorios_Testes/surefire-reports/` (e `target/surefire-reports/`)
