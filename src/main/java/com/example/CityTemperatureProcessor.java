package com.example;

import java.nio.file.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;


public class CityTemperatureProcessor {
    private Path file;

    public CityTemperatureProcessor(Path file) {
        this.file = file;
    }

    public void process() {
        Map<String, double[]> monthlyTemps = new HashMap<>();

        try (BufferedReader br = Files.newBufferedReader(file)) {
            String line;

            // Ignorar a primeira linha (cabeçalho)
            br.readLine();

            while ((line = br.readLine()) != null) {
                // Separar os dados pelo delimitador correto (vírgula neste caso)
                String[] data = line.split(",");

                // Verificar se a linha tem pelo menos 6 colunas (para evitar problemas com linhas malformadas)
                if (data.length < 6) {
                    System.err.println("Linha malformada: " + Arrays.toString(data));
                    continue;
                }

                String country = data[0];   // País (não estamos usando por enquanto)
                String city = data[1];      // Cidade (não estamos usando por enquanto)
                String month = data[2];     // Mês
                String day = data[3];       // Dia (não estamos usando para o cálculo mensal)
                String year = data[4];      // Ano
                double avgTemp;

                // Tentar converter a temperatura para double
                try {
                    avgTemp = Double.parseDouble(data[5]);  // Temperatura média
                } catch (NumberFormatException e) {
                    System.err.println("Temperatura inválida para " + Arrays.toString(data));
                    continue;  // Pular essa linha se a temperatura for inválida
                }

                // Criar uma chave para agrupar os dados por ano e mês (ex: "1995-01")
                String yearMonth = year + "-" + (month.length() == 1 ? "0" + month : month);  // Formatar o mês com dois dígitos

                // Atualizar ou inicializar as estatísticas mensais
                if (monthlyTemps.containsKey(yearMonth)) {
                    double[] temps = monthlyTemps.get(yearMonth);
                    temps[0] = Math.max(temps[0], avgTemp);  // Atualizar a temperatura máxima
                    temps[1] = Math.min(temps[1], avgTemp);  // Atualizar a temperatura mínima
                    temps[2] += avgTemp;  // Somar a temperatura média
                    temps[3] += 1;  // Contar os dias
                } else {
                    monthlyTemps.put(yearMonth, new double[]{avgTemp, avgTemp, avgTemp, 1});
                }
            }

            // Exibir as estatísticas mensais
            for (String yearMonth : monthlyTemps.keySet()) {
                double[] temps = monthlyTemps.get(yearMonth);
                double maxTemp = temps[0];
                double minTemp = temps[1];
                double avgTemp = temps[2] / temps[3];  // Média acumulada
                System.out.println("Ano-Mês: " + yearMonth + " - Máx: " + maxTemp + " - Mín: " + minTemp + " - Média: " + avgTemp);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método processByYear() para processar os dados por ano
    public void processByYear() {
        Map<String, List<String>> dataByYear = new HashMap<>();
        try (BufferedReader br = Files.newBufferedReader(file)) {
            String line;

            // Ignorar a primeira linha (cabeçalho)
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                String year = data[1].substring(0, 4);  // Pegar o ano da data (YYYY)

                // Agrupar linhas por ano
                dataByYear.computeIfAbsent(year, k -> new ArrayList<>()).add(line);
            }

            // Criar threads para processar os dados de cada ano
            ExecutorService yearExecutor = Executors.newFixedThreadPool(dataByYear.size());
            for (String year : dataByYear.keySet()) {
                yearExecutor.submit(() -> {
                    List<String> yearData = dataByYear.get(year);
                    processYearData(yearData);
                });
            }

            yearExecutor.shutdown();
            yearExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Método auxiliar para processar os dados de um ano específico
    private void processYearData(List<String> yearData) {
        for (String line : yearData) {
            String[] data = line.split(",");
            String dataTemp = data[1];
            double tempMax = Double.parseDouble(data[2]);
            double tempMin = Double.parseDouble(data[3]);
            double tempMedia = Double.parseDouble(data[4]);

            // Exibir as estatísticas do ano
            System.out.println("Ano: " + dataTemp.substring(0, 4) + " - Máx: " + tempMax + " - Mín: " + tempMin + " - Média: " + tempMedia);
        }
    }
}
