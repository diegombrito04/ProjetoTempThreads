# Projeto: TempThreads - Comparação de Performance com Threads

## Descrição:
Este projeto implementa um experimento para comparar a performance de processamento de dados de arquivos CSV contendo temperaturas de várias cidades ao redor do mundo. O projeto utiliza threads em diferentes configurações para medir o impacto do paralelismo no tempo de execução das tarefas de leitura e processamento de arquivos.

## Estrutura do Projeto:
- **src/main/java/com/example/**  
  - `App.java` - Classe principal que executa os experimentos com diferentes configurações de threads.
  - `CityTemperatureProcessor.java` - Classe responsável por processar os dados das temperaturas de cada cidade.
  
- **data/** - Diretório que contém os arquivos CSV com os dados de temperatura das cidades.

- **output/** - Diretório para salvar os resultados dos tempos de execução dos experimentos.

## Requisitos:
- Java 17+
- Maven 3.8+
- Git

## Instalação:
1. Clone este repositório para sua máquina local:
    ```bash
    git clone https://github.com/SEU_USUARIO/ProjetoTempThreads.git
    ```

2. Navegue até o diretório do projeto:
    ```bash
    cd ProjetoTempThreads
    ```

3. Compile o projeto usando Maven:
    ```bash
    mvn clean install
    ```

4. Certifique-se de que os arquivos CSV estão no diretório `data/`.

## Como Executar:
1. Execute o projeto diretamente pela linha de comando usando o Maven:
    ```bash
    mvn exec:java -Dexec.mainClass="com.example.App"
    ```

2. O programa irá rodar 20 versões do experimento, com 10 rodadas cada, variando o número de threads usadas no processamento de arquivos CSV.

3. Os tempos de execução para cada experimento serão salvos no diretório `output/` com nomes como `versao_1.txt`, `versao_2.txt`, etc.

## Configuração dos Experimentos:
- **Versão 1:** Processamento sequencial (sem threads).
- **Versão 2-10:** Processamento com múltiplas threads (2, 4, 8, 16, 32, 64, 80, 160, 320 threads).
- **Versão 11-20:** Processamento com threads por cidade e threads por ano.

## Exemplo de Saída:

```bash
Iniciando o Experimento 1
  Executando rodada 1
  Rodada 1 concluída em 2040ms
  ...
Tempo médio do experimento 1: 1647ms
```
## Resultados e Relatório:
Os resultados obtidos são salvos em arquivos de texto no diretório `output/`. Cada arquivo contém os tempos de execução para as 10 rodadas e o tempo médio do experimento.

O relatório final está disponível no formato PDF, contendo explicações teóricas sobre threads, análise dos resultados e gráficos comparativos.

## Contribuições:
Sinta-se à vontade para abrir issues ou pull requests caso queira contribuir com melhorias ao projeto.

## Licença:
Este projeto é licenciado sob a **MIT License**.

